/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.nimbusds.jwt.JWTClaimsSet;
import com.picdrop.exception.ApplicationException;
import com.picdrop.exception.ErrorMessageCode;
import com.picdrop.guice.factory.CookieProviderFactory;
import com.picdrop.guice.names.AuthorizationToken;
import com.picdrop.guice.names.Credentials;
import com.picdrop.guice.names.RefreshToken;
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

    protected Logger log = LogManager.getLogger();

    protected Repository<String, RegisteredUser> userRepo;
    protected Repository<String, TokenSet> tsRepo;

    protected CookieProviderFactory cookieProvFactory;
    protected WebTokenFactory tokenFactory;

    protected Authenticator<RegisteredUser> basicAuthenticator;
    protected Authenticator<RegisteredUser> refreshAuthenticator;

    protected ClaimSetFactory<RegisteredUser> authCsFact;
    protected ClaimSetFactory<RegisteredUser> refreshCsFact;

    @Inject
    protected Provider<RequestContext> contextProv;

    protected boolean cookieEnabled;
    protected String authCookieName;
    protected String refreshCookieName;
    protected int tsExpiry;

    @Inject
    public AuthorizationService(
            Repository<String, RegisteredUser> userRepo,
            Repository<String, TokenSet> tsRepo,
            WebTokenFactory tokenFactory) {
        this.tsRepo = tsRepo;
        this.userRepo = userRepo;

        this.tokenFactory = tokenFactory;
        try {
            this.tokenFactory.init();
        } catch (IOException ex) {
            log.fatal("Unable to initialize token factory", ex);
        }
    }

    @Inject
    public void setCookieProvFactory(CookieProviderFactory cookieProvFactory) {
        this.cookieProvFactory = cookieProvFactory;
    }

    @Inject
    public void setBasicAuthenticator(@Credentials Authenticator<RegisteredUser> basicAuthenticator) {
        this.basicAuthenticator = basicAuthenticator;
    }

    @Inject
    public void setRefreshAuthenticator(@RefreshToken Authenticator<RegisteredUser> refreshAuthenticator) {
        this.refreshAuthenticator = refreshAuthenticator;
    }

    @Inject
    public void setAuthClaimSetFact(@AuthorizationToken ClaimSetFactory<RegisteredUser> authCsFact) {
        this.authCsFact = authCsFact;
    }

    @Inject
    public void setRefreshClaimSetFact(@RefreshToken ClaimSetFactory<RegisteredUser> refreshCsFact) {
        this.refreshCsFact = refreshCsFact;
    }

    @Inject
    public void setCookieEnabled(@Named("service.cookie.enabled") boolean cookieEnabled) {
        this.cookieEnabled = cookieEnabled;
    }

    @Inject
    public void setAuthCookieName(@Named("service.cookie.auth.name") String authCookieName) {
        this.authCookieName = authCookieName;
    }

    @Inject
    public void setRefreshCookieName(@Named("service.cookie.refresh.name") String refreshCookieName) {
        this.refreshCookieName = refreshCookieName;
    }

    @Inject
    public void setTokenSetExpiry(@Named("service.jwt.refresh.exp") int tsExpiry) {
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
                    .devMessage("Invalid login recieved")
                    .status(401);
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
    @Permission("logout")
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
