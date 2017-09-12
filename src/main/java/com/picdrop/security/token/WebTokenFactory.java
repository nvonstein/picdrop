/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token;

import com.nimbusds.jwt.JWTClaimsSet;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author i330120
 */
public interface WebTokenFactory {
    void init() throws IOException;
    String getToken(JWTClaimsSet claims) throws IOException;
    JWTClaimsSet parseToken(String token) throws IOException, ParseException;
}
