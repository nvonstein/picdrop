/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authentication.authenticator;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author i330120
 */
public interface Authenticator<T> {
    
    T authenticate(HttpServletRequest request);
}
