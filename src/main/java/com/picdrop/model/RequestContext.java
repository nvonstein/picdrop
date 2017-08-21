/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.picdrop.model.user.User;
import org.jboss.resteasy.plugins.guice.RequestScoped;

/**
 *
 * @author i330120
 */
@RequestScoped
public class RequestContext {

    User principal;

    public User getPrincipal() {
        return principal;
    }

    public void setPrincipal(User principal) {
        this.principal = principal;
    }

    public boolean hasPrincipal() {
        return this.principal != null;
    }
}
