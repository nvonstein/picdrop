/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.picdrop.helper.EnvHelper;
import com.picdrop.helper.JacksonObjectMerger;
import com.picdrop.helper.ObjectMerger;
import com.picdrop.service.implementation.AuthorizationService;
import com.picdrop.service.implementation.CollectionService;
import com.picdrop.service.implementation.GroupService;
import com.picdrop.service.implementation.RegisteredUserService;
import com.picdrop.service.implementation.FileResourceService;
import com.picdrop.service.implementation.UserService;
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
    }
    
    protected void bindProperties(Binder binder) {
        Names.bindProperties(binder, EnvHelper.getProperties());
    }
    
    protected void bindObjectMapper(Binder binder) {
        binder.bind(ObjectMapper.class).toInstance(new ObjectMapper());
    }
    
    protected void binObjectMerger(Binder binder) {
        binder.bind(ObjectMerger.class).to(JacksonObjectMerger.class);
    }
    
    protected void bindServices(Binder binder) {
        binder.bind(UserService.class).in(Singleton.class);
        binder.bind(GroupService.class).in(Singleton.class);
        binder.bind(FileResourceService.class).in(Singleton.class);
        binder.bind(RegisteredUserService.class).in(Singleton.class);
        binder.bind(AuthorizationService.class).in(Singleton.class);
        binder.bind(CollectionService.class).in(Singleton.class);
    }
}
