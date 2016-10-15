/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.filter;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.picdrop.annotations.Authorized;
import com.picdrop.guice.provider.RequestContext;
import com.picdrop.model.LoggedIn;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import com.picdrop.security.authenticator.Authenticator;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.core.ResourceMethodInvoker;

/**
 *
 * @author i330120
 */
@Provider
public class AuthorizationFilter implements ContainerRequestFilter { // TODO abstraction for making injectable

    @Inject
    @Named("basic")
    Authenticator authenticator;
    @Context
    HttpServletRequest request;
    @Inject
    com.google.inject.Provider<RequestContext> context;

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        try {
            ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) crc.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
            if (methodInvoker == null) {
                // TODO log
                crc.abortWith(Response.serverError().build());
                return;
            }

            Method method = methodInvoker.getMethod();
            Class<?> clazz = methodInvoker.getResourceClass();

            if (clazz.isAnnotationPresent(Authorized.class) || method.isAnnotationPresent(Authorized.class)) {
                RegisteredUser user = authenticator.authenticate(request);
                if (user == null) {
                    crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                    return;
                }
                context.get().setPrincipal(user);
            }
        } catch (Exception e) {
            // TODO log
            crc.abortWith(Response.serverError().build());
            return;
        }
    }

}
