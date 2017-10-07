/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.inject.Inject;
import com.picdrop.exception.ApplicationException;
import com.picdrop.guice.provider.ResourceContainer;
import com.picdrop.model.FileType;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.resource.ImageDescriptor;
import com.picdrop.model.resource.ResourceDescriptor;
import com.picdrop.repository.Repository;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public class ImageProcessor extends AbstractUpdateProcessor<FileResource> {

    
    @Inject
    public ImageProcessor(Repository<String, FileResource> repo) {
        super(repo);
    }

    @Override
    public FileResource onPostStore(FileResource entity, ResourceContainer cnt) throws IOException, ApplicationException {
        ResourceDescriptor rdes = entity.getDescriptor();
        if ((rdes != null) && (rdes.getType().isCoveredBy(FileType.IMAGE_WILDTYPE))) {
            ImageDescriptor ides = rdes.to(ImageDescriptor.class);

            entity.setDescriptor(ides);
        }
        return super.onPostStore(entity, cnt);
    }

}
