/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import java.util.ArrayList;
import java.util.List;
import org.jboss.resteasy.plugins.guice.RequestScoped;

/**
 *
 * @author i330120
 */
@RequestScoped
public class RequestContext {

    protected RegisteredUser principal;
    protected User user;
    protected List<String> permissions = new ArrayList<>();

    public RegisteredUser getPrincipal() {
        return principal;
    }

    public void setPrincipal(RegisteredUser principal) {
        this.principal = principal;
    }

    public boolean hasPrincipal() {
        return this.principal != null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean hasUser() {
        return this.user != null;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public RequestContext addPermission(String permission) {
        this.permissions.add(permission);
        return this;
    }

    public RequestContext addPermission(List<String> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }
}
