/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Inject;
import com.picdrop.helper.HttpHelper;
import com.picdrop.model.resource.Image;
import com.picdrop.model.resource.Resource;
import com.picdrop.repository.AdvancedRepository;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author i330120
 */
@Path("resources")
@Consumes("application/json")
@Produces("application/json")
public class ResourceService {

    AdvancedRepository<String, Resource> repo;
    AdvancedRepository<String, Image> imageRepo;

    @Inject
    public ResourceService(AdvancedRepository<String, Resource> repo,
            AdvancedRepository<String, Image> imageRepo) {
        this.repo = repo;
        this.imageRepo = imageRepo;
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
    public List<Resource> create(MultipartFormDataInput formdata) {
        List<Resource> res = new ArrayList<>();
        Map<String, List<InputPart>> fieldMap = formdata.getFormDataMap();

        if (fieldMap == null) {
            throw new IllegalArgumentException("no files provided"); // 400
        }

        List<InputPart> rawFiles = fieldMap.get("files");
        if ((rawFiles == null) || rawFiles.isEmpty()) {
            return res; // Nothing to do
        }

        for (InputPart rf : rawFiles) {
            InputStream is = null;
            OutputStream os = null;

            Resource r = new Resource();
            r.setName(HttpHelper.parseFilename(rf.getHeaders())); // TODO fallback name or just generate own?

            try {
                is = rf.getBody(InputStream.class, null);
                os = new FileOutputStream(""); // TODO think about resource handling
                try {
                    byte[] data = IOUtils.toByteArray(is);
                    os.write(data);
                } catch (IOException ex) {
                    // cleanup?
                } finally {
                    is.close();
                    os.close();
                }
            } catch (IOException ex) {
                // skip
            }

            r = this.repo.save(r);
            
            // Determine type & dispatch
            // case Image - process & store
            this.imageRepo.save(r.toImage()); // TODO maybe use processor for factoring concrete resources? 

            res.add(r);
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
    @Path("/images/{id}")
    public Image getImage(@PathParam("id") String id) throws IOException {
        List<Image> imgs = imageRepo.queryNamed("getChild", "images", id);
        if (imgs.isEmpty()) {
            return null; //404
        }
        return imgs.get(0);
    }

}
