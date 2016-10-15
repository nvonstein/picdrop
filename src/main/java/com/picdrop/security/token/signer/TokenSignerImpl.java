/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token.signer;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public class TokenSignerImpl implements TokenSigner {

    JWSAlgorithm alg;
    @Inject
    Provider<JWSSigner> signerProv;
    @Inject
    Provider<JWSVerifier> verifierProv;

    @Inject
    public TokenSignerImpl(@Named("token.signer.alg") String alg) {
        this.alg = JWSAlgorithm.parse(alg);
    }

    @Override
    public SignedJWT sign(JWTClaimsSet claims) throws IOException {
        SignedJWT sjwt = new SignedJWT(new JWSHeader(alg), claims);
        try {
            sjwt.sign(signerProv.get());
        } catch (JOSEException ex) {
            throw new IOException("Unable to sign token: " + ex.getMessage(), ex);
        }
        return sjwt;
    }

    @Override
    public boolean verify(SignedJWT sjwt) {
        if (sjwt == null) {
            return false;
        }
        try {
            return sjwt.verify(verifierProv.get());
        } catch (JOSEException ex) {
            return false;
        }
    }

}
