/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Inject;
import com.picdrop.model.resource.Image;
import com.picdrop.repository.AdvancedRepository;
import com.picdrop.service.CrudService;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author i330120
 */
@Path("/resources/image")
@Consumes("application/json")
@Produces("application/json")
public class ImageService extends CrudService<String, Image, AdvancedRepository<String, Image>> {

    @Inject
    public ImageService(AdvancedRepository<String, Image> repo) {
        super(repo);
    }

}
