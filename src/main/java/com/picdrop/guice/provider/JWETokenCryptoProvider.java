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
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public interface JWETokenCryptoProvider {

    public static interface EncrypterCheckedProvider extends CheckedProvider<JWEEncrypter> {

        @Override
        public JWEEncrypter get() throws IOException;
    }

    public static interface DecrypterCheckedProvider extends CheckedProvider<JWEDecrypter> {

        @Override
        public JWEDecrypter get() throws IOException;
    }

    public static interface EncrypterProvider extends Provider<JWEEncrypter> {

    }

    public static interface DecrypterProvider extends Provider<JWEDecrypter> {

    }
}
