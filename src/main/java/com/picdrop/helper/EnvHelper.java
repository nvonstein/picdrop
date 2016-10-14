/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author i330120
 */
public abstract class EnvHelper {
    
    public static Map<String,String> getProperties() {
        Map<String,String> p = new HashMap<>();
        
        p.put("picdrop.validation.email.regex", "^[^@]+[@][^@]+[.][^@]+$");
        
        p.put("service.session.cookie.name", "token");
        p.put("service.session.cookie.maxage", "900");
        p.put("service.session.cookie.http", "true");
        p.put("service.session.cookie.secure", "false");
        
        
        return p;
    }
}
