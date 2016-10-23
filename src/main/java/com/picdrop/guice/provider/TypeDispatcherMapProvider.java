/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.picdrop.io.EntityProcessor;
import com.picdrop.io.ImageProcessor;
import com.picdrop.model.resource.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author i330120
 */
public class TypeDispatcherMapProvider implements Provider<Map<String, EntityProcessor<Resource>>> {

    EntityProcessor iCon;

    @Inject
    public TypeDispatcherMapProvider(ImageProcessor icon) {
        this.iCon = icon;
    }

    @Override
    public Map<String, EntityProcessor<Resource>> get() {
        Map<String, EntityProcessor<Resource>> consumers = new HashMap<>();
        consumers.put("image/jpeg", iCon);
        consumers.put("image/png", iCon);
        consumers.put("image/tiff", iCon);
        
        return consumers;
    }

}
