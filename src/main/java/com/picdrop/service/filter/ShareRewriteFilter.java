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

    protected Matcher getMatcher(String uri) {
        return this.pattern.matcher(uri);
    }

    protected boolean isResourceAccess(Matcher matcher) {
        return matcher.matches();
    }

    protected String getShareResourceString(Matcher matcher) {
        return matcher.group(1);
    }

    protected String getShareId(Matcher matcher) {
        return matcher.group(2);
    }

    protected String getRootResourceString(Matcher matcher) {
        return matcher.group(3);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        URI uri = requestContext.getUriInfo().getRequestUri();
        String path = uri.getPath();

        log.debug(FILTER, "Checking request path match");
        Matcher mtch = getMatcher(path);
        if (isResourceAccess(mtch)) {
            log.debug(FILTER, "Matching request detected");
            String shareId = getShareId(mtch);

            Share s = srepo.get(shareId, null);
            if (s == null) {
                requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
                return;
            }

            log.debug(FILTER, "Matching resource uri");
            ResourceReference r = s.getResource();
            if (!r.toResourceString().equals(getRootResourceString(mtch))) {
                requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
                return;
            }

            log.debug(FILTER, "Adding permissions");
            RequestContext rctx = context.get();

            log.debug(FILTER, "Is commenting allowed: {}", s.isAllowComment());
            if (s.isAllowComment()) {
                rctx.addPermission(String.format("%s/*/comment", r.toResourceString()));
            }
            log.debug(FILTER, "Is rating allowed: {}", s.isAllowRating());
            if (s.isAllowRating()) {
                rctx.addPermission(String.format("%s/*/rate", r.toResourceString()));
            }
            rctx.addPermission(String.format("%s/*/read", r.toResourceString()));

            rctx.setPrincipal(s.getOwner(false));

            // Rewrite route
            String newpath = path.replace(getShareResourceString(mtch), "");
            log.debug(FILTER, "Rewriting route from '{}' to '{}'", path, newpath);
            uri = URI.create(newpath);
            requestContext.setRequestUri(uri);
        }

    }

}
