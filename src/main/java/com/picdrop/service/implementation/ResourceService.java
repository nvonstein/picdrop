/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.guice.provider.RequestContext;
import com.picdrop.io.EntityProcessor;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.model.ProcessingState;
import com.picdrop.model.resource.Image;
import com.picdrop.model.resource.Resource;
import com.picdrop.repository.AdvancedRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

/**
 *
 * @author i330120
 */
@Path("/app/resources")
@Consumes("application/json")
@Produces("application/json")
public class ResourceService {

    AdvancedRepository<String, Resource> repo;
    AdvancedRepository<String, Image> imageRepo;
    FileProcessor<Resource> fileHandler;
    EntityProcessor<Resource> entityHandler;

    final List<String> mimeImage = Arrays.asList("image/jpeg", "image/png", "image/tiff");

    @Inject
    ServletFileUpload upload;

    @Inject
    RequestContext context;

    @Inject
    public ResourceService(
            AdvancedRepository<String, Resource> repo,
            AdvancedRepository<String, Image> imageRepo,
            @Named("filehandler") FileProcessor<Resource> fileHandler,
            @Named("entityhandler") EntityProcessor<Resource> entityHandler) {
        this.repo = repo;
        this.imageRepo = imageRepo;
        this.fileHandler = fileHandler;
        this.entityHandler = entityHandler;
    }

    @GET
    @Path("/{id}")
    public Resource getResource(@PathParam("id") String id) {
        return this.repo.get(id);
    }

    @GET
    @Path("/")
    public List<Resource> listResource() {
        return this.repo.list();
    }

    @POST
    @Path("/")
    @Consumes("multipart/form-data")
    public List<Resource> create(
            //            MultipartFormDataInput formdata, 
            @Context HttpServletRequest request) throws IOException {
        List<Resource> res = new ArrayList<>();

        List<FileItem> files = null;
        try {
            files = upload.parseRequest(request);
        } catch (FileUploadException ex) {
            throw new IllegalArgumentException("bad upload request: " + ex.getMessage(), ex); // 400
        }

        for (FileItem file : files) {
            if (!file.isFormField()) {
                Resource r = new Resource();
                r.setName(file.getName());
                r.setOwner(context.getPrincipal());
                r.setState(ProcessingState.PROCESSING);

                String mime = file.getContentType(); // TODO do content guess and dont trust client

                if (mimeImage.contains(mime)) {
                    r.setType(mime);
                }
                if (Strings.isNullOrEmpty(r.getType())) { // No legal type
                    throw new IllegalArgumentException("illegal content type"); // 400
                }

                InputStream in = file.getInputStream();
                try {
                    fileHandler.process(r, in);
                    res.add(this.repo.save(r));
                    entityHandler.process(r); // TODO exception resolving
                } finally {
                    in.close();
                }
            }
        }

//        Map<String, List<InputPart>> fieldMap = formdata.getFormDataMap();
//
//        if (fieldMap == null) {
//            throw new IllegalArgumentException("no files provided"); // 400
//        }
//
//        List<InputPart> rawFiles = fieldMap.get("files");
//        if ((rawFiles == null) || rawFiles.isEmpty()) {
//            return res; // Nothing to do
//        }
//
//        for (InputPart rf : rawFiles) {
//            InputStream is = null;
//            OutputStream os = null;
//
//            Resource r = new Resource();
//            r.setName(HttpHelper.parseFilename(rf.getHeaders())); // TODO fallback name or just generate own?
//
//            try {
//                is = rf.getBody(InputStream.class, null);
//                os = new FileOutputStream("~/" + r.getName()); // TODO think about resource handling
//                try {
//                    byte[] data = IOUtils.toByteArray(is);
//                    os.write(data);
//                } catch (IOException ex) {
//                    // cleanup?
//                } finally {
//                    is.close();
//                    os.close();
//                }
//            } catch (IOException ex) {
//                // skip
//            }
//
//            r = this.repo.save(r);
//            
//            // Determine type & dispatch
//            // case Image - process & store
//            this.imageRepo.save(r.toImage()); // TODO maybe use processor for factoring concrete resources? 
//
//            res.add(r);
//        }
        return res;
    }

    public Resource update(String id, Resource entity) {
//        try {
//            if (this.imageRepo.deleteNamed("updateChild", "images", id) != 0) {
//                super.delete(id);
//                return;
//            }
////            if ( ...  != 0) {
////                super.delete(id);
////                return;
////            }
//        } catch (IOException ex) {
//            throw new RuntimeException(ex); // TODO think about handling exception in toplevel services
//        }
        return null;
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") String id) {
        try {
            if (this.imageRepo.deleteNamed("getChild", "images", id) != 0) {
                this.repo.delete(id);
                return;
            }
//            if ( ...  != 0) {
//                super.delete(id);
//                return;
//            }
        } catch (IOException ex) {
            throw new RuntimeException(ex); // TODO think about handling exception in toplevel services
        }
    }

    @GET
    @Path("/images/{id}") // TODO overrides image service
    public Image getImage(@PathParam("id") String id) throws IOException {
        List<Image> imgs = imageRepo.queryNamed("getChild", "images", id);
        if (imgs.isEmpty()) {
            return null; //404
        }
        return imgs.get(0);
    }

}
