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
import com.picdrop.annotations.Authorized;
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import java.util.regex.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author i330120
 */
@Path("/app/users")
@Consumes("application/json")
@Produces("application/json")
public class RegisteredUserService {

    Repository<String, RegisteredUser> repo;

    @Inject
    Provider<RequestContext> contextProv;

    Pattern emailPattern = Pattern.compile("^[^@]+[@][^@]+[.][^@]+$");

    @Inject
    public RegisteredUserService(Repository<String, RegisteredUser> repo) {
        this.repo = repo;
    }

    @Inject
    public void setEmailPattern(@Named("picdrop.validation.email.regex") String pattern) {
        emailPattern = Pattern.compile(pattern);
    }

    @POST
    @Path("/")
    public RegisteredUser create(RegisteredUser entity) {
        if (Strings.isNullOrEmpty(entity.getPhash())) {
            throw new IllegalArgumentException("no phash provided"); // 400
        }
        if (Strings.isNullOrEmpty(entity.getEmail())) {
            throw new IllegalArgumentException("no email provided"); // 400
        } else if (!emailPattern.matcher(entity.getEmail()).matches()) {
            throw new IllegalArgumentException("invalid email provided"); // 400
        }
        if (Strings.isNullOrEmpty(entity.getName())) {
            entity.setName("PicdropUser");
        }

        return repo.save(entity);
    }

    @GET
    @Path("/me")
    @Authorized
    public RegisteredUser getMe() {
        return contextProv.get().getPrincipal();
    }

    @DELETE
    @Path("/me")
    @Authorized
    public void deleteMe() {
        RegisteredUser me = contextProv.get().getPrincipal();
        if (me != null) {
            repo.delete(me.getId());
        }
    }

    @PUT
    @Path("/me")
    @Authorized
    public RegisteredUser updateMe(RegisteredUser entity) {
        RegisteredUser me = contextProv.get().getPrincipal();
        if (me == null) {
            return null; // 404
        }
        return repo.update(me.getId(), entity);
    }

}
