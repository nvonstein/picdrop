/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.picdrop.guice.names.AuthorizationToken;
import com.picdrop.guice.names.RefreshToken;
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.token.ClaimSetFactory;
import com.picdrop.security.token.WebTokenFactory;

/**
 *
 * @author nvonstein
 */
public class AuthorizationModuleMock extends AbstractAuthorizationModule {

    RequestContext ctx;
    AuthorizationModule authModule = new AuthorizationModule();

    public AuthorizationModuleMock(RequestContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void bindRequestContext(Binder binder) {
        binder.bind(RequestContext.class).toInstance(ctx);
    }

    @Provides
    @Singleton
    @AuthorizationToken
    @Override
    Authenticator<RegisteredUser> provideAuthTokenAuthenticator(WebTokenFactory tfactory, String authCookieName, ClaimSetFactory<RegisteredUser> f) {
        return authModule.provideAuthTokenAuthenticator(tfactory, authCookieName, f);
    }

    @Provides
    @Singleton
    @RefreshToken
    @Override
    Authenticator<RegisteredUser> provideRefreshTokenAuthenticator(WebTokenFactory tfactory, String authCookieName, ClaimSetFactory<RegisteredUser> f) {
        return authModule.provideRefreshTokenAuthenticator(tfactory, authCookieName, f);
    }

}
