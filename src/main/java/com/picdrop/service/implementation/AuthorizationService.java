/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.name.Named;
import com.picdrop.guice.provider.CookieProviderFactory;
import com.picdrop.model.Identifiable;
import com.picdrop.model.LoggedIn;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import com.picdrop.security.authenticator.Authenticator;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author i330120
 */
@Path("/")
public class AuthorizationService {

    Repository<String, RegisteredUser> userRepo;
    Repository<String, LoggedIn> loginRepo;
    CookieProviderFactory cookieProvFactory;
    Authenticator authenticator;

    @Inject
    public AuthorizationService(
            Repository<String, RegisteredUser> userRepo,
            Repository<String, LoggedIn> loginRepo,
            @Named("basic") Authenticator authenticator,
            CookieProviderFactory cookieProvFactory) {
        this.userRepo = userRepo;
        this.loginRepo = loginRepo;
        this.cookieProvFactory = cookieProvFactory;
        this.authenticator = authenticator;
    }

    @Inject
    @POST
    @Path("/login")
    public Response loginUser(@Context HttpServletRequest request) { // TODO make redirect target injectable
        RegisteredUser user = authenticator.authenticate(request);
        if (user == null) {
            return Response.noContent().status(Status.UNAUTHORIZED).build();
        }

        LoggedIn li = this.loginRepo.save(new LoggedIn(user));
        if (li == null) {
            return Response.noContent().status(Status.UNAUTHORIZED).build(); // TODO maybe 500 or 503?
        }

        // generate JWT with logged in id and build cookie     
        NewCookie c = cookieProvFactory.getSessionCookieProvider("").get();
        
        return Response.seeOther(null).cookie(c).build(); // Redirect dahsboard
    }

    @POST
    @Path("/logout")
    public Response logoutUser(Identifiable jwt) { // TODO inject parsed JWT
        if (!this.loginRepo.delete(jwt.getId())) {
            // TODO Log 
        }

        // generate kill cookie
        NewCookie c = cookieProvFactory.getSessionCookieProvider("").get();
        NewCookie killcookie = new NewCookie(c, c.getComment(), 0, c.isSecure());

        return Response.seeOther(null).cookie(killcookie).build(); // Redirect dahsboard
    }
}
