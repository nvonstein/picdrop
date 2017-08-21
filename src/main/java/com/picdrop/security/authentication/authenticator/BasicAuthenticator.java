/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authentication.authenticator;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.Repository;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author i330120
 */
public class BasicAuthenticator implements Authenticator<RegisteredUser> {
    
    Repository<String, RegisteredUser> userRepo;

    @Inject
    public BasicAuthenticator(Repository<String, RegisteredUser> userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public RegisteredUser authenticate(@Context HttpServletRequest request) {       
        String authHdr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.isNullOrEmpty(authHdr) || !authHdr.contains("Basic")) {
            return null;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(authHdr.replace("Basic ", ""));
            String[] userAndPass = new String(decoded, "UTF-8").split(":");
            if (userAndPass.length != 2) {
                return null;
            }
            
            List<RegisteredUser> users = userRepo.queryNamed("registeredUser.byEmail", userAndPass[0]);
            
            if (users.isEmpty()) {
                return null;
            }
            
            if (!users.get(0).getPhash().equals(userAndPass[1])) {
                return null;
            }
            
            return users.get(0);
        } catch (Exception e) {
            return null;
        }
    }
    
}
