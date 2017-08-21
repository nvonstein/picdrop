/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import com.picdrop.model.resource.FileResource;
import com.picdrop.model.resource.ImageDescriptor;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.resource.ResourceDescriptor;
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public interface ObjectMerger {
    public <T extends Resource> Resource merge(Resource defaults, T update) throws IOException;
    public <T extends FileResource> FileResource merge(FileResource defaults, T update) throws IOException;
    public <T extends ResourceDescriptor> ResourceDescriptor merge(ResourceDescriptor defaults, T update) throws IOException;
    public <T extends ImageDescriptor> ImageDescriptor merge(ImageDescriptor defaults, T update) throws IOException;
}
