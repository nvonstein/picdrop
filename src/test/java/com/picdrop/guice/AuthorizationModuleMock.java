/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.picdrop.model.RequestContext;

/**
 *
 * @author nvonstein
 */
public class AuthorizationModuleMock extends AuthorizationModule {
    
    RequestContext ctx;

    public AuthorizationModuleMock(RequestContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void bindRequestContext(Binder binder) {
       binder.bind(RequestContext.class).toInstance(ctx);
    }  
}
