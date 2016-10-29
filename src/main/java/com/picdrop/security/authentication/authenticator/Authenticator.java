/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authentication.authenticator;

import com.picdrop.model.user.RegisteredUser;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author i330120
 */
public interface Authenticator {
    
    RegisteredUser authenticate(HttpServletRequest request);
}
