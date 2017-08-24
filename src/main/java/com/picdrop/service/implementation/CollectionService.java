/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.picdrop.exception.ApplicationException;
import com.picdrop.model.RequestContext;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.Authenticated;
import com.picdrop.security.authentication.RoleType;
import com.picdrop.service.CrudService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author i330120
 */
@Path("/app/collections")
@Consumes("application/json")
@Produces("application/json")
public class CollectionService extends CrudService<String, Collection, Repository<String, Collection>> {

    Repository<String, Collection.CollectionItem> ciRepo;
    Repository<String, FileResource> fRepo;
    
    @Inject
    Provider<RequestContext> context;

    @Inject
    public CollectionService(Repository<String, Collection> repo,
            Repository<String, Collection.CollectionItem> ciRepo,
            Repository<String, FileResource> fRepo) {
        super(repo);
        this.ciRepo = ciRepo;
        this.fRepo = fRepo;
    }

    @PUT
    @Path("/{id}")
    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public Collection update(@PathParam("id") String id, Collection entity) throws ApplicationException {
        return super.update(id, entity); //To change body of generated methods, choose Tools | Templates.
    }

    @GET
    @Path("/{id}")
    @Override
    @Authenticated(include = {RoleType.REGISTERED})
    public Collection get(@PathParam("id") String id) throws ApplicationException {
        return super.get(id);
    }

    @DELETE
    @Path("/{id}")
    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public void delete(@PathParam("id") String id) throws ApplicationException {
        // TODO delete open shares
        super.delete(id);
    }

    @GET
    @Path("/")
    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public List<Collection> list() throws ApplicationException {
        return super.list();
    }

    @POST
    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public Collection create(Collection entity) throws ApplicationException {      
        try {
            entity.setOwner(context.get().getPrincipal().to(RegisteredUser.class));
        } catch (IllegalArgumentException ex) {
            return null; // 500
        }
        
        return super.create(entity);
    }

    @GET
    @Path("/{id}/elements")
    @Authenticated(include = {RoleType.REGISTERED})
    public List<Collection.CollectionItem> listElements(@PathParam("id") String id) throws ApplicationException {
        Collection c = get(id);
        if (c == null) {
            return null; // 404
        }
        return c.getResources();
    }

    @GET
    @Path("/{id}/elements/{eid}")
    @Authenticated(include = {RoleType.REGISTERED})
    public Collection.CollectionItem getElement(@PathParam("id") String id, @PathParam("eid") String eid) throws ApplicationException {
        Collection c = get(id);
        if (c == null) {
            return null; // 404
        }

        Collection.CollectionItem ce = ciRepo.get(eid);

        return (c.getResources().contains(ce))
                ? ce
                : null; // 404
    }

    @DELETE
    @Path("/{id}/elements/{eid}")
    @Authenticated(include = RoleType.REGISTERED)
    public void deleteElement(@PathParam("id") String id, @PathParam("eid") String eid) throws ApplicationException {
        Collection c = get(id);
        if (c == null) {
            return; // 404
        }
        Collection.CollectionItem ce = ciRepo.get(eid);

        if (c.getResources().contains(ce)) {
            c.getResources().remove(ce);
            repo.update(id, c);
            ciRepo.delete(eid);
        } else {
            // 404
        }
    }

    @POST
    @Path("/{id}/elements")
    @Authenticated(include = RoleType.REGISTERED)
    public Collection.CollectionItem addElement(@PathParam("id") String id, Collection.CollectionItem entity) throws ApplicationException {
        Collection c = get(id);
        if (c == null) {
            return null;// 404
        }

        FileResource fr = entity.getResource();
        if ((fr == null) || Strings.isNullOrEmpty(fr.getId())) {
            return null;// 400
        }

        fr = fRepo.get(fr.getId());
        if (fr == null) {
            return null; // 400
        }

        Collection.CollectionItem ce = new Collection.CollectionItem();
        ce.setResource(fr);

        ce = ciRepo.save(ce);
        if (ce == null) {
            return null; // 500
        }

        c.addResource(ce);
        if (repo.update(c.getId(), c) == null) {
            return null; // 500 
            // TODO rollback?
        }

        return ce;
    }
}
