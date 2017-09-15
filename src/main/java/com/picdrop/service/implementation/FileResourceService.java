/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.picdrop.exception.ApplicationException;
import com.picdrop.exception.ErrorMessageCode;
import com.picdrop.guice.names.File;
import com.picdrop.guice.provider.FileRepositoryProvider;
import com.picdrop.model.RequestContext;
import com.picdrop.model.resource.FileResource;
import static com.picdrop.helper.LogHelper.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.picdrop.io.Processor;
import com.picdrop.model.FileType;
import com.picdrop.model.resource.ResourceDescriptor;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import javax.ws.rs.PUT;
import com.picdrop.io.FileRepository;
import com.picdrop.model.Share;
import com.picdrop.model.ShareReference;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import com.picdrop.security.authentication.Permission;
import java.io.InputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import com.picdrop.guice.factory.ResourceContainerFactory;
import com.picdrop.guice.provider.ResourceContainer;

/**
 *
 * @author i330120
 */
@Path("/app/resources")
@Consumes("application/json")
@Produces("application/json")
public class FileResourceService {

    Logger log = LogManager.getLogger(this.getClass());

    Repository<String, FileResource> repo;
    AwareRepository<String, Share, User> srepo;
    Repository<String, Collection.CollectionItem> cirepo;
    Repository<String, Collection> crepo;

    FileRepository<String> fileRepo;
    List<Processor<FileResource>> processors;

    final List<String> mimeImage = Arrays.asList("image/jpeg", "image/png", "image/tiff");

    @Inject
    ServletFileUpload upload;

    @Inject
    Provider<RequestContext> contextProv;

    @Inject
    ResourceContainerFactory instProvFac;

    Tika tika;

    @Inject
    public FileResourceService(
            Repository<String, FileResource> repo,
            AwareRepository<String, Share, User> srepo,
            Repository<String, Collection.CollectionItem> cirepo,
            Repository<String, Collection> crepo,
            @File FileRepositoryProvider fileRepoProv,
            @File List<Processor<FileResource>> processors) {
        this.repo = repo;
        this.srepo = srepo;
        this.cirepo = cirepo;
        this.crepo = crepo;

        try {
            this.fileRepo = fileRepoProv.get();
        } catch (IOException ex) {
            log.fatal("Unable to initialize file repository", ex);
        }
        this.processors = processors;
        log.trace(SERVICE, "created with ({},{},{},{},{},{})", repo, srepo, cirepo, crepo, fileRepo, processors);
    }

    public Tika getTika() {
        return tika;
    }

    @Inject
    public void setTika(Tika tika) {
        this.tika = tika;
    }

    protected List<FileItem> parseRequest(HttpServletRequest request) throws FileUploadException {
        List<FileItem> files = null;

        files = upload.parseRequest(request);

        return files;
    }

    protected FileType parseMimeType(FileItem file) throws ApplicationException, IOException {
        FileType mime = FileType.forName(file.getContentType());

        if (mime.isUnknown()) {
            throw new ApplicationException()
                    .status(400)
                    .devMessage(String.format("Invalid mime type detected '%s'", file.getContentType()))
                    .code(ErrorMessageCode.BAD_UPLOAD_MIME);
        }

        Metadata mdata = new Metadata();
        InputStream in = file.getInputStream();
        String tikaMime = null;
        try {
            tikaMime = tika.detect(in, mdata);
        } finally {
            in.close();
        }

        log.debug(SERVICE, "Mime types resolved. HTTP Header: '{}' Tika: '{}'", file.getContentType(), tikaMime);
        if (!FileType.forName(tikaMime).isCovering(mime)) {
            throw new ApplicationException()
                    .status(400)
                    .devMessage(String.format("Mime type mismatch, expected: '%s' detected: '%s'", file.getContentType(), tikaMime))
                    .code(ErrorMessageCode.BAD_UPLOAD_MIME);
        }
        return mime;
    }

    protected FileResource processCreateUpdate(FileResource e, FileItem file) throws ApplicationException {
        log.entry(e);
        FileResource loce = e;
        String fileId;
        // Pre store
        log.debug(SERVICE, "Pre-Store: Processing file");
        ResourceContainer cnt = instProvFac.create(file);
        try {
            for (Processor<FileResource> p : processors) {
                loce = p.onPreStore(loce, cnt);
            }
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(500)
                    .code(ErrorMessageCode.ERROR_UPLOAD)
                    .devMessage("Error while pre-store phase: " + ex.getMessage());
        }

        // Store
        try {
            fileId = fileRepo.write(loce.getFileId(), cnt);
            loce.setFileId(fileId);
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(500)
                    .code(ErrorMessageCode.ERROR_UPLOAD)
                    .devMessage("Error while store phase: " + ex.getMessage());
        }
        if (Strings.isNullOrEmpty(loce.getId())) {
            loce = this.repo.save(loce);
        } else {
            loce = this.repo.update(loce.getId(), loce);
        }

        // Post store
        log.debug(SERVICE, "Post-Store: Processing file");
        cnt = instProvFac.create(loce);
        try {
            for (Processor<FileResource> p : processors) {
                loce = p.onPostStore(loce, cnt);
            }
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(500)
                    .code(ErrorMessageCode.ERROR_UPLOAD)
                    .devMessage("Error while post-store phase: " + ex.getMessage());
        }

        log.traceExit(loce);
        return loce;
    }

    protected void processDelete(FileResource e) throws ApplicationException {
        log.entry(e);
        boolean res = false;

        // Delete citems referring this res
        log.debug(SERVICE, "Pre-Delete: Retrieving collection items");
        List<Collection.CollectionItem> cis;
        try {
            cis = this.cirepo.queryNamed("citems.with.resource", e.getId());
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .code(ErrorMessageCode.ERROR_DELETE)
                    .status(500)
                    .devMessage("Error while querying citems: " + ex.getMessage());
        }
        log.debug(SERVICE, "Pre-Delete: Deleting collection items");
        for (Collection.CollectionItem ci : cis) {
            this.cirepo.delete(ci.getId());
            Collection c = ci.getParentCollection().resolve(false);
            c.removeItem(ci);
            this.crepo.update(c.getId(), c);
        }

        // Deleting Shares referring this res
        log.debug(SERVICE, "Pre-Delete: Deleting shares");
        for (ShareReference sref : e.getShares()) {
            this.srepo.delete(sref.getId());
        }
        e.setShares(new ArrayList<>());

        // Pre process
        log.debug(SERVICE, "Pre-Delete: Processing file");
        try {
            for (Processor<FileResource> p : processors) {
                p.onPreDelete(e);
            }
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .code(ErrorMessageCode.ERROR_DELETE)
                    .status(500)
                    .devMessage("Error while pre-delete phase: " + ex.getMessage());
        }

        // Delete in DB
        res = this.repo.delete(e.getId());
        if (!res) {
            throw new ApplicationException()
                    .code(ErrorMessageCode.ERROR_DELETE)
                    .status(500)
                    .devMessage("Repository returned 'false'");
        }

        // Delete file
        try {
            res = fileRepo.delete(e.getFileId());
        } catch (IOException ex) {
            // Roleback
            this.repo.save(e);
            throw new ApplicationException(ex)
                    .code(ErrorMessageCode.ERROR_DELETE)
                    .status(500)
                    .devMessage("Error while delete file phase: " + ex.getMessage());
        }
        if (!res) {
            // Roleback
            this.repo.save(e);
            throw new ApplicationException()
                    .code(ErrorMessageCode.ERROR_DELETE)
                    .status(500)
                    .devMessage("File repository returned 'false'");
        }

        // Post process
        log.debug(SERVICE, "Post-Delete: Processing file");
        try {
            for (Processor<FileResource> p : processors) {
                p.onPostDelete(e);
            }
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .code(ErrorMessageCode.ERROR_DELETE)
                    .status(500)
                    .devMessage("Error while post-delete phase: " + ex.getMessage());
        }
        log.traceExit();
    }

    @GET
    @Path("/{id}")
    @Permission("read")
    public FileResource getResource(@PathParam("id") String id) throws ApplicationException {
        log.entry(id);
        FileResource fr = this.repo.get(id);
        if (fr == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        log.info(SERVICE, "FileResource found");
        log.traceExit(fr);
        return fr;
    }

    @GET
    @Path("/")
    @Permission("read")
    public List<FileResource> listResource() {
        return this.repo.list();
    }

    @POST
    @Path("/")
    @Permission("write")
    @Consumes("multipart/form-data")
    public List<FileResource> create(@Context HttpServletRequest request) throws ApplicationException {
        log.traceEntry();
        List<FileResource> res = new ArrayList<>();
        List<FileItem> files;

        log.debug(SERVICE, "Parsing multipart request");
        try {
            files = parseRequest(request);
        } catch (FileUploadException ex) {
            throw new ApplicationException(ex)
                    .status(400)
                    .devMessage(ex.getMessage())
                    .code(ErrorMessageCode.BAD_UPLOAD);
        }

        log.debug(SERVICE, "Processing file items");
        for (FileItem file : files) {
            if (!file.isFormField()) {
                FileResource r = new FileResource();
                r.setName(file.getName());
                r.setOwner(contextProv.get().getPrincipal().to(RegisteredUser.class));

                try {
                    log.debug(SERVICE, "Validating mime type");
                    FileType mime = parseMimeType(file);

                    r.setDescriptor(ResourceDescriptor.get(mime));

                    res.add(processCreateUpdate(r, file));
                } catch (IOException ex) {
                    throw new ApplicationException(ex)
                            .status(500)
                            .devMessage(ex.getMessage())
                            .code(ErrorMessageCode.ERROR_INTERNAL);
                }
            }
        }
        log.info(SERVICE, "FileResource created");
        log.traceExit(res);
        return res;
    }

    @PUT
    @Path("/{id}")
    @Permission("write")
    public FileResource update(
            @PathParam("id") String id,
            FileResource entity) throws ApplicationException {
        log.entry(id, entity);
        if (entity == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_REQUEST_BODY);
        }

        FileResource r = getResource(id);
        if (r == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }

        log.debug(SERVICE, "Performing object merge");
        try {
            r = r.merge(entity);
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(500)
                    .code(ErrorMessageCode.ERROR_OBJ_MERGE)
                    .devMessage(ex.getMessage());
        }
        log.info(SERVICE, "FileResource updated");
        return log.traceExit(repo.update(id, r));
    }

    @PUT
    @Path("/{id}")
    @Permission("write")
    @Consumes("multipart/form-data")
    public FileResource updateFile(@PathParam("id") String id, @Context HttpServletRequest request) throws ApplicationException {
        log.entry(id);
        FileResource r = getResource(id);
        List<FileItem> files = null;
        if (r == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }

        log.debug(SERVICE, "Parsing multipart request");
        try {
            files = parseRequest(request);
        } catch (FileUploadException ex) {
            throw new ApplicationException(ex)
                    .status(400)
                    .devMessage(ex.getMessage())
                    .code(ErrorMessageCode.BAD_UPLOAD);
        }

        log.debug(SERVICE, "Processing file items");
        for (FileItem file : files) {
            if (!file.isFormField()) {
                try {
                    log.debug(SERVICE, "Validating mime type");
                    FileType mime = parseMimeType(file);

                    r.setDescriptor(ResourceDescriptor.get(mime));

                    r = processCreateUpdate(r, file);
                } catch (IOException ex) {
                    throw new ApplicationException(ex)
                            .status(500)
                            .devMessage(ex.getMessage())
                            .code(ErrorMessageCode.ERROR_INTERNAL);
                }
            }
        }

        log.info(SERVICE, "FileResource updated");
        log.traceExit(r);
        return r;
    }

    @DELETE
    @Permission("write")
    @Path("/{id}")
    public void delete(@PathParam("id") String id) throws ApplicationException {
        log.entry(id);
        FileResource r = getResource(id);
        if (r != null) {
            processDelete(r);
        } else {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        log.info(SERVICE, "FileResource deleted");
        log.traceExit();
    }
}
