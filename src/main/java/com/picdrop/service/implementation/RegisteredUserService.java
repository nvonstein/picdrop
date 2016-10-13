/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
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
@Path("users")
@Consumes("application/json")
@Produces("application/json")
public class RegisteredUserService {

    Repository<String, RegisteredUser> repo;

    Pattern emailPattern = Pattern.compile("^[^@]+[@][^@]+[.][^@]+$"); // TODO inject

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
    public RegisteredUser getMe() {
        return null;
    }

    @DELETE
    @Path("/me")
    public void deleteMe() {
    }

    @PUT
    @Path("/me")
    public RegisteredUser updateMe() {
        return null;
    }

}
