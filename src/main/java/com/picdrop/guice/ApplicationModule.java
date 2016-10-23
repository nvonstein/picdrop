/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.picdrop.guice.provider.CookieProviderFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.picdrop.guice.provider.CookieProvider;
import com.picdrop.guice.provider.FileItemFactoryProvider;
import com.picdrop.guice.provider.RequestContext;
import com.picdrop.guice.provider.SessionCookieProvider;
import com.picdrop.guice.provider.TypeDispatcherMapProvider;
import com.picdrop.guice.provider.UploadHandlerProvider;
import com.picdrop.helper.EnvHelper;
import com.picdrop.io.EntityProcessor;
import com.picdrop.io.ImageProcessor;
import com.picdrop.io.TypeDispatchingProcessor;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.io.writer.MurmurFileWriter;
import com.picdrop.model.resource.Resource;
import com.picdrop.security.authenticator.Authenticator;
import com.picdrop.security.authenticator.BasicAuthenticator;
import com.picdrop.security.authenticator.TokenAuthenticator;
import com.picdrop.service.filter.AuthorizationFilter;
import com.picdrop.service.implementation.GroupService;
import com.picdrop.service.implementation.ImageService;
import com.picdrop.service.implementation.RegisteredUserService;
import com.picdrop.service.implementation.ResourceService;
import com.picdrop.service.implementation.UserService;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.jboss.resteasy.plugins.guice.RequestScoped;
import com.picdrop.io.FileProcessor;
import com.picdrop.io.GeneralResourceFileProcessor;

/**
 *
 * @author i330120
 */
public class ApplicationModule implements Module {

    @Override
    public void configure(Binder binder) {
        // Services
        binder.bind(UserService.class).in(Singleton.class);
        binder.bind(GroupService.class).in(Singleton.class);
        binder.bind(ResourceService.class).in(Singleton.class);
        binder.bind(ImageService.class).in(Singleton.class);
        binder.bind(RegisteredUserService.class).in(Singleton.class);

        // Session management
        binder.install(new FactoryModuleBuilder()
                .implement(CookieProvider.class, Names.named("cookie.session"), SessionCookieProvider.class)
                .build(CookieProviderFactory.class)
        );

        // Upload handeling
        binder.bind(FileItemFactory.class).toProvider(FileItemFactoryProvider.class).asEagerSingleton();
        binder.bind(ServletFileUpload.class).toProvider(UploadHandlerProvider.class);

        // File writing
        binder.bind(FileWriter.class).to(MurmurFileWriter.class);
        binder.bind(new TypeLiteral<FileProcessor<Resource>>() {
        }).annotatedWith(Names.named("filehandler")).to(GeneralResourceFileProcessor.class);
        
        // Resource type dispatching
        binder.bind(new TypeLiteral<EntityProcessor<Resource>>() {
        }).annotatedWith(Names.named("entityhandler")).to(TypeDispatchingProcessor.class);
        
        
        binder.bind(new TypeLiteral<Map<String, EntityProcessor<Resource>>>() {
        }).toProvider(TypeDispatcherMapProvider.class);
        
        // Authorization
        binder.bind(AuthorizationFilter.class);

        binder.bind(RequestContext.class).in(RequestScoped.class);

        binder.bind(Authenticator.class).annotatedWith(Names.named("basic")).to(BasicAuthenticator.class);
        binder.bind(Authenticator.class).annotatedWith(Names.named("token")).to(TokenAuthenticator.class);

        // Json
        binder.bind(ObjectMapper.class).toInstance(new ObjectMapper());

        // Environment
        Names.bindProperties(binder, EnvHelper.getProperties());
    }

}
