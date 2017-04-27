/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token;

import com.picdrop.security.token.cipher.TokenCipher;
import com.picdrop.security.token.signer.TokenSigner;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author i330120
 */
public class WebTokenFactoryImpl implements WebTokenFactory {

    TokenCipher tcipher;
    TokenSigner tsigner;

    @Inject
    public WebTokenFactoryImpl(TokenCipher tcipher, TokenSigner tsigner) {
        this.tcipher = tcipher;
        this.tsigner = tsigner;
    }

    @Override
    public String getToken(JWTClaimsSet claims) throws IOException {
        SignedJWT sjwt = tsigner.sign(claims);
        JWEObject jwe = tcipher.encrypt(new Payload(sjwt),"JWT");

        return jwe.serialize();
    }

    @Override
    public JWTClaimsSet parseToken(String token) throws ParseException, IOException {
        if (Strings.isNullOrEmpty(token)) {
            throw new IOException("Empty token");
        }

        SignedJWT sjwt = tcipher.decrypt(token).toSignedJWT();
        if (!tsigner.verify(sjwt)) {
            throw new IOException("Invalid signature");
        }

        return sjwt.getJWTClaimsSet();
    }

}
