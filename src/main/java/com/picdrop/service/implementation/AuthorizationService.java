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
import com.picdrop.model.TokenSet;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.token.ClaimSetFactory;
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
import org.joda.time.DateTimeZone;

/**
 *
 * @author i330120
 */
@Path("/app")
public class AuthorizationService {

    Repository<String, RegisteredUser> userRepo;
    Repository<String, TokenSet> tsRepo;

    CookieProviderFactory cookieProvFactory;
    WebTokenFactory tokenFactory;

    Authenticator<RegisteredUser> authenticator;

    ClaimSetFactory<User> authCsFact;
    ClaimSetFactory<User> refreshCsFact;

    @com.google.inject.Inject
    Provider<RequestContext> contextProv;

    final boolean cookieEnabled;
    final int tsExpiry;

    @Inject
    public AuthorizationService(
            Repository<String, RegisteredUser> userRepo,
            Repository<String, TokenSet> tsRepo,
            CookieProviderFactory cookieProvFactory,
            WebTokenFactory tokenFactory,
            @Named("authenticator.basic") Authenticator<RegisteredUser> authenticator,
            @Named("claimset.factory.auth") ClaimSetFactory<User> authCsFact,
            @Named("claimset.factory.refresh") ClaimSetFactory<User> refreshCsFact,
            @Named("service.session.cookie.enabled") boolean cookieEnabled,
            @Named("service.session.jwt.refresh.exp") int tsExpiry) {
        this.tsRepo = tsRepo;
        this.userRepo = userRepo;
        this.cookieProvFactory = cookieProvFactory;
        this.tokenFactory = tokenFactory;
        this.authCsFact = authCsFact;
        this.refreshCsFact = refreshCsFact;
        this.authenticator = authenticator;
        this.cookieEnabled = cookieEnabled;
        this.tsExpiry = tsExpiry;
    }

    @POST
    @Path("/login")
    public Response loginUser(@Context HttpServletRequest request) { // TODO make redirect target injectable
        RegisteredUser user = authenticator.authenticate(request);
        if (user == null) {
            return Response.noContent().status(Status.FORBIDDEN).build();
        }

        JWTClaimsSet authClaims = this.authCsFact.builder()
                .subject(user.getId())
                .build();
        
        JWTClaimsSet refreshClaims = this.refreshCsFact.builder()
                .subject(user.getId())
                .build();

        TokenSet ts = new TokenSet();
        ts.setAuthJti(authClaims.getJWTID());
        ts.setRefreshJti(refreshClaims.getJWTID());
        ts.setOwner(user);
        ts.setExpireAt(DateTime.now(DateTimeZone.UTC)
                .plusMinutes(tsExpiry)
                .toDate());

        ts = this.tsRepo.save(ts);

        try {
            String token = tokenFactory.getToken(authClaims);

            user.setLastLogin();
            user.addToken(ts);
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
