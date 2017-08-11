/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.name.Named;
import com.picdrop.model.Share;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.Authenticated;
import com.picdrop.security.authentication.RoleType;
import com.picdrop.service.CrudService;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author nvonstein
 */
@Path("/app/shares")
@Consumes("application/json")
@Produces("application/json")
public class ShareService extends CrudService<String, Share, AwareRepository<String, Share, User>> {
    
    public ShareService(AwareRepository<String, Share, User> repo) {
        super(repo);
    }

    @PUT
    @Path("/{id}")
    @Authenticated(include = RoleType.REGISTERED)
    @Override
    public Share update(@PathParam("id") String id, Share entity) {
        return super.update(id, entity);
    }

    @GET
    @Path("/{id}")
    @Override
    public Share get(@PathParam("id") String id) {
        return super.get(id);
    }

    @DELETE
    @Path("/{id}")
    @Authenticated(include = RoleType.REGISTERED)
    @Override
    public void delete(@PathParam("id") String id) {
        super.delete(id);
    }

    @GET
    @Authenticated(include = RoleType.REGISTERED)
    @Override
    public List<Share> list() {
        return super.list();
    }

    @POST
    @Authenticated(include = RoleType.REGISTERED)
    @Override
    public Share create(Share entity) {
        return super.create(entity);
    }

    
}
