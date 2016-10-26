/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.picdrop.annotations.Authorized;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.guice.factory.InputStreamProviderFactory;
import com.picdrop.model.RequestContext;
import com.picdrop.model.resource.FileResource;
import com.picdrop.repository.AdvancedRepository;
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
import com.picdrop.io.FileProcessor;
import com.picdrop.io.Processor;
import com.picdrop.model.FileType;
import com.picdrop.model.resource.ResourceDescriptor;
import com.picdrop.repository.Repository;
import javax.ws.rs.PUT;

/**
 *
 * @author i330120
 */
@Path("/app/resources")
@Consumes("application/json")
@Produces("application/json")
@Authorized
public class ResourceService {

    Repository<String, FileResource> repo;

    FileProcessor<FileResource> writeProcessor;
    List<Processor<FileResource>> processors;

    final List<String> mimeImage = Arrays.asList("image/jpeg", "image/png", "image/tiff");

    @Inject
    ServletFileUpload upload;

    @Inject
    Provider<RequestContext> contextProv;

    @Inject
    InputStreamProviderFactory instProvFac;

    @Inject
    public ResourceService(
            Repository<String, FileResource> repo,
            @Named("processor.write") FileProcessor<FileResource> writeProcessor,
            @Named("processors") List<Processor<FileResource>> processors) {
        this.repo = repo;

        this.writeProcessor = writeProcessor;
        this.processors = processors;
    }

    protected List<FileItem> parseRequest(HttpServletRequest request) {
        List<FileItem> files = null;
        try {
            files = upload.parseRequest(request);
        } catch (FileUploadException ex) {
            throw new IllegalArgumentException("bad upload request: " + ex.getMessage(), ex); // 400
        }
        return files;
    }

    protected FileResource processCreateUpdate(FileResource e, FileItem file) throws IOException {
        FileResource loce = e;
        // Pre store
        InputStreamProvider isp = instProvFac.create(file);
        for (Processor<FileResource> p : processors) {
            loce = p.onPreStore(loce, isp);
        }

        // Store
        writeProcessor.process(loce, isp);
        if (Strings.isNullOrEmpty(loce.getId())) {
            loce = this.repo.save(loce);
        } else {
            loce = this.repo.update(loce.getId(), loce);
        }

        // Post store
        isp = instProvFac.create(loce);
        for (Processor<FileResource> p : processors) {
            loce = p.onPostStore(loce, isp);
        }

        return loce;
    }

    protected void processDelete(FileResource e) throws IOException {
        for (Processor<FileResource> p : processors) {
            p.onPreDelete(e);
        }
        
        // TODO remove file?
        this.repo.delete(e.getId());
        
        for (Processor<FileResource> p : processors) {
            p.onPostDelete(e);
        }
    }

    @GET
    @Path("/{id}")
    public FileResource getResource(@PathParam("id") String id) {
        return this.repo.get(id);
    }

    @GET
    @Path("/")
    public List<FileResource> listResource() {
        return this.repo.list();
    }

    @POST
    @Path("/")
    @Consumes("multipart/form-data")
    public List<FileResource> create(@Context HttpServletRequest request) throws IOException {
        List<FileResource> res = new ArrayList<>();

        List<FileItem> files = parseRequest(request);

        for (FileItem file : files) {
            if (!file.isFormField()) {
                FileResource r = new FileResource();
                r.setName(file.getName());
                r.setOwner(contextProv.get().getPrincipal());

                String mime = file.getContentType(); // TODO do content guess and dont trust client

                r.setDescriptor(ResourceDescriptor.get(FileType.forName(mime)));

                res.add(processCreateUpdate(r, file));
            }
        }

        return res;
    }

    @PUT
    @Path("/{id}")
    @Consumes("multipart/form-data")
    public FileResource update(@PathParam("id") String id, @Context HttpServletRequest request) throws IOException {
        FileResource r = getResource(id);
        if (r == null) {
            return null; // 404
        }

        List<FileItem> files = parseRequest(request);

        for (FileItem file : files) {
            if (!file.isFormField()) {
                String mime = file.getContentType(); // TODO do content guess and dont trust client

                r.setDescriptor(ResourceDescriptor.get(FileType.forName(mime)));

                r = processCreateUpdate(r, file);
            }
        }

        return r;
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") String id) throws IOException {
        FileResource r = getResource(id);
        if (r != null) {
            processDelete(r);
        }
    }
}
