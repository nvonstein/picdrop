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
import com.picdrop.exception.ErrorMessageCode;
import com.picdrop.model.RequestContext;
import com.picdrop.model.Share;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.Authenticated;
import com.picdrop.security.authentication.RoleType;
import com.picdrop.service.CrudService;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author i330120
 */
@Path("/app/collections")
@Consumes("application/json")
@Produces("application/json")
public class CollectionService extends CrudService<String, Collection, Repository<String, Collection>> {
    
    Logger log = LogManager.getLogger(this.getClass());
    
    Repository<String, Collection.CollectionItem> ciRepo;
    Repository<String, FileResource> fRepo;
    Repository<String, Share> sRepo;
    
    @Inject
    Provider<RequestContext> context;
    
    @Inject
    public CollectionService(Repository<String, Collection> repo,
            Repository<String, Collection.CollectionItem> ciRepo,
            Repository<String, FileResource> fRepo,
            Repository<String, Share> sRepo) {
        super(repo);
        this.ciRepo = ciRepo;
        this.fRepo = fRepo;
        this.sRepo = sRepo;
        
        log.trace("created with ({},{},{})", ciRepo, fRepo, sRepo);
    }
    
    @PUT
    @Path("/{id}")
    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public Collection update(@PathParam("id") String id, Collection entity) throws ApplicationException {
        log.entry(id);
        if (entity == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_REQUEST_BODY);
        }
        Collection c = this.get(id);

        // TODO merge
        log.traceExit();
        return super.update(id, c);
    }
    
    @GET
    @Path("/{id}")
    @Override
    @Authenticated(include = {RoleType.REGISTERED})
    public Collection get(@PathParam("id") String id) throws ApplicationException {
        log.entry(id);
        Collection c = super.get(id);
        if (c == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        log.traceExit(c);
        return c;
    }
    
    @DELETE
    @Path("/{id}")
    @Override
    @Authenticated(include = RoleType.REGISTERED)
    public void delete(@PathParam("id") String id) throws ApplicationException {
        log.entry(id);
        Collection c = this.get(id);
        
        for (String sid : c.getShareIds()) {
            sRepo.delete(sid);
        }
        
        for (Collection.CollectionItem ci : c.getResources()) {
            ciRepo.delete(ci.getId());
        }
        
        if (!repo.delete(id)) { // TODO rethink capturing error on deletion
            throw new ApplicationException()
                    .status(500)
                    .code(ErrorMessageCode.ERROR_DELETE)
                    .devMessage("Repository returned 'false'");
        }
        log.traceExit();
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
        log.entry(entity);
        if (entity == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_REQUEST_BODY);
        }
        
        entity.setCreated(DateTime.now(DateTimeZone.UTC).getMillis());
        entity.setOwner(context.get().getPrincipal().to(RegisteredUser.class));

        // Optionally create CollectionItems
        if ((entity.getResources() != null) && !entity.getResources().isEmpty()) {

            // Validity check of CollectionItem (to reject invalid requests faster)
            for (Collection.CollectionItem ci : entity.getResources()) {
                if ((ci.getResource() == null) || Strings.isNullOrEmpty(ci.getResource().getId())) {
                    throw new ApplicationException()
                            .status(400)
                            .code(ErrorMessageCode.BAD_CITEM)
                            .devMessage("Collection item's resource was null");
                }
            }
            
            List<Collection.CollectionItem> items = new ArrayList<>();
            // Existence check of Resource
            for (Collection.CollectionItem ci : entity.getResources()) {
                FileResource fr = fRepo.get(ci.getResource().getId());
                if (fr == null) {
                    throw new ApplicationException()
                            .status(400)
                            .code(ErrorMessageCode.BAD_CITEM_NOT_FOUND)
                            .devMessage(String.format("Collection item's resource not found for id '%s'", ci.getResource().getId()));
                }
                ci.setResource(fr);
                items.add(ciRepo.save(ci));
            }

            // Set item list on Collection entity
            entity.setResources(items);
        }
        
        if (Strings.isNullOrEmpty(entity.getName())) {
            entity.setName("Collection");
        }
        
        entity = super.create(entity);
        
        log.traceExit(entity);
        return entity;
    }
    
    @GET
    @Path("/{id}/elements")
    @Authenticated(include = {RoleType.REGISTERED})
    public List<Collection.CollectionItem> listElements(@PathParam("id") String id) throws ApplicationException {
        Collection c = this.get(id);
        return c.getResources();
    }
    
    @GET
    @Path("/{id}/elements/{eid}")
    @Authenticated(include = {RoleType.REGISTERED})
    public Collection.CollectionItem getElement(@PathParam("id") String id, @PathParam("eid") String eid) throws ApplicationException {
        log.entry(id, eid);
        Collection c = this.get(id);
        
        for (Collection.CollectionItem ci : c.getResources()) {
            if (ci.getId().equals(eid)) {
                return log.traceExit(ci);
            }
        }
        
        throw new ApplicationException()
                .status(404)
                .code(ErrorMessageCode.NOT_FOUND)
                .devMessage(String.format("Object with id '%s' not found", id));
    }
    
    @DELETE
    @Path("/{id}/elements/{eid}")
    @Authenticated(include = RoleType.REGISTERED)
    public void deleteElement(@PathParam("id") String id, @PathParam("eid") String eid) throws ApplicationException {
        log.entry(id, eid);
        Collection c = this.get(id);
        
        for (Collection.CollectionItem ci : c.getResources()) {
            if (ci.getId().equals(eid)) {
                ciRepo.delete(ci.getId());
                c = c.removeResource(ci);
                
                repo.update(c.getId(), c);
                log.traceExit();
                return;
            }
        }
        
        throw new ApplicationException()
                .status(404)
                .code(ErrorMessageCode.NOT_FOUND)
                .devMessage(String.format("Object with id '%s' not found", id));
    }
    
    @POST
    @Path("/{id}/elements")
    @Authenticated(include = RoleType.REGISTERED)
    public Collection.CollectionItem addElement(@PathParam("id") String id, Collection.CollectionItem entity) throws ApplicationException {
        log.entry(id, entity);
        Collection c = this.get(id);
        
        FileResource fr = entity.getResource();
        if ((fr == null) || Strings.isNullOrEmpty(fr.getId())) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_CITEM)
                    .devMessage("Collection item's resource was empty or null");
        }
        
        fr = fRepo.get(fr.getId());
        if (fr == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_CITEM_NOT_FOUND)
                    .devMessage(String.format("Collection item's resource not found for id '%s'", entity.getResource().getId()));
        }
        
        entity.setResource(fr);
        entity = ciRepo.save(entity);
        
        c = c.addResource(entity);
        
        // TODO validate update?
        repo.update(c.getId(), c);
        
        log.traceExit(entity);
        return entity;
    }
}
