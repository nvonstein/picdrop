/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.picdrop.model.user.RegisteredUser;
import org.jboss.resteasy.plugins.guice.RequestScoped;

/**
 *
 * @author i330120
 */
@RequestScoped
public class RequestContext {

    RegisteredUser principal;

    public RegisteredUser getPrincipal() {
        return principal;
    }

    public void setPrincipal(RegisteredUser principal) {
        this.principal = principal;
    }

}
