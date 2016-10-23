/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.inject.Inject;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.model.resource.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author i330120
 */
public class GeneralResourceFileProcessor implements FileProcessor<Resource>{
    
    @Inject
    protected FileWriter writer;

    @Override
    public Resource process(Resource entity, InputStream in) throws IOException {
        String fileUri = this.writer.write(entity.getFileUri(), in);
        entity.setFileUri(fileUri);
        
        return entity;
    }
    
}
