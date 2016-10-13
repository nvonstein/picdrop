/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.model.Group;
import com.picdrop.model.Identifiable;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import com.picdrop.service.ExtendedCrudService;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author i330120
 */
@Path("/groups")
@Consumes("application/json")
@Produces("application/json")
public class GroupService extends ExtendedCrudService<Group, Repository<String, Group>> {

    @Inject
    public GroupService(@Named("groups") Repository<String, Group> repo) {
        super(repo);
    }

    @Override
    public Object performAddSubResource(String field, Group model, Identifiable entity) {
        switch (field) {
            case "users":
                model.addUser(new User(entity.getId()));
                break;
            default:
                return null; // 404
        }
        return super.performAddSubResource(field, model, entity);
    }

    @Override
    protected Object performDeleteSubResource(String field, Group model, String eid) {
        switch (field) {
            case "users":
                model.removeUser(new User(eid));
                break;
            default:
                return null; // 404
        }
        return super.performDeleteSubResource(field, model, eid);
    }

    @Override
    protected Object performGetSubResource(String field, Group model) {
        switch (field) {
            case "users":
                return model.getUsers();
            default:
                return null; // 404
        }
    }
}
