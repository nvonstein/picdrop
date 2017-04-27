/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Provider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;

/**
 *
 * @author nvonstein
 */
public abstract class JWSTokenMACSignerVerifierProvider {

    protected final JWSSigner signer;
    protected final JWSVerifier verfier;

    private static byte[] key = null;

    public JWSTokenMACSignerVerifierProvider() throws KeyLengthException, JOSEException, NoSuchAlgorithmException {
//        String key = "D4271D5AE4D8ADC966A35EC11F1F5";
        generateKey();
        this.signer = new MACSigner(key);
        this.verfier = new MACVerifier(key);
    }

    private static void generateKey() throws NoSuchAlgorithmException {
        if (key == null) {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            key = keyGen.generateKey().getEncoded();
        }
    }

    public static class JWSTokenMACSignerProvider extends JWSTokenMACSignerVerifierProvider implements Provider<JWSSigner> {

        public JWSTokenMACSignerProvider() throws KeyLengthException, JOSEException, NoSuchAlgorithmException {
            super();
        }

        @Override
        public JWSSigner get() {
            return super.signer;
        }

    }

    public static class JWSTokenMACVerifierProvider extends JWSTokenMACSignerVerifierProvider implements Provider<JWSVerifier> {

        public JWSTokenMACVerifierProvider() throws KeyLengthException, JOSEException, NoSuchAlgorithmException {
            super();
        }

        @Override
        public JWSVerifier get() {
            return super.verfier;
        }

    }

}
