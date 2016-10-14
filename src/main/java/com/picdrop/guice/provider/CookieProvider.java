/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import javax.ws.rs.core.NewCookie;

/**
 *
 * @author i330120
 */
public interface CookieProvider {
   NewCookie get(); 
}
