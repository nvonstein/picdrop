/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.filter;

import com.google.inject.Inject;
import com.picdrop.guice.names.AuthorizationToken;
import static com.picdrop.helper.LogHelper.*;
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.security.authentication.Permission;
import com.picdrop.security.authentication.PermissionResolver;
import com.picdrop.security.authentication.authenticator.Authenticator;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.resteasy.core.ResourceMethodInvoker;

/**
 *
 * @author nvonstein
 */
@Provider
@Permission
@Priority(1000)
public class PermissionAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    PermissionResolver solver;

    Logger log = LogManager.getLogger(this.getClass());

    @Inject
    @AuthorizationToken
    Authenticator<RegisteredUser> authenticator;
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
            log.debug(FILTER, "Resolving service method");
            ResourceMethodInvoker methodInvoker = getMethodInvoker(crc);

            if (methodInvoker == null) {
                log.error(FILTER, "Error on authentication, unable to resolve method invoker entity.");
                crc.abortWith(Response.serverError().build());
                return;
            }

            Method method = methodInvoker.getMethod();
            Class<?> clazz = methodInvoker.getResourceClass();

            Permission classAnnotation = clazz.getAnnotation(Permission.class);
            Permission methodAnnotation = method.getAnnotation(Permission.class);

            RequestContext rctx = this.context.get();
            User user;
            if (rctx.hasPrincipal()) {
                user = rctx.getPrincipal();
            } else {
                log.debug(FILTER, "Authenticating User");
                user = authenticator.authenticate(request);
                if (user == null) {
                    log.debug(FILTER, "Unable to authenticate a user.");
                    crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                    return;
                }
                user.setPermissions(
                        Arrays.asList(
                                "*/logout",
                                "/resources/*/read",
                                "/resources/*/write",
                                "/collections/*/read",
                                "/collections/*/write",
                                "/collections/*/comment",
                                "/collections/*/rate",
                                "/shares/*/read",
                                "/shares/*/write",
                                "/users/*/read",
                                "/users/*/write"));
                rctx.setPrincipal(user);
            }

            String action = getAction(classAnnotation, methodAnnotation);
            String req = parsePermissionString(crc.getUriInfo().getPath(), action);
            log.debug(FILTER, "Checking required permission {}", req);
            if (!resolvePermission(req, user.getPermissions())) {
                log.debug(FILTER, "User not authorized. Required permission: {}", req);
                crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
                return;
            }
        } catch (Exception e) {
            log.error(FILTER, "Error on authentication.", e);
            crc.abortWith(Response.serverError().build());
            return;
        }

        log.traceExit();
    }

    protected String getAction(Permission clazz, Permission method) {
        String action = "";
        if (clazz != null) {
            action = clazz.value();
        }
        if (method != null) {
            action = method.value();
        }
        return action;
    }

    protected String parsePermissionString(String path, String action) {
        path = path.replace("/app", ""); // TODO remove?
        return String.format("%s/*/%s", path, action);
    }

    protected boolean resolvePermission(String req, List<String> ac) {
        for (String s : ac) {
            if (this.solver.resolve(req, s)) {
                return true;
            }
        }
        return false;
    }
}
