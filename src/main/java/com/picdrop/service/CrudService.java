/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service;

import com.picdrop.repository.Repository;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author i330120
 */
public abstract class CrudService<ID, T, REPO extends Repository<ID, T>> {

    protected REPO repo;

    @Inject
    public CrudService(REPO repo) {
        this.repo = repo;
    }

    @POST
    @Path(value = "/")
    @Transactional
    public T create(T entity) {
        return this.repo.save(entity);
    }

    @GET
    @Path("/")
    public List<T> list() {
        return this.repo.list();
    }

    @DELETE
    @Path(value = "/{id}")
    @Transactional
    public void delete(@PathParam(value = "id") ID id) {
        this.repo.delete(id);
    }

    @GET
    @Path(value = "/{id}")
    public T get(@PathParam(value = "id") ID id) {
        return this.repo.get(id);
    }

    @PUT
    @Path(value = "/{id}")
    @Transactional
    public T update(@PathParam(value = "id") ID id, T entity) {
        return this.repo.update(id, entity);
    }
}
