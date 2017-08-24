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
import com.picdrop.model.resource.Collection.Rating;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.user.NameOnlyUserReference;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.Authenticated;
import com.picdrop.security.authentication.RoleType;
import com.picdrop.service.CrudService;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
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
 * @author nvonstein
 */
@Path("/app/shares")
@Consumes("application/json")
@Produces("application/json")
public class ShareService extends CrudService<String, Share, AwareRepository<String, Share, User>> {

    Logger log = LogManager.getLogger(this.getClass());

    Repository<String, Collection> crepo;
    Repository<String, FileResource> frepo;

    @Inject
    Provider<RequestContext> contextProv;

    @Inject
    public ShareService(AwareRepository<String, Share, User> repo,
            Repository<String, Collection> crepo,
            Repository<String, FileResource> frepo) {
        super(repo);

        this.crepo = crepo;
        this.frepo = frepo;

        log.trace("created with ({},{},{})", repo, crepo, frepo);
    }

    private boolean verifyName(NameOnlyUserReference entity) throws ApplicationException {
        if ((entity != null) && (entity.getName().length() > 100)) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.BAD_NAME);
        }
        return true;
    }

    @POST
    @Path("/{id}/collections/{cid}/elements/{eid}/ratings")
    public Collection.CollectionItem rate(@PathParam("id") String id,
            @PathParam("cid") String cid,
            @PathParam("eid") String eid,
            Rating entity) throws ApplicationException {
        log.entry(id, cid, eid, entity);
        Share s = this.get(id);
        if (s.getResource() == null) {
            log.warn("Active share ({}) requested without existing resource attached. Possibly some cleanup is missing.", id);
            this.repo.delete(id, null);
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        if (!s.getResource().getId().equals(cid)) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", cid));
        }
        if (!s.getResource().isCollection()) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.BAD_OPERATION);
        }
        RequestContext ctx = contextProv.get();
        if (ctx.hasPrincipal() && ctx.getPrincipal().isRegistered()) {
            entity.setUser(ctx.getPrincipal());
        } else {
            verifyName(entity);
        }
        Collection c = (Collection) s.getResource();
        for (Collection.CollectionItem item : c.getResources()) {
            if (c.getId().equals(eid)) {
                item.addRating(entity);
                log.traceExit(item);
                return item;
            }
        }
        throw new ApplicationException()
                .status(404)
                .code(ErrorMessageCode.NOT_FOUND)
                .devMessage(String.format("Object with id '%s' not found", eid));
    }

    @POST
    @Path("/{id}/collections/{cid}/elements/{eid}/comments")
    public Collection.CollectionItem comment(@PathParam("id") String id,
            @PathParam("cid") String cid,
            @PathParam("eid") String eid,
            Collection.Comment entity) throws ApplicationException {
        log.entry(id, cid, eid, entity);
        Share s = this.get(id);
        if (s.getResource() == null) {
            log.warn("Active share ({}) requested without existing resource attached. Possibly some cleanup is missing.", id);
            this.repo.delete(id, null);
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        if (!s.getResource().getId().equals(cid)) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", cid));
        }
        if (!s.getResource().isCollection()) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.BAD_OPERATION);
        }
        RequestContext ctx = contextProv.get();
        if (ctx.hasPrincipal() && ctx.getPrincipal().isRegistered()) {
            entity.setUser(ctx.getPrincipal());
        } else {
            verifyName(entity);
        }
        entity.setCreated(DateTime.now(DateTimeZone.UTC).getMillis());
        Collection c = (Collection) s.getResource();
        for (Collection.CollectionItem item : c.getResources()) {
            if (c.getId().equals(eid)) {
                item.addComment(entity);
                log.traceExit(item);
                return item;
            }
        }
        throw new ApplicationException()
                .status(404)
                .code(ErrorMessageCode.NOT_FOUND)
                .devMessage(String.format("Object with id '%s' not found", eid));
    }

    @PUT
    @Path("/{id}")
    @Authenticated(include = RoleType.REGISTERED)
    @Override
    public Share update(@PathParam("id") String id, Share entity) throws ApplicationException {
        log.entry(id, entity);
        Share s = super.get(id);
        if (s == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        try {
            s = s.merge(entity);
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(500)
                    .code(ErrorMessageCode.ERROR_OBJ_MERGE)
                    .devMessage(ex.getMessage());
        }
        return log.traceExit(super.update(id, s));
    }

    @GET
    @Path("/{id}")
    @Override
    public Share get(@PathParam("id") String id) throws ApplicationException {
        log.entry(id);
        Share s = this.repo.get(id, null);
        if (s == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        log.traceExit(s);
        return s;
    }

    @DELETE
    @Path("/{id}")
    @Authenticated(include = RoleType.REGISTERED)
    @Override
    public void delete(@PathParam("id") String id) throws ApplicationException {
        log.entry(id);
        Share s = super.get(id);
        if (s == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        if (!repo.delete(id)) {
            throw new ApplicationException()
                    .status(500)
                    .code(ErrorMessageCode.ERROR_DELETE)
                    .devMessage("Repositotry returned 'false'");
        }
        log.traceExit();
    }

    @GET
    @Authenticated(include = RoleType.REGISTERED)
    @Override
    public List<Share> list() throws ApplicationException {
        log.traceEntry();
        return log.traceExit(super.list());
    }

    @POST
    @Authenticated(include = RoleType.REGISTERED)
    @Override
    public Share create(Share entity) throws ApplicationException {
        log.entry(entity);

        entity.setCreated(DateTime.now().getMillis());
        entity.setOwner(contextProv.get().getPrincipal().to(RegisteredUser.class));

        Resource r = entity.getResource();
        if ((r == null) || Strings.isNullOrEmpty(r.getId())) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_RESOURCE)
                    .devMessage("Resource was null");
        }

        if (r.isCollection()) {
            Collection c = crepo.get(r.getId());
            if (c == null) {
                throw new ApplicationException()
                        .status(400)
                        .code(ErrorMessageCode.BAD_RESOURCE)
                        .devMessage(String.format("Collection with id '%s' not found", r.getId()));
            }
            entity.setResource(c);
            Share s = super.create(entity);

            c.addShareId(s.getId());
            crepo.update(c.getId(), c);

            log.traceExit(s);
            return s;
        }

        if (r.isFile()) {
            FileResource f = frepo.get(r.getId());
            if (f == null) {
                throw new ApplicationException()
                        .status(400)
                        .code(ErrorMessageCode.BAD_RESOURCE)
                        .devMessage(String.format("FileResource with id '%s' not found", r.getId()));
            }
            entity.setResource(f);
            Share s = super.create(entity);

            f.addShareId(s.getId());
            frepo.update(f.getId(), f);

            log.traceExit(s);
            return s;
        }

        throw new ApplicationException()
                .status(400)
                .code(ErrorMessageCode.BAD_RESOURCE)
                .devMessage("Unable to dispatch resource type");
    }

}
