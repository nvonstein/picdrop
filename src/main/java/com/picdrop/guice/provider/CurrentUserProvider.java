/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Provider;
import com.picdrop.model.user.RegisteredUser;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import org.jboss.resteasy.plugins.guice.RequestScoped;
import org.jboss.resteasy.spi.HttpRequest;

/**
 *
 * @author i330120
 */
@RequestScoped
public class CurrentUserProvider implements Provider<RegisteredUser> {
    
    @Context  ContainerRequestContext context;
    @Context HttpRequest request;

    @Override
    public RegisteredUser get() {
        Object obj = context.getProperty("user");
        try {
            return (obj == null) ? null : (RegisteredUser) obj;
        } catch (Exception e) {
            return null;
        }
    }
    
}
