/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.picdrop.guice.factory.CookieProviderFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.picdrop.guice.provider.CookieProvider;
import com.picdrop.model.RequestContext;
import com.picdrop.guice.provider.SessionCookieProvider;
import com.picdrop.helper.EnvHelper;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.authentication.authenticator.BasicAuthenticator;
import com.picdrop.security.authentication.authenticator.TokenAuthenticator;
import com.picdrop.service.filter.AuthenticationFilter;
import com.picdrop.service.implementation.GroupService;
import com.picdrop.service.implementation.RegisteredUserService;
import com.picdrop.service.implementation.FileResourceService;
import com.picdrop.service.implementation.UserService;
import javax.inject.Singleton;
import org.jboss.resteasy.plugins.guice.RequestScoped;

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
        binder.bind(FileResourceService.class).in(Singleton.class);
        binder.bind(RegisteredUserService.class).in(Singleton.class);

        // Session management
        binder.install(new FactoryModuleBuilder()
                .implement(CookieProvider.class, Names.named("cookie.session"), SessionCookieProvider.class)
                .build(CookieProviderFactory.class)
        );

        // Authorization
        binder.bind(AuthenticationFilter.class);

        binder.bind(RequestContext.class).in(RequestScoped.class);

        binder.bind(Authenticator.class).annotatedWith(Names.named("basic")).to(BasicAuthenticator.class);
        binder.bind(Authenticator.class).annotatedWith(Names.named("token")).to(TokenAuthenticator.class);

        // Json
        binder.bind(ObjectMapper.class).toInstance(new ObjectMapper());

        // Environment
        Names.bindProperties(binder, EnvHelper.getProperties());
    }
}
