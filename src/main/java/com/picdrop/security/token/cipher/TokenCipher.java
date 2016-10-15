/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token.cipher;

import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author i330120
 */
public interface TokenCipher {

    JWEObject encrypt(Payload pl) throws IOException;

    JWEObject encrypt(Payload pl, String contenttype) throws IOException;

    Payload decrypt(String raw) throws IOException, ParseException;

    Payload decrypt(JWEObject jew) throws IOException;
}
