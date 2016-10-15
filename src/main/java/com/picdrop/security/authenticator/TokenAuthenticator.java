/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authenticator;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nimbusds.jwt.JWTClaimsSet;
import com.picdrop.helper.HttpHelper;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import com.picdrop.security.token.WebTokenFactory;
import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import org.joda.time.DateTime;

/**
 *
 * @author i330120
 */
public class TokenAuthenticator implements Authenticator {

    String authCookieName;
    WebTokenFactory tfactory;
    Repository<String, RegisteredUser> userRepo;

    @Inject
    public TokenAuthenticator(
            Repository<String, RegisteredUser> userRepo,
            @Named("service.session.cookie.name") String authCookieName,
            WebTokenFactory tfactory) {
        this.authCookieName = authCookieName;
        this.tfactory = tfactory;
        this.userRepo = userRepo;
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
            if (claims.getExpirationTime().before(DateTime.now().toDate())) { // expired
                // TODO Log
                return null;
            }

            String sub = claims.getSubject();
            if (Strings.isNullOrEmpty(sub)) {
                // TODO log
                return null;
            }
            return userRepo.get(sub);
        } catch (IOException | ParseException ex) {
            // Log
            return null;
        }
    }

}
