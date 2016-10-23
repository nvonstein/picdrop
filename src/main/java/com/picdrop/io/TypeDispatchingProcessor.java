/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.inject.Inject;
import com.picdrop.model.resource.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author i330120
 */
public class TypeDispatchingProcessor implements EntityProcessor<Resource>{
    
    Map<String,EntityProcessor<Resource>> lookup;

    @Inject
    public TypeDispatchingProcessor(Map<String, EntityProcessor<Resource>> lookup) {
        this.lookup = Collections.unmodifiableMap(lookup);
    }

    @Override
    public Resource process(Resource entity) throws IOException {
        EntityProcessor<Resource> fc = lookup.get(entity.getType());
        if (fc == null) {
            throw new IOException("unknown file type");
        }
        
        return fc.process(entity);
    }
    
}
