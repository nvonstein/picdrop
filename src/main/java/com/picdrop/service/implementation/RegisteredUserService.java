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
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import java.util.regex.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import com.picdrop.security.authentication.Authenticated;
import com.picdrop.security.authentication.RoleType;
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

    @Inject
    Provider<RequestContext> contextProv;

    Pattern emailPattern = Pattern.compile("^[^@]+[@][^@]+[.][^@]+$");

    @Inject
    public RegisteredUserService(Repository<String, RegisteredUser> repo) {
        this.repo = repo;
        log.trace("created with ({})", repo);
    }

    @Inject
    public void setEmailPattern(@Named("picdrop.validation.email.regex") String pattern) {
        emailPattern = Pattern.compile(pattern);
    }

    @POST
    @Path("/")
    public RegisteredUser create(RegisteredUser entity) throws ApplicationException {
        log.entry(entity);
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
    @Authenticated(include = {RoleType.REGISTERED, RoleType.USER})
    public User getMe() {
        log.traceEntry();
        return log.traceExit(contextProv.get().getPrincipal());
    }

    @DELETE
    @Path("/me")
    @Authenticated(include = {RoleType.REGISTERED})
    public void deleteMe() {
        log.traceEntry();
        User me = contextProv.get().getPrincipal();
        if (me != null) {
            repo.delete(me.getId());
        }
        log.traceExit();
    }

    @PUT
    @Path("/me")
    @Authenticated(include = {RoleType.REGISTERED})
    public RegisteredUser updateMe(RegisteredUser entity) throws ApplicationException {
        log.entry(entity);
        User me = contextProv.get().getPrincipal();
        if (me == null) {
            throw new ApplicationException()
                    .status(404)
                    .code(ErrorMessageCode.NOT_FOUND)
                    .devMessage("No principal set");
        }
        return log.traceExit(repo.update(me.getId(), entity));
    }

}
