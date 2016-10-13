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
import com.picdrop.service.implementation.GroupService;
import com.picdrop.service.implementation.ImageService;
import com.picdrop.service.implementation.RegisteredUserService;
import com.picdrop.service.implementation.ResourceService;
import com.picdrop.service.implementation.UserService;
import javax.inject.Singleton;

/**
 *
 * @author i330120
 */
public class ApplicationModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(UserService.class).in(Singleton.class);
        binder.bind(GroupService.class).in(Singleton.class);
        binder.bind(ResourceService.class).in(Singleton.class);
        binder.bind(ImageService.class).in(Singleton.class);
        binder.bind(RegisteredUserService.class).in(Singleton.class);

        binder.bind(ObjectMapper.class).toInstance(new ObjectMapper());
        
        Names.bindProperties(binder, EnvHelper.getProperties());
    }

}
