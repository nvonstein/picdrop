/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.picdrop.exception.ApplicationException;
import com.picdrop.exception.ErrorMessageCode;
import com.picdrop.model.RequestContext;
import com.picdrop.model.Share;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.Repository;
import com.picdrop.security.authentication.Permission;
import com.picdrop.service.CrudService;
import static com.picdrop.helper.LogHelper.*;
import java.io.IOException;
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
    Repository<String, Collection.CollectionItem> cirepo;

    @Inject
    Provider<RequestContext> contextProv;

    @Inject
    public ShareService(AwareRepository<String, Share, User> repo,
            Repository<String, Collection> crepo,
            Repository<String, Collection.CollectionItem> cirepo,
            Repository<String, FileResource> frepo) {
        super(repo);

        this.crepo = crepo;
        this.frepo = frepo;
        this.cirepo = cirepo;

        log.trace(SERVICE, "created with ({},{},{},{})", repo, crepo, cirepo, frepo);
    }

    @PUT
    @Path("/{id}")
    @Permission("write")
    @Override
    public Share update(@PathParam("id") String id, Share entity) throws ApplicationException {
        log.entry(id, entity);
        if (entity == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_REQUEST_BODY);
        }

        Share s = this.getAware(id);

        log.debug(SERVICE, "Performing object merge");
        try {
            s = s.merge(entity);
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(500)
                    .code(ErrorMessageCode.ERROR_OBJ_MERGE)
                    .devMessage(ex.getMessage());
        }

        s = super.update(id, s);

        log.info(SERVICE, "Share updated");
        log.traceExit();
        return s;
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
        log.info(SERVICE, "Share found");
        log.traceExit(s);
        return s;
    }

    protected Share getAware(String id) throws ApplicationException {
        Share s = this.repo.get(id);
        if (s == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage(String.format("Object with id '%s' not found", id));
        }
        return s;
    }

    @DELETE
    @Path("/{id}")
    @Permission("write")
    @Override
    public void delete(@PathParam("id") String id) throws ApplicationException {
        log.entry(id);
        Share s = this.getAware(id);

        repo.delete(id);

        log.debug(SERVICE, "Deleting share reference on resources");
        Resource r = s.getResource(false);
        if (r != null) {
            r.deleteShare(s);
            if (r.isCollection()) {
                this.crepo.update(r.getId(), (Collection) r);
            }

            if (r.isFileResource()) {
                this.frepo.update(r.getId(), (FileResource) r);
            }
        } else {
            log.warn(SERVICE, "Share without resolvable resource detected. Id: '{}'", s.getResource().toResourceString());
        }
        log.info(SERVICE, "Share deleted");
        log.traceExit();
    }

    @GET
    @Permission("read")
    @Override
    public List<Share> list() throws ApplicationException {
        return super.list();
    }

    @POST
    @Permission("write")
    @Override
    public Share create(Share entity) throws ApplicationException {
        log.entry(entity);
        if (entity == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_REQUEST_BODY);
        }

        entity.setCreated(DateTime.now().getMillis());
        entity.setOwner(contextProv.get().getPrincipal().to(RegisteredUser.class));

        log.debug(SERVICE, "Validating share's attributes");
        if (entity.getResource() == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_RESOURCE)
                    .devMessage("Resource was null");
        }

        Resource r = entity.getResource(false);
        if (r == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_RESOURCE)
                    .devMessage("Resource not found");
        }

        // TODO generate uri
        Share s = super.create(entity);

        log.debug(SERVICE, "Adding share reference to resource");
        r = r.addShare(s);

        if (r.isCollection()) {
            this.crepo.update(r.getId(), (Collection) r);
            log.info(SERVICE, "Share created");
            log.traceExit();
            return s;
        }

        if (r.isFileResource()) {
            this.frepo.update(r.getId(), (FileResource) r);
            log.info(SERVICE, "Share updated");
            log.traceExit();
            return s;
        }

        throw new ApplicationException()
                .status(400)
                .code(ErrorMessageCode.BAD_RESOURCE)
                .devMessage("Unable to dispatch resource type");
    }

}
