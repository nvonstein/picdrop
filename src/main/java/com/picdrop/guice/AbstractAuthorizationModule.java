/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.picdrop.guice.factory.CookieProviderFactory;
import com.picdrop.guice.names.AuthorizationToken;
import com.picdrop.guice.names.Credentials;
import com.picdrop.guice.names.RefreshToken;
import com.picdrop.guice.names.Session;
import com.picdrop.guice.provider.CookieProvider;
import com.picdrop.guice.provider.implementation.SessionCookieProvider;
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.authentication.authenticator.BasicAuthenticator;
import com.picdrop.security.token.AuthTokenClaimSetFactory;
import com.picdrop.security.token.ClaimSetFactory;
import com.picdrop.security.token.RefreshTokenClaimSetFactory;
import com.picdrop.security.token.WebTokenFactory;
import com.picdrop.security.token.WebTokenFactoryImpl;
import com.picdrop.service.filter.PermissionAuthenticationFilter;
import com.picdrop.service.filter.ShareRewriteFilter;
import org.jboss.resteasy.plugins.guice.RequestScoped;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractAuthorizationModule implements Module {

    @Override
    public void configure(Binder binder) {
        // Session management
        bindSessionCookieFactory(binder);
        // Authorization
        bindAuthenticationFilter(binder);
        bindRequestContext(binder);
        bindClaimSetFactories(binder);
        bindAuthenticators(binder);
        bindWebTokenFactory(binder);
    }

    protected void bindSessionCookieFactory(Binder binder) {
        binder.install(new FactoryModuleBuilder().implement(CookieProvider.class, Session.class, SessionCookieProvider.class).build(CookieProviderFactory.class));
    }

    protected void bindAuthenticationFilter(Binder binder) {
        binder.bind(PermissionAuthenticationFilter.class);
        binder.bind(ShareRewriteFilter.class);
    }

    protected void bindRequestContext(Binder binder) {
        binder.bind(RequestContext.class).in(RequestScoped.class);
    }

    protected void bindAuthenticators(Binder binder) {
        binder.bind(new TypeLiteral<Authenticator<RegisteredUser>>() {
        }).annotatedWith(Credentials.class).to(BasicAuthenticator.class);
    }

    protected void bindClaimSetFactories(Binder binder) {
        binder.bind(new TypeLiteral<ClaimSetFactory<RegisteredUser>>() {
        }).annotatedWith(AuthorizationToken.class).to(AuthTokenClaimSetFactory.class);
        binder.bind(new TypeLiteral<ClaimSetFactory<RegisteredUser>>() {
        }).annotatedWith(RefreshToken.class).to(RefreshTokenClaimSetFactory.class);
    }

    protected void bindWebTokenFactory(Binder binder) {
        binder.bind(WebTokenFactory.class).to(WebTokenFactoryImpl.class).asEagerSingleton();
    }

    abstract Authenticator<RegisteredUser> provideAuthTokenAuthenticator(WebTokenFactory tfactory, String authCookieName, @AuthorizationToken ClaimSetFactory<RegisteredUser> f);

    abstract Authenticator<RegisteredUser> provideRefreshTokenAuthenticator(WebTokenFactory tfactory, String authCookieName, @RefreshToken ClaimSetFactory<RegisteredUser> f);

}
