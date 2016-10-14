/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authenticator;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.helper.HttpHelper;
import com.picdrop.model.user.RegisteredUser;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author i330120
 */
public class TokenAuthenticator implements Authenticator {

    String authCookieName;

    @Inject
    public TokenAuthenticator(@Named("service.session.cookie.name") String authCookieName) {
        this.authCookieName = authCookieName;
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
        
        // Parse JWT
        // validate JWT
        // fetch user from 'sub' field
        
        return null; // return user;
    }

}
