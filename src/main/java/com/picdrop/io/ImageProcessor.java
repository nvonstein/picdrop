/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.inject.Inject;
import com.picdrop.model.resource.Image;
import com.picdrop.model.resource.Resource;
import com.picdrop.repository.AdvancedRepository;
import com.picdrop.repository.Repository;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public class ImageProcessor implements EntityProcessor<Resource> {

    Repository<String, Image> repo;

    @Inject
    public ImageProcessor(AdvancedRepository<String, Image> repo) {
        this.repo = repo;
    }

    @Override
    public Resource process(Resource entity) throws IOException {
        Image i = entity.toImage();
        
        repo.save(i);

        // TODO generate thumbnails etc.
        
        return entity;
    }

}
