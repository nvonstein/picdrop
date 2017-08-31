/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.picdrop.exception.ApplicationException;
import com.picdrop.exception.ErrorMessageCode;
import com.picdrop.model.RequestContext;
import com.picdrop.model.Share;
import com.picdrop.model.TokenSet;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.AdvancedRepository;
import com.picdrop.repository.Repository;
import java.util.regex.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import com.picdrop.security.authentication.Permission;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author i330120
 */
@Path("/app/users")
@Consumes("application/json")
@Produces("application/json")
public class RegisteredUserService {

    Logger log = LogManager.getLogger(this.getClass());

    Repository<String, RegisteredUser> repo;
    AdvancedRepository<String, TokenSet> tsrepo;
    AdvancedRepository<String, FileResource> frepo;
    AdvancedRepository<String, Collection> crepo;
    AdvancedRepository<String, Share> srepo;
    AdvancedRepository<String, Collection.CollectionItem> cirepo;

    @Inject
    Provider<RequestContext> contextProv;

    Pattern emailPattern = Pattern.compile("^[^@]+[@][^@]+[.][^@]+$");

    @Inject
    public RegisteredUserService(Repository<String, RegisteredUser> repo,
            AdvancedRepository<String, TokenSet> tsrepo,
            AdvancedRepository<String, FileResource> frepo,
            AdvancedRepository<String, Collection> crepo,
            AdvancedRepository<String, Collection.CollectionItem> cirepo,
            AdvancedRepository<String, Share> srepo) {
        this.repo = repo;
        this.tsrepo = tsrepo;
        this.frepo = frepo;
        this.crepo = crepo;
        this.cirepo = cirepo;
        this.srepo = srepo;
        log.trace("created with ({},{},{},{},{},{})", repo, tsrepo, frepo, crepo, cirepo, srepo);
    }

    @Inject
    public void setEmailPattern(@Named("picdrop.validation.email.regex") String pattern) {
        emailPattern = Pattern.compile(pattern);
    }

    @POST
    @Path("/")
    public RegisteredUser create(RegisteredUser entity) throws ApplicationException {
        log.entry(entity);
        if (entity == null) {
            throw new ApplicationException()
                    .status(400)
                    .code(ErrorMessageCode.BAD_REQUEST_BODY);
        }

        if (Strings.isNullOrEmpty(entity.getPhash())) {
            throw new ApplicationException()
                    .code(ErrorMessageCode.BAD_PHASH)
                    .status(400);
        }
        if (Strings.isNullOrEmpty(entity.getEmail())) {
            throw new ApplicationException()
                    .code(ErrorMessageCode.BAD_EMAIL)
                    .status(400);
        } else if (!emailPattern.matcher(entity.getEmail()).matches()) {
            throw new ApplicationException()
                    .code(ErrorMessageCode.BAD_EMAIL)
                    .status(400);
        }
        if (Strings.isNullOrEmpty(entity.getName())) {
            entity.setName("PicdropUser");
        }

        return log.traceExit(repo.save(entity));
    }

    @GET
    @Path("/me")
    @Permission("read")
    public User getMe() {
        log.traceEntry();
        return log.traceExit(contextProv.get().getPrincipal());
    }

    @DELETE
    @Path("/me")
    @Permission("write")
    public void deleteMe() {
        log.traceEntry();
        User me = contextProv.get().getPrincipal();
        if (me != null) {
            repo.delete(me.getId());
            try {
                tsrepo.deleteNamed("with.owner", me.getId());
                cirepo.deleteNamed("with.owner", me.getId());
                crepo.deleteNamed("with.owner", me.getId());
                frepo.deleteNamed("with.owner", me.getId()); // TODO remove files
                srepo.deleteNamed("with.owner", me.getId());
            } catch (IOException ex) {
                log.warn("Error during invalidation of existing tokens", ex);
            }
        }
        log.traceExit();
    }

    @PUT
    @Path("/me")
    @Permission("write")
    public RegisteredUser updateMe(RegisteredUser entity) throws ApplicationException {
        log.entry(entity);
        RegisteredUser me = contextProv.get().getPrincipal().to(RegisteredUser.class);
        if (me == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage("No principal set");
        }
        try {
            me = me.merge(entity);
        } catch (IOException ex) {
            throw new ApplicationException(ex)
                    .status(500)
                    .code(ErrorMessageCode.ERROR_OBJ_MERGE)
                    .devMessage(ex.getMessage());
        }
        return log.traceExit(repo.update(me.getId(), me));
    }

}
