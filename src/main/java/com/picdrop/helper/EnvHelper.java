/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import com.google.common.io.Files;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author i330120
 */
public abstract class EnvHelper {

    public static Map<String, String> getProperties() {
        Map<String, String> p = new HashMap<>();

        p.put("picdrop.validation.email.regex", "^[^@]+[@][^@]+[.][^@]+$");

        p.put("service.cookie.enabled", "true");
        p.put("service.cookie.domain", "localhost");
        p.put("service.cookie.maxage", "900");
        p.put("service.cookie.http", "true");
        p.put("service.cookie.secure", "false");
        
        p.put("service.cookie.auth.name", "auth");
        p.put("service.cookie.refresh.name", "refresh");
        
        p.put("service.jwt.auth.exp", "60"); // 1 Hour
//        p.put("service.session.jwt.refresh.exp", "43200");    // 30 Days
        p.put("service.jwt.refresh.exp", "3");    // 30 Days
        p.put("service.jwt.iss", "picdrop");
        p.put("service.jwt.aud", "picdrop/app");
        
        p.put("token.signer.alg", "HS256");
        p.put("token.cipher.alg", "dir");
        p.put("token.cipher.meth", "A128CBC-HS256");
        
        p.put("service.file.store", "/Users/nvonstein/picdrop/store");

        p.put("service.upload.store", "/Users/nvonstein/picdrop/uploads"); 
        p.put("service.upload.maxmemory", "100000000"); // 100 MB
        p.put("service.upload.maxfilesize", "10000000"); // 10 MB
        p.put("service.upload.maxrequestsize", "100000000"); // 100 MB
        
        p.put("service.json.view", "public");

        return p;
    }
    
    public static Map<String, String> getPropertiesTest() {
        Map<String, String> p = new HashMap<>();

        p.put("picdrop.validation.email.regex", "^[^@]+[@][^@]+[.][^@]+$");

        p.put("service.session.cookie.enabled", "true");
        p.put("service.session.cookie.name", "token");
        p.put("service.session.cookie.domain", "localhost");
        p.put("service.session.cookie.maxage", "900");
        p.put("service.session.cookie.http", "true");
        p.put("service.session.cookie.secure", "false");
        p.put("service.session.jwt.exp", "900");
        p.put("service.session.jwt.iss", "picdrop");
        
        p.put("token.signer.alg", "HS256");
        p.put("token.cipher.alg", "dir");
        p.put("token.cipher.meth", "A128CBC-HS256");
        
        p.put("service.file.store", "");

        p.put("service.upload.store", Files.createTempDir().getAbsolutePath()); 
        p.put("service.upload.maxmemory", "100000000"); // 100 MB
        p.put("service.upload.maxfilesize", "10000000"); // 10 MB
        p.put("service.upload.maxrequestsize", "100000000"); // 100 MB
        
        p.put("service.json.view", "public");

        return p;
    }
}
