/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import com.picdrop.service.CrudService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import com.picdrop.security.authentication.Authenticated;

/**
 *
 * @author i330120
 */
@Path("/usersold")
@Consumes("application/json")
@Produces("application/json")
@Authenticated
public class UserService extends CrudService<String, User, Repository<String, User>> {
    
    @Inject
    Provider<RequestContext> context;

    @Inject
    public UserService(@Named("users") Repository<String, User> repo) {
        super(repo);
    }

    @Path("/me")
    @GET
    public User user() {
        User current = context.get().getPrincipal();
        return current; //To change body of generated methods, choose Tools | Templates.
    }
    
    

}
