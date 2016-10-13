/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.name.Named;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import com.picdrop.security.authenticator.Authenticator;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 *
 * @author i330120
 */
@Path("/login")
public class AuthorizationService {

    Repository<String, RegisteredUser> userRepo;

    public AuthorizationService(Repository<String, RegisteredUser> userRepo) {
        this.userRepo = userRepo;
    }

    @Inject
    @POST
    public Response loginUser(@Context HttpServletRequest request, @Named("basic") Authenticator authenticator) {
        RegisteredUser user = authenticator.authenticate(request);
        if (user == null) {
            return Response.noContent().status(Status.UNAUTHORIZED).build();
        }
        
        // generate JWT with user id
        
        return Response.seeOther(null).cookie(null).build();
    }
}
