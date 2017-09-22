/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authentication.authenticator;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nimbusds.jwt.JWTClaimsSet;
import com.picdrop.helper.HttpHelper;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.security.token.ClaimSetFactory;
import com.picdrop.security.token.WebTokenFactory;
import java.io.IOException;
import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author i330120
 */
public class TokenAuthenticator implements Authenticator<RegisteredUser> {

    String authCookieName;
    WebTokenFactory tfactory;
    ClaimSetFactory<RegisteredUser> csFac;

    @Inject
    public TokenAuthenticator(
            @Named("service.session.cookie.name") String authCookieName,
            WebTokenFactory tfactory,
            ClaimSetFactory<RegisteredUser> csFactory) {
        this.authCookieName = authCookieName;
        this.tfactory = tfactory;
        this.csFac = csFactory;
    }

    @Override
    public RegisteredUser authenticate(HttpServletRequest request) {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.isNullOrEmpty(auth)) {
            auth = HttpHelper.getCookieValue(authCookieName, request.getCookies());
        }
        if (Strings.isNullOrEmpty(auth)) {
            return null;
        }

        if (!auth.contains("Bearer")) {
            return null;
        }

        String rawtoken = auth.replace("Bearer ", "");

        try {
            JWTClaimsSet claims = tfactory.parseToken(rawtoken);

            return this.csFac.verify(claims);
        } catch (IOException | ParseException ex) {
            // Log
            return null;
        }
    }

}
