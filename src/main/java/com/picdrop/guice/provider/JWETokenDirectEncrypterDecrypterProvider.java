/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Provider;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;

/**
 *
 * @author nvonstein
 */
public abstract class JWETokenDirectEncrypterDecrypterProvider {

    protected final JWEEncrypter encryptor;
    protected final JWEDecrypter decrypter;

    private static byte[] key = null;

    public JWETokenDirectEncrypterDecrypterProvider() throws KeyLengthException, NoSuchAlgorithmException {
//        String key = "999C9A2C572AE8C2A71B2E255FA78";
        generateKey();
        this.encryptor = new DirectEncrypter(key);
        this.decrypter = new DirectDecrypter(key);
    }

    private static void generateKey() throws NoSuchAlgorithmException {
        if (key == null) {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            key = keyGen.generateKey().getEncoded();
        }
    }

    public static class JWETokenDirectEncrypterProvider extends JWETokenDirectEncrypterDecrypterProvider implements Provider<JWEEncrypter> {

        public JWETokenDirectEncrypterProvider() throws KeyLengthException, NoSuchAlgorithmException {
            super();
        }

        @Override
        public JWEEncrypter get() {
            return super.encryptor;
        }

    }

    public static class JWETokenDirectDecrypterProvider extends JWETokenDirectEncrypterDecrypterProvider implements Provider<JWEDecrypter> {

        public JWETokenDirectDecrypterProvider() throws KeyLengthException, NoSuchAlgorithmException {
            super();
        }

        @Override
        public JWEDecrypter get() {
            return super.decrypter;
        }

    }
}
