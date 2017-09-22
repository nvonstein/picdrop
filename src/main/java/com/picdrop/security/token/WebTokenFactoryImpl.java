/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.picdrop.guice.provider.TokenCipherProvider;
import com.picdrop.guice.provider.TokenSignerProvider;
import com.picdrop.security.token.cipher.TokenCipher;
import com.picdrop.security.token.signer.TokenSigner;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author i330120
 */
public class WebTokenFactoryImpl implements WebTokenFactory {

    private final TokenCipherProvider tcipherProv;
    private final TokenSignerProvider tsignerProv;

    protected TokenCipher tcipher;
    protected TokenSigner tsigner;

    @Inject
    public WebTokenFactoryImpl(TokenCipherProvider tcipher, TokenSignerProvider tsigner) {
        this.tcipherProv = tcipher;
        this.tsignerProv = tsigner;
    }

    protected boolean isInit() {
        return (this.tcipher != null) && (this.tsigner != null);
    }

    protected void checkInit() {
        if (!isInit()) {
            throw new IllegalStateException("Token factory not initialized");
        }
    }

    @Override
    public String getToken(JWTClaimsSet claims) throws IOException {
        checkInit();
        SignedJWT sjwt = tsigner.sign(claims);
        JWEObject jwe = tcipher.encrypt(new Payload(sjwt), "JWT");

        return jwe.serialize();
    }

    @Override
    public JWTClaimsSet parseToken(String token) throws ParseException, IOException {
        checkInit();
        if (Strings.isNullOrEmpty(token)) {
            throw new IOException("Empty token");
        }

        SignedJWT sjwt = tcipher.decrypt(token).toSignedJWT();
        if (!tsigner.verify(sjwt)) {
            throw new IOException("Invalid signature");
        }

        return sjwt.getJWTClaimsSet();
    }

    @Override
    public void init() throws IOException {
        if (!isInit()) {
            this.tcipher = tcipherProv.get();
            this.tsigner = tsignerProv.get();
        }
    }

}
