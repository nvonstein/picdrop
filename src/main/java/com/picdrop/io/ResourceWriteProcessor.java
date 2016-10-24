/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.inject.Inject;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.model.resource.Resource;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public class ResourceWriteProcessor implements FileProcessor<Resource> {

    @Inject
    protected FileWriter writer;

    @Override
    public Resource process(Resource entity, InputStreamProvider in) throws IOException {
        String fileUri = this.writer.write(entity.getFileUri(), in.get());
        entity.setFileUri(fileUri);

        return entity;
    }

}
