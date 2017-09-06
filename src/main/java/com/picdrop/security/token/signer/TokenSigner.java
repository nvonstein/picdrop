/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token.signer;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public interface TokenSigner {
    SignedJWT sign(JWTClaimsSet claims) throws IOException;
    boolean verify(SignedJWT jwt) throws IOException;
}
