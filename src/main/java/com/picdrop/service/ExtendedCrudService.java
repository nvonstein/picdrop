/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service;

import com.picdrop.model.Identifiable;
import com.picdrop.repository.Repository;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author i330120
 */
public abstract class ExtendedCrudService<T extends Identifiable, REPO extends Repository<String, T>>
        extends CrudService<String, T, REPO> {

    public ExtendedCrudService(REPO repo) {
        super(repo);
    }

    protected Object performAddSubResource(String field, T model, Identifiable entity) {
        return repo.update(model.getId(), model);
    }

    protected Object performGetSubResource(String field, T model) {
        return null; // 404
    }

    protected Object performDeleteSubResource(String field, T model, String eid) {
        return repo.update(model.getId(), model);
    }

    @POST
    @Path("/{id}/{field}")
    public Object addSubResource(@PathParam("id") String id, @PathParam("field") String field, Identifiable entity) {
        T model = super.get(id);
        if (model == null) {
            return null; // 404
        }

        return performAddSubResource(field, model, entity);
    }

    @GET
    @Path("/{id}/{field}")
    public Object getSubResource(@PathParam("id") String id, @PathParam("field") String field) {
        T model = super.get(id);
        if (model == null) {
            return null; // 404
        }

        return performGetSubResource(field, model);
    }

    @DELETE
    @Path("/{id}/{field}/{eid}")
    public Object deleteSubResource(@PathParam("id") String id, @PathParam("field") String field, @PathParam("eid") String eid) {
        T model = super.get(id);
        if (model == null) {
            return null; // 404
        }

        return performDeleteSubResource(field, model, eid);
    }
}
