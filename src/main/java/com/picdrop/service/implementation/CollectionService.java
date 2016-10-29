/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author i330120
 */
@Path("/collections")
public class CollectionService extends CrudService<String, Collection, Repository<String, Collection>> {

    Repository<String, Collection.CollectionElement> cRepo;
    Repository<String, FileResource> fRepo;
    
    @Inject
    Provider<RequestContext> context;

    @Inject
    public CollectionService(Repository<String, Collection> repo,
            Repository<String, Collection.CollectionElement> cRepo,
            Repository<String, FileResource> fRepo) {
        super(repo);
        this.cRepo = cRepo;
        this.fRepo = fRepo;
    }

    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public Collection update(String id, Collection entity) {
        return super.update(id, entity); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Authenticated(include = {RoleType.REGISTERED, RoleType.USER})
    public Collection get(String id) {
        return super.get(id);
    }

    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public void delete(String id) {
        super.delete(id);
    }

    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public List<Collection> list() {
        return super.list();
    }

    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public Collection create(Collection entity) {      
        try {
            entity.setOwner(context.get().getPrincipal().to(RegisteredUser.class));
        } catch (IOException ex) {
            return null; // 500
        }
        
        return super.create(entity);
    }

    @GET
    @Path("/{id}/elements")
    @Authenticated(include = {RoleType.REGISTERED, RoleType.USER})
    public List<Collection.CollectionElement> listElements(@PathParam("id") String id) {
        Collection c = get(id);
        if (c == null) {
            return null; // 404
        }
        return c.getResources();
    }

    @GET
    @Path("/{id}/elements/{eid}")
    @Authenticated(include = {RoleType.REGISTERED, RoleType.USER})
    public Collection.CollectionElement getElement(@PathParam("id") String id, @PathParam("eid") String eid) {
        Collection c = get(id);
        if (c == null) {
            return null; // 404
        }

        Collection.CollectionElement ce = cRepo.get(eid);

        return (c.getResources().contains(ce))
                ? ce
                : null; // 404
    }

    @DELETE
    @Path("/{id}/elements/{eid}")
    @Authenticated(include = RoleType.REGISTERED)
    public void deleteElement(@PathParam("id") String id, @PathParam("eid") String eid) {
        Collection c = get(id);
        if (c == null) {
            return;// 404
        }

        Collection.CollectionElement ce = cRepo.get(eid);

        if (c.getResources().contains(ce)) {
            c.getResources().remove(ce);
            repo.update(id, c);
            cRepo.delete(eid);
        } else {
            // 404
        }
    }

    @POST
    @Path("/{id}/elements")
    @Authenticated(include = RoleType.REGISTERED)
    public Collection.CollectionElement addElement(@PathParam("id") String id, Collection.CollectionElement entity) {
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

        Collection.CollectionElement ce = new Collection.CollectionElement();
        ce.setResource(fr);

        ce = cRepo.save(ce);
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
