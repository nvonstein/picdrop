/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.picdrop.guice.factory.CookieProviderFactory;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.picdrop.guice.provider.CookieProvider;
import com.picdrop.model.RequestContext;
import com.picdrop.guice.provider.SessionCookieProvider;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.authentication.authenticator.BasicAuthenticator;
import com.picdrop.security.authentication.authenticator.TokenAuthenticator;
import com.picdrop.security.token.AuthTokenClaimSetFactory;
import com.picdrop.security.token.ClaimSetFactory;
import com.picdrop.security.token.RefreshTokenClaimSetFactory;
import com.picdrop.security.token.WebTokenFactory;
import com.picdrop.security.token.WebTokenFactoryImpl;
import com.picdrop.security.token.cipher.TokenCipher;
import com.picdrop.security.token.cipher.TokenCipherImpl;
import com.picdrop.security.token.signer.TokenSigner;
import com.picdrop.security.token.signer.TokenSignerImpl;
import com.picdrop.service.filter.PermissionAuthenticationFilter;
import com.picdrop.service.filter.ShareRewriteFilter;
import org.jboss.resteasy.plugins.guice.RequestScoped;

/**
 *
 * @author i330120
 */
public class AuthorizationModule implements Module {

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
        binder.install(new FactoryModuleBuilder()
                .implement(CookieProvider.class, Names.named("service.cookie.factory"), SessionCookieProvider.class)
                .build(CookieProviderFactory.class)
        );
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
        }).annotatedWith(Names.named("authenticator.basic")).to(BasicAuthenticator.class);
    }

    protected void bindClaimSetFactories(Binder binder) {
        binder.bind(new TypeLiteral<ClaimSetFactory<RegisteredUser>>() {
        }).annotatedWith(Names.named("claimset.factory.auth")).to(AuthTokenClaimSetFactory.class);
        binder.bind(new TypeLiteral<ClaimSetFactory<RegisteredUser>>() {
        }).annotatedWith(Names.named("claimset.factory.refresh")).to(RefreshTokenClaimSetFactory.class);
    }

    protected void bindWebTokenFactory(Binder binder) {
        binder.bind(WebTokenFactory.class).to(WebTokenFactoryImpl.class).in(Singleton.class);
    }

    @Provides
    @Named("authenticator.token.auth")
    Authenticator<RegisteredUser> provideAuthTokenAuthenticator(
            WebTokenFactory tfactory,
            @Named("service.cookie.auth.name") String authCookieName,
            @Named("claimset.factory.auth") ClaimSetFactory<RegisteredUser> f) {
        return new TokenAuthenticator(authCookieName, tfactory, f);
    }

    @Provides
    @Named("authenticator.token.refresh")
    Authenticator<RegisteredUser> provideRefreshTokenAuthenticator(
            WebTokenFactory tfactory,
            @Named("service.cookie.refresh.name") String authCookieName,
            @Named("claimset.factory.refresh") ClaimSetFactory<RegisteredUser> f) {
        return new TokenAuthenticator(authCookieName, tfactory, f);
    }
}
