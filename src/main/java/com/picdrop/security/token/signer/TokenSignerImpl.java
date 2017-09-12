/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token.signer;

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

    final JWSSigner sign;
    final JWSVerifier verif;

    public TokenSignerImpl(JWSAlgorithm alg, JWSSigner sign, JWSVerifier verif) {
        this.alg = alg;
        this.sign = sign;
        this.verif = verif;
    }

    public TokenSignerImpl(String alg, JWSSigner sign, JWSVerifier verif) {
        this.alg = JWSAlgorithm.parse(alg);
        this.sign = sign;
        this.verif = verif;
    }

    @Override
    public SignedJWT sign(JWTClaimsSet claims) throws IOException {
        SignedJWT sjwt = new SignedJWT(new JWSHeader(alg), claims);
        try {
            sjwt.sign(sign);
        } catch (JOSEException ex) {
            throw new IOException("Unable to sign token: " + ex.getMessage(), ex);
        }
        return sjwt;
    }

    @Override
    public boolean verify(SignedJWT sjwt) throws IOException {
        if (sjwt == null) {
            return false;
        }
        try {
            return sjwt.verify(verif);
        } catch (JOSEException ex) {
            return false;
        }
    }

}
