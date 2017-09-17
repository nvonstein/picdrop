/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.picdrop.guice.names.AuthorizationToken;
import com.picdrop.guice.names.RefreshToken;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.security.authentication.authenticator.Authenticator;
import com.picdrop.security.authentication.authenticator.TokenAuthenticator;
import com.picdrop.security.token.ClaimSetFactory;
import com.picdrop.security.token.WebTokenFactory;

/**
 *
 * @author i330120
 */
public final class AuthorizationModule extends AbstractAuthorizationModule {


    @Provides
    @AuthorizationToken
    @Singleton
    @Override
    Authenticator<RegisteredUser> provideAuthTokenAuthenticator(
            WebTokenFactory tfactory,
            @Named("service.cookie.auth.name") String authCookieName,
            @AuthorizationToken ClaimSetFactory<RegisteredUser> f) {
        return new TokenAuthenticator(authCookieName, tfactory, f);
    }

    @Provides
    @RefreshToken
    @Singleton
    @Override
    Authenticator<RegisteredUser> provideRefreshTokenAuthenticator(
            WebTokenFactory tfactory,
            @Named("service.cookie.refresh.name") String authCookieName,
            @RefreshToken ClaimSetFactory<RegisteredUser> f) {
        return new TokenAuthenticator(authCookieName, tfactory, f);
    }
}
