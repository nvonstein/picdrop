/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.inject.Inject;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.model.FileType;
import com.picdrop.model.resource.ImageDescriptor;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.resource.ResourceDescriptor;
import com.picdrop.repository.Repository;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public class ImageProcessor extends AbstractUpdateProcessor<Resource> {

    @Inject
    public ImageProcessor(Repository<String, Resource> repo) {
        super(repo);
    }

    @Override
    public Resource onPostStore(Resource entity, InputStreamProvider in) throws IOException {
        ResourceDescriptor rdes = entity.getDescriptor();
        if ((rdes != null) && (rdes.getType().isCoveredBy(FileType.IMAGE_WILDTYPE))) {
            ImageDescriptor ides = rdes.to(ImageDescriptor.class);

            // calc properties
            ides.addThumbnailUri("small", "dummy");
            ides.addThumbnailUri("medium", "dummy2");

            entity.setDescriptor(ides);
        }
        return super.onPostStore(entity, in);
    }

}
