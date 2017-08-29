/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.picdrop.exception.ApplicationExeptionMapper;
import com.picdrop.helper.EnvHelper;
import com.picdrop.service.implementation.AuthorizationService;
import com.picdrop.service.implementation.CollectionService;
import com.picdrop.service.implementation.RegisteredUserService;
import com.picdrop.service.implementation.FileResourceService;
import com.picdrop.service.implementation.ShareService;
import javax.inject.Singleton;

/**
 *
 * @author i330120
 */
public class ApplicationModule implements Module {
    
    @Override
    public void configure(Binder binder) {
        // Services
        bindServices(binder);

        // Json
        bindObjectMapper(binder);

        // Environment
        bindProperties(binder);
        
        // Static ObjectMapper
        bindStaticObjectMapper(binder);
    }
    
    protected void bindProperties(Binder binder) {
        Names.bindProperties(binder, EnvHelper.getProperties());
    }
    
    protected void bindObjectMapper(Binder binder) {
        ObjectMapper mapper = new ObjectMapper();
        
        binder.bind(ObjectMapper.class).toInstance(mapper);
        binder.bind(ObjectWriter.class).toInstance(mapper.writer());
    }
    
    protected void bindStaticObjectMapper(Binder binder) {
        binder.requestStaticInjection(ApplicationExeptionMapper.class);
    }
    
    protected void bindServices(Binder binder) {
        binder.bind(FileResourceService.class).in(Singleton.class);
        binder.bind(RegisteredUserService.class).in(Singleton.class);
        binder.bind(AuthorizationService.class).in(Singleton.class);
        binder.bind(CollectionService.class).in(Singleton.class);
        binder.bind(ShareService.class).in(Singleton.class);
    }
}
