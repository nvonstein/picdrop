/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.filter;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.security.authentication.authenticator.Authenticator;
import java.io.IOException;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import com.picdrop.security.authentication.Authenticated;
import com.picdrop.security.authentication.Role;
import com.picdrop.security.authentication.RoleType;
import java.util.Arrays;
import javax.annotation.Priority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author i330120
 */
@Provider
@Authenticated
@Priority(1000)
public class AuthenticationFilter implements ContainerRequestFilter { // TODO abstraction for making injectable

    Logger log = LogManager.getLogger(this.getClass());

    @Inject
    @Named("token")
    Authenticator<User> authenticator;
    @Context
    HttpServletRequest request;
    @Inject
    com.google.inject.Provider<RequestContext> context;

    protected ResourceMethodInvoker getMethodInvoker(ContainerRequestContext crc) {
        ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) crc.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
        return methodInvoker;
    }

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        log.traceEntry();
        try {
            ResourceMethodInvoker methodInvoker = getMethodInvoker(crc);

            if (methodInvoker == null) {
                log.error("Error on authentication, unable to resolve method invoker entity.");
                crc.abortWith(Response.serverError().build());
                return;
            }

            Method method = methodInvoker.getMethod();
            Class<?> clazz = methodInvoker.getResourceClass();

            Authenticated classAnnotation = clazz.getAnnotation(Authenticated.class);
            Authenticated methodAnnotation = method.getAnnotation(Authenticated.class);

            if ((classAnnotation == null) && (methodAnnotation == null)) {
                return;
            }

            User user = authenticator.authenticate(request);
            if (user == null) {
                log.debug("Unable to authenticate a user.");
                crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                return;
            }
            Role roleAnnotation = user.getClass().getAnnotation(Role.class);
            RoleType[] roles = (roleAnnotation == null) ? new RoleType[]{} : roleAnnotation.roles();

            if (classAnnotation != null) {
                if (!RoleType.resolve(roles, classAnnotation.include(), classAnnotation.exclusive())
                        || ((classAnnotation.exclude().length != 0) && RoleType.resolve(roles, classAnnotation.exclude(), false))) {
                    log.info("Unauthorized access on method. User roles: {}", Arrays.asList(roles));
                    crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                    return;
                }
            }
            if (methodAnnotation != null) {
                if (!RoleType.resolve(roles, methodAnnotation.include(), methodAnnotation.exclusive())
                        || ((methodAnnotation.exclude().length != 0) && RoleType.resolve(roles, methodAnnotation.exclude(), false))) {
                    log.info("Unauthorized access on method. User roles: {}", Arrays.asList(roles));
                    crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                    return;
                }
            }

            context.get().setPrincipal(user);
        } catch (Exception e) {
            log.error("Error on authentication.", e);
            crc.abortWith(Response.serverError().build());
            return;
        }
        log.traceExit();
    }

}
