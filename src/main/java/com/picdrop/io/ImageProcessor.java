/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.model.FileType;
import com.picdrop.model.resource.ImageDescriptor;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.resource.ResourceDescriptor;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public class ImageProcessor implements FileProcessor<Resource> {

    @Override
    public Resource process(Resource entity, InputStreamProvider in) throws IOException {
        ResourceDescriptor rdes = entity.getDescriptor();
        if ((rdes != null) && (rdes.getType().isCoveredBy(FileType.IMAGE_WILDTYPE))) {
            ImageDescriptor ides = rdes.to(ImageDescriptor.class);

            // calc properties
            entity.setDescriptor(ides);
        }

        return entity;
    }

}
