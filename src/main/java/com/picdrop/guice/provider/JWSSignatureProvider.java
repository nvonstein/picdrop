/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Provider;
import com.google.inject.throwingproviders.CheckedProvider;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public interface JWSSignatureProvider {

    public static interface SignerCheckedProvider extends CheckedProvider<JWSSigner> {

        @Override
        public JWSSigner get() throws IOException;
    }

    public static interface VerifierCheckedProvider extends CheckedProvider<JWSVerifier> {

        @Override
        public JWSVerifier get() throws IOException;
    }

    public static interface SignerProvider extends Provider<JWSSigner> {

    }

    public static interface VerifierProvider extends Provider<JWSVerifier> {

    }
}
