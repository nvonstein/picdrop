/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.picdrop.guice.factory.CookieProviderFactory;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.picdrop.guice.provider.CookieProvider;
import com.picdrop.guice.provider.JWETokenDirectEncrypterDecrypterProvider;
import com.picdrop.guice.provider.JWSTokenMACSignerVerifierProvider;
import com.picdrop.model.RequestContext;
import com.picdrop.guice.provider.SessionCookieProvider;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.authentication.authenticator.BasicAuthenticator;
import com.picdrop.security.authentication.authenticator.TokenAuthenticator;
import com.picdrop.security.token.WebTokenFactory;
import com.picdrop.security.token.WebTokenFactoryImpl;
import com.picdrop.security.token.cipher.TokenCipher;
import com.picdrop.security.token.cipher.TokenCipherImpl;
import com.picdrop.security.token.signer.TokenSigner;
import com.picdrop.security.token.signer.TokenSignerImpl;
import com.picdrop.service.filter.AuthenticationFilter;
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

        bindAuthenticators(binder);
        
        bindWebTokenFactory(binder);
        
        bindCipherSignerProviders(binder);
    }
    
    protected void bindSessionCookieFactory(Binder binder) {
        binder.install(new FactoryModuleBuilder()
                .implement(CookieProvider.class, Names.named("cookie.session"), SessionCookieProvider.class)
                .build(CookieProviderFactory.class)
        );
    }
    
    protected void bindAuthenticationFilter(Binder binder) {
        binder.bind(AuthenticationFilter.class);
    }
    
    protected void bindRequestContext(Binder binder) {
        binder.bind(RequestContext.class).in(RequestScoped.class);
    }
    
    protected void bindAuthenticators(Binder binder) {
        binder.bind(Authenticator.class).annotatedWith(Names.named("basic")).to(BasicAuthenticator.class);
        binder.bind(Authenticator.class).annotatedWith(Names.named("token")).to(TokenAuthenticator.class);
    }
    
    protected void bindWebTokenFactory(Binder binder) {
        binder.bind(WebTokenFactory.class).to(WebTokenFactoryImpl.class);
        binder.bind(TokenSigner.class).to(TokenSignerImpl.class);
        binder.bind(TokenCipher.class).to(TokenCipherImpl.class);
    }
    
    protected void bindCipherSignerProviders(Binder binder) {
        binder.bind(JWEDecrypter.class).toProvider(JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectDecrypterProvider.class);
        binder.bind(JWEEncrypter.class).toProvider(JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectEncrypterProvider.class);
        binder.bind(JWSSigner.class).toProvider(JWSTokenMACSignerVerifierProvider.JWSTokenMACSignerProvider.class);
        binder.bind(JWSVerifier.class).toProvider(JWSTokenMACSignerVerifierProvider.JWSTokenMACVerifierProvider.class);
    }
}
