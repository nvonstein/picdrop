/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.nimbusds.jwt.JWTClaimsSet;
import com.picdrop.guice.factory.CookieProviderFactory;
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.token.WebTokenFactory;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.joda.time.DateTime;

/**
 *
 * @author i330120
 */
@Path("/app")
public class AuthorizationService {

    Repository<String, RegisteredUser> userRepo;

    CookieProviderFactory cookieProvFactory;
    WebTokenFactory tokenFactory;

    Authenticator authenticator;

    @com.google.inject.Inject
    Provider<RequestContext> contextProv;

    final int configJwtExpiry;
    final String configJwtIssuer;

    @Inject
    public AuthorizationService(
            Repository<String, RegisteredUser> userRepo,
            @Named("basic") Authenticator authenticator,
            CookieProviderFactory cookieProvFactory,
            WebTokenFactory tokenFactory,
            @Named("service.session.jwt.exp") int jwtExpiry,
            @Named("service.session.jwt.iss") String jwtIssuer) {
        this.userRepo = userRepo;
        this.cookieProvFactory = cookieProvFactory;
        this.tokenFactory = tokenFactory;
        this.authenticator = authenticator;
        this.configJwtExpiry = jwtExpiry;
        this.configJwtIssuer = jwtIssuer;
    }

    @POST
    @Path("/login")
    public Response loginUser(@Context HttpServletRequest request) { // TODO make redirect target injectable
        RegisteredUser user = authenticator.authenticate(request);
        if (user == null) {
            return Response.noContent().status(Status.FORBIDDEN).build();
        }

        // generate JWT with logged in id and build cookie
        DateTime now = DateTime.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .audience("picdrop")
                .issueTime(now.toDate())
                .expirationTime(now.plusMinutes(configJwtExpiry).toDate())
                .issuer(configJwtIssuer)
                .subject(user.getId())
                .build();

        try {
            String token = tokenFactory.getToken(claims);

            user.setLastLogin();
            userRepo.update(user.getId(), user);

            NewCookie c = cookieProvFactory.getSessionCookieProvider(token).get();

            return Response.ok(token).cookie(c).build(); // TODO post-redirect-get
        } catch (IOException ex) {
            // TODO log
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/logout")
    public Response logoutUser() { // TODO rework login/logout
        User user = contextProv.get().getPrincipal();
        if (user == null) {
            return Response.ok().build(); // TODO ???
        }

        // generate kill cookie
        NewCookie c = cookieProvFactory.getSessionCookieProvider("").get();
        NewCookie killcookie = new NewCookie(c, c.getComment(), 0, c.isSecure());

        return Response.seeOther(null).cookie(killcookie).build(); // Redirect dahsboard
    }
}
