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
import com.picdrop.model.ShareReference;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.resource.FileResourceReference;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.Permission;
import com.picdrop.service.CrudService;
import java.io.IOException;
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
    @Permission("write")
    public Collection update(@PathParam("id") String id, Collection entity) throws ApplicationException {
        log.entry(id);
        if (entity == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_REQUEST_BODY);
        }
        Collection c = this.get(id);

        try {
            c = c.merge(entity);
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(500)
                    .code(ErrorMessageCode.ERROR_OBJ_MERGE)
                    .devMessage(ex.getMessage());
        }

        log.traceExit();
        return super.update(id, c);
    }

    @GET
    @Path("/{id}")
    @Override
    @Permission("read")
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
    @Permission("write")
    public void delete(@PathParam("id") String id) throws ApplicationException {
        log.entry(id);
        Collection c = this.get(id);

        for (ShareReference sref : c.getShares()) {
            sRepo.delete(sref.getId());
        }

        for (Collection.CollectionItemReference ciref : c.getItems()) {
            ciRepo.delete(ciref.getId());
        }

        repo.delete(id);

        log.traceExit();
    }

    @GET
    @Path("/")
    @Override
    @Permission("read")
    public List<Collection> list() throws ApplicationException {
        return super.list();
    }

    @POST
    @Override
    @Permission("write")
    public Collection create(Collection entity) throws ApplicationException {
        log.entry(entity);
        if (entity == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_REQUEST_BODY);
        }

        entity.setCreated(DateTime.now(DateTimeZone.UTC).getMillis());
        entity.setOwner(context.get().getPrincipal().to(RegisteredUser.class));
        if (Strings.isNullOrEmpty(entity.getName())) {
            entity.setName("Collection");
        }

        // Optionally create CollectionItems
        if ((entity.getItems() != null) && !entity.getItems().isEmpty()) {

            // Validity check of CollectionItem (to reject invalid requests faster)
            for (Collection.CollectionItemReference ci : entity.getItems()) {
                if ((ci.getResource() == null) || Strings.isNullOrEmpty(ci.getResource().getId())) {
                    throw new ApplicationException()
                            .status(400)
                            .code(ErrorMessageCode.BAD_CITEM)
                            .devMessage("Collection item's resource was null");
                }
            }

            // Existence check of Resource
            List<Collection.CollectionItem> items = new ArrayList<>();
            for (Collection.CollectionItemReference ciref : entity.getItems()) {

                FileResource fr = ciref.getResource().resolve(false);
                if (fr == null) {
                    throw new ApplicationException()
                            .status(400)
                            .code(ErrorMessageCode.BAD_CITEM_NOT_FOUND)
                            .devMessage(String.format("Collection item's resource not found for id '%s'", ciref.getResource().getId()));
                }

                Collection.CollectionItem ci = new Collection.CollectionItem();
                ci.setResource(fr);
                items.add(ci);
            }

            entity = super.create(entity);
            for (Collection.CollectionItem ci : items) {
                ci.setParentCollection(entity);
                ci = this.ciRepo.save(ci);
                entity.addItem(ci);
            }

            entity = this.repo.update(entity.getId(), entity);
        } else {
            entity = super.create(entity);
        }

        log.traceExit(entity);
        return entity;
    }

    @GET
    @Path("/{id}/elements")
    @Permission("read")
    public List<Collection.CollectionItem> listElements(@PathParam("id") String id) throws ApplicationException {
        Collection c = this.get(id);

        List<Collection.CollectionItem> ret = new ArrayList<>();
        for (Collection.CollectionItemReference ciref : c.getItems()) {
            ret.add(ciref.resolve(false));
        }

        return ret;
    }

    @GET
    @Path("/{id}/elements/{eid}")
    @Permission("read")
    public Collection.CollectionItem getElement(@PathParam("id") String id, @PathParam("eid") String eid) throws ApplicationException {
        log.entry(id, eid);
        Collection c = this.get(id);

        for (Collection.CollectionItemReference ciref : c.getItems()) {
            if (ciref.getId().equals(eid)) {
                return log.traceExit(ciref.resolve(false));
            }
        }

        throw new ApplicationException()
                .status(404)
                .code(ErrorMessageCode.NOT_FOUND)
                .devMessage(String.format("Object with id '%s' not found", id));
    }

    @DELETE
    @Path("/{id}/elements/{eid}")
    @Permission("write")
    public void deleteElement(@PathParam("id") String id, @PathParam("eid") String eid) throws ApplicationException {
        log.entry(id, eid);
        Collection c = this.get(id);

        for (Collection.CollectionItemReference ciref : c.getItems()) {
            if (ciref.getId().equals(eid)) {
                ciRepo.delete(ciref.getId());
                c = c.removeItem(ciref);

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
    @Permission("write")
    public Collection.CollectionItem addElement(@PathParam("id") String id, Collection.CollectionItem entity) throws ApplicationException {
        log.entry(id, entity);
        Collection c = this.get(id);

        FileResourceReference frref = entity.getResource();
        if ((frref == null) || Strings.isNullOrEmpty(frref.getId())) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_CITEM)
                    .devMessage("Collection item's resource was empty or null");
        }

        FileResource fr = fRepo.get(frref.getId());
        if (fr == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_CITEM_NOT_FOUND)
                    .devMessage(String.format("Collection item's resource not found for id '%s'", entity.getResource().getId()));
        }

        entity.setResource(fr);
        entity = ciRepo.save(entity);

        c = c.addItem(entity);

        // TODO validate update?
        repo.update(c.getId(), c);

        log.traceExit(entity);
        return entity;
    }
}
