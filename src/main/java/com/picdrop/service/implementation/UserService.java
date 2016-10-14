/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.annotations.Authorized;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import com.picdrop.service.CrudService;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author i330120
 */
@Path("/usersold")
@Consumes("application/json")
@Produces("application/json")
@Authorized
public class UserService extends CrudService<String, User, Repository<String, User>> {

    @Inject
    public UserService(@Named("users") Repository<String, User> repo) {
        super(repo);
    }

}
