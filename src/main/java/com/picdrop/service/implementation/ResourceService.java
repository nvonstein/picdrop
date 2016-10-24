/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.guice.factory.InputStreamProviderFactory;
import com.picdrop.guice.provider.RequestContext;
import com.picdrop.model.resource.Resource;
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
import com.picdrop.model.FileType;
import com.picdrop.model.resource.ResourceDescriptor;

/**
 *
 * @author i330120
 */
@Path("/app/resources")
@Consumes("application/json")
@Produces("application/json")
public class ResourceService {

    AdvancedRepository<String, Resource> repo;

    List<FileProcessor<Resource>> preStore;
    List<FileProcessor<Resource>> postStore;
    FileProcessor<Resource> writeProcessor;

    final List<String> mimeImage = Arrays.asList("image/jpeg", "image/png", "image/tiff");

    @Inject
    ServletFileUpload upload;

    @Inject
    RequestContext context;

    @Inject
    InputStreamProviderFactory instProvFac;

    @Inject
    public ResourceService(
            AdvancedRepository<String, Resource> repo,
            @Named("processors.pre") List<FileProcessor<Resource>> preStore,
            @Named("processors.post") List<FileProcessor<Resource>> postStore,
            @Named("processor.write") FileProcessor<Resource> writeProcessor) {
        this.repo = repo;
        
        this.preStore = preStore;
        this.postStore = postStore;
        this.writeProcessor = writeProcessor;
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
    public List<Resource> create(@Context HttpServletRequest request) throws IOException {
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

                String mime = file.getContentType(); // TODO do content guess and dont trust client
                
                r.setDescriptor(ResourceDescriptor.get(FileType.forName(mime)));

                // Pre store
                InputStreamProvider isp = instProvFac.create(file);
                for (FileProcessor<Resource> fp : preStore) {
                    fp.process(r, isp);
                }

                // Store
                writeProcessor.process(r, isp);
                r = this.repo.save(r);

                // Post store
                isp = instProvFac.create(r);
                for (FileProcessor<Resource> fp : postStore) {
                    fp.process(r, isp);
                }
                
                // TODO roleback?

                res.add(this.repo.update(r.getId(), r));
            }
        }

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
//        try {
//            if ( ...  != 0) {
//                super.delete(id);
//                return;
//            }
//        } catch (IOException ex) {
//            throw new RuntimeException(ex); // TODO think about handling exception in toplevel services
//        }
    }
}
