/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.nimbusds.jwt.JWTClaimsSet;
import com.picdrop.exception.ApplicationException;
import com.picdrop.guice.factory.CookieProviderFactory;
import com.picdrop.model.RequestContext;
import com.picdrop.model.TokenSet;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.Permission;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.token.ClaimSetFactory;
import com.picdrop.security.token.WebTokenFactory;
import static com.picdrop.helper.LogHelper.*;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author i330120
 */
@Path("/app")
public class AuthorizationService {

    Logger log = LogManager.getLogger();

    Repository<String, RegisteredUser> userRepo;
    Repository<String, TokenSet> tsRepo;

    CookieProviderFactory cookieProvFactory;
    WebTokenFactory tokenFactory;

    Authenticator<RegisteredUser> basicAuthenticator;
    Authenticator<RegisteredUser> refreshAuthenticator;

    ClaimSetFactory<RegisteredUser> authCsFact;
    ClaimSetFactory<RegisteredUser> refreshCsFact;

    @Inject
    Provider<RequestContext> contextProv;

    @Inject
    ObjectMapper mapper;

    final boolean cookieEnabled;
    final String authCookieName;
    final String refreshCookieName;
    final int tsExpiry;

    @Inject
    public AuthorizationService(
            Repository<String, RegisteredUser> userRepo,
            Repository<String, TokenSet> tsRepo,
            CookieProviderFactory cookieProvFactory,
            WebTokenFactory tokenFactory,
            @Named("authenticator.basic") Authenticator<RegisteredUser> basicAuthenticator,
            @Named("authenticator.token.refresh") Authenticator<RegisteredUser> refreshAuthenticator,
            @Named("claimset.factory.auth") ClaimSetFactory<RegisteredUser> authCsFact,
            @Named("claimset.factory.refresh") ClaimSetFactory<RegisteredUser> refreshCsFact,
            @Named("service.cookie.enabled") boolean cookieEnabled,
            @Named("service.cookie.auth.name") String authCookieName,
            @Named("service.cookie.refresh.name") String refreshCookieName,
            @Named("service.jwt.refresh.exp") int tsExpiry) {
        this.tsRepo = tsRepo;
        this.userRepo = userRepo;
        this.cookieProvFactory = cookieProvFactory;
        this.tokenFactory = tokenFactory;
        try {
            this.tokenFactory.init();
        } catch (IOException ex) {
            log.fatal("Unable to initialize token factory", ex);
        }

        this.authCsFact = authCsFact;
        this.refreshCsFact = refreshCsFact;
        this.basicAuthenticator = basicAuthenticator;
        this.refreshAuthenticator = refreshAuthenticator;
        this.cookieEnabled = cookieEnabled;
        this.authCookieName = authCookieName;
        this.refreshCookieName = refreshCookieName;
        this.tsExpiry = tsExpiry;
    }

    protected TokenSet.JsonWrapper generateTokens(
            RegisteredUser user,
            String nonce,
            String name) throws ApplicationException {
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
        ts.setName(name);
        ts.setExpireAt(DateTime.now(DateTimeZone.UTC)
                .plusMinutes(tsExpiry)
                .toDate());

        ts = this.tsRepo.save(ts);

        try {
            String authToken = tokenFactory.getToken(authClaims);
            String refreshToken = tokenFactory.getToken(refreshClaims);

            return new TokenSet.JsonWrapper()
                    .auth(authToken)
                    .refresh(refreshToken)
                    .nonce(Strings.isNullOrEmpty(nonce) ? null : nonce);
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(403);
        }
    }

    @POST
    @Path("/login")
    public Response loginUser(@Context HttpServletRequest request,
            @QueryParam("nonce") String nonce,
            @QueryParam("name") String name) throws ApplicationException { // TODO make redirect target injectable
        log.traceEntry();

        RegisteredUser user = basicAuthenticator.authenticate(request);
        if (user == null) {
            throw new ApplicationException()
                    .status(403);
        }
        log.debug(SERVICE, "User successfully authenticated by credentials");

        if (Strings.isNullOrEmpty(name)) {
            name = request.getHeader("user-agent");
        }

        log.debug(SERVICE, "Generating tokens");
        TokenSet.JsonWrapper tokens = generateTokens(user, nonce, name);

        user.setLastLogin();
        userRepo.update(user.getId(), user);

        log.debug(SERVICE, "Generating cookies");
        NewCookie authC = cookieProvFactory.getSessionCookieProvider(authCookieName, tokens.getAuth()).get();
        NewCookie refreshC = cookieProvFactory.getSessionCookieProvider(refreshCookieName, tokens.getRefresh()).get();

        log.info(SERVICE, "User logged in");
        log.traceExit();
        return Response
                .ok(tokens, MediaType.APPLICATION_JSON)
                .cookie(authC, refreshC)
                .build();
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(@Context HttpServletRequest request,
            @QueryParam("nonce") String nonce) throws ApplicationException {
        log.traceEntry();
        RegisteredUser user = refreshAuthenticator.authenticate(request);
        if (user == null) {
            throw new ApplicationException()
                    .status(403);
        }
        log.debug(SERVICE, "User successfully authenticated by refresh token");

        log.debug(SERVICE, "Deleting old tokens");
        TokenSet ts = user.getActiveToken();
        tsRepo.delete(ts.getId());

        log.debug(SERVICE, "Generating tokens");
        TokenSet.JsonWrapper tokens = generateTokens(user, nonce, ts.getName());

        user.setLastLogin();
        userRepo.update(user.getId(), user);

        log.debug(SERVICE, "Generating cookies");
        NewCookie authC = cookieProvFactory.getSessionCookieProvider(authCookieName, tokens.getAuth()).get();
        NewCookie refreshC = cookieProvFactory.getSessionCookieProvider(refreshCookieName, tokens.getRefresh()).get();

        log.info(SERVICE, "User token's refreshed");
        log.traceExit();
        return Response
                .ok(tokens, MediaType.APPLICATION_JSON)
                .cookie(authC, refreshC)
                .build();
    }

    @POST
    @Path("/logout")
    @Permission("*/logout")
    public Response logoutUser() { // TODO rework login/logout
        log.traceEntry();
        User user = contextProv.get().getPrincipal();
        if (user == null) {
            return Response.ok().build();
        }

        log.debug(SERVICE, "Deleting tokens");
        RegisteredUser ru = user.to(RegisteredUser.class);
        tsRepo.delete(ru.getActiveToken().getId());

        log.debug(SERVICE, "Generating kill cookies");
        NewCookie authC = cookieProvFactory.getSessionCookieProvider(authCookieName, "").get();
        NewCookie refreshC = cookieProvFactory.getSessionCookieProvider(refreshCookieName, "").get();

        NewCookie killcookie1 = new NewCookie(authC, authC.getComment(), 0, authC.isSecure());
        NewCookie killcookie2 = new NewCookie(refreshC, refreshC.getComment(), 0, refreshC.isSecure());

        log.info(SERVICE, "User logged out");
        log.traceExit();
        return Response.ok()
                .cookie(killcookie1, killcookie2)
                .build();
    }
}
