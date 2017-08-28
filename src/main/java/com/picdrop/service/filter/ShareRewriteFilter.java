/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.filter;

import com.google.inject.Inject;
import com.picdrop.model.RequestContext;
import com.picdrop.model.Share;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.resource.ResourceReference;
import com.picdrop.model.user.RegisteredUserDelegate;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.Repository;
import java.io.IOException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

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
    protected static final String regex = "\\/*(\\/shares\\/([a-zA-Z0-9]*))(\\/[a-zA-Z0-9]*\\/[a-zA-Z0-9]*)";
    protected Pattern pattern;

    @Inject
    AwareRepository<String, Share, User> srepo;
    @Inject
    com.google.inject.Provider<RequestContext> context;

    public ShareRewriteFilter() {
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        URI uri = requestContext.getUriInfo().getRequestUri();
        String path = uri.getPath();

        Matcher mtch = this.pattern.matcher(path);
        if (mtch.matches()) {
            String shareId = mtch.group(2);

            Share s = srepo.get(shareId, null);
            if (s == null) {
                requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
                return;
            }

            ResourceReference r = s.getResource();
            if (!r.toResourceString().equals(mtch.group(3))) {
                requestContext.abortWith(Response.status(Response.Status.NOT_FOUND).build());
                return;
            }
            
            RequestContext rctx = context.get();
            rctx.setPrincipal(new RegisteredUserDelegate(s.getOwner(false)));

            // Rewrite route
            path = path.replace(mtch.group(1), "");
            uri = URI.create(path);
            requestContext.setRequestUri(uri);
        }

    }

}
