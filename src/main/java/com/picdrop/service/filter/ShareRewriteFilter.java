/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.filter;

import com.google.inject.Inject;
import static com.picdrop.helper.LogHelper.*;
import com.picdrop.model.RequestContext;
import com.picdrop.model.Share;
import com.picdrop.model.resource.ResourceReference;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
@Provider
@PreMatching
public class ShareRewriteFilter implements ContainerRequestFilter {

    // group(0) - /shares/{id}/{identifier}/{id}
    // group(1) - /shares/{id}
    // group(2) - {id}
    // group(3) - /{identifier}/{id}
    protected static final String regex = "^.*(\\/shares\\/([a-zA-Z0-9]*))(\\/[a-zA-Z0-9]*\\/[a-zA-Z0-9]*)\\/?.*$";
    protected Pattern pattern;

    @Inject
    AwareRepository<String, Share, User> srepo;
    @Inject
    com.google.inject.Provider<RequestContext> context;

    Logger log = LogManager.getLogger(this.getClass());

    public ShareRewriteFilter() {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        URI uri = requestContext.getUriInfo().getRequestUri();
        String path = uri.getPath();

        log.debug(FILTER, "Checking request path match");
        Matcher mtch = this.pattern.matcher(path);
        if (mtch.matches()) {
            log.debug(FILTER, "Matching request detected");
            String shareId = mtch.group(2);

            Share s = srepo.get(shareId, null);
            if (s == null) {
                requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
                return;
            }

            log.debug(FILTER, "Matching resource uri");
            ResourceReference r = s.getResource();
            if (!r.toResourceString().equals(mtch.group(3))) {
                requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
                return;
            }

            log.debug(FILTER, "Generating delegate with permissions");
            RequestContext rctx = context.get();

            if (s.isAllowComment()) {
                rctx.addPermission(String.format("/collections/%s/*/comment", r.getId()));
            }
            if (s.isAllowRating()) {
                rctx.addPermission(String.format("/collections/%s/*/rate", r.getId()));
            }
            rctx.addPermission(String.format("%s/*/read", r.toResourceString()));

            rctx.setPrincipal(s.getOwner(false));

            // Rewrite route
            String newpath = path.replace(mtch.group(1), "");
            log.debug(FILTER, "Rewriting route from '{}' to '{}'", path, newpath);
            uri = URI.create(newpath);
            requestContext.setRequestUri(uri);
        }

    }

}
