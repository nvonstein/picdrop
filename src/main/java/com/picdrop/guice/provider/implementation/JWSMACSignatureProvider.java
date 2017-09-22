/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.Inject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.picdrop.guice.names.Signature;
import com.picdrop.guice.provider.JWSSignatureProvider;
import com.picdrop.guice.provider.SymmetricKeyProvider;
import java.io.IOException;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public abstract class JWSMACSignatureProvider {

    Logger log = LogManager.getLogger();

    private final SymmetricKeyProvider symKProv;
    private final SecretKey KEY;

    private boolean isInit = false;

    protected JWSSigner signer;
    protected JWSVerifier verfier;

    JWSMACSignatureProvider(SymmetricKeyProvider symKProv) {
        this.symKProv = symKProv;
        this.KEY = null;
    }

    public JWSMACSignatureProvider(SecretKey key) {
        this.KEY = key;
        this.symKProv = null;
    }

    protected void init() throws IOException, KeyLengthException, JOSEException {
        if (!isInit) {
            SecretKey lkey = (this.KEY == null)
                    ? symKProv.get()
                    : this.KEY;

            this.signer = new MACSigner(lkey);
            this.verfier = new MACVerifier(lkey);
            isInit = true;
        }
    }

    public static class SignerProvider extends JWSMACSignatureProvider implements JWSSignatureProvider.SignerCheckedProvider {

        @Inject
        SignerProvider(@Signature SymmetricKeyProvider symKProv) {
            super(symKProv);
            this.log = LogManager.getLogger();
        }

        public SignerProvider(SecretKey key) {
            super(key);
            this.log = LogManager.getLogger();
        }

        @Override
        public JWSSigner get() throws IOException {
            try {
                init();
            } catch (JOSEException ex) {
                log.fatal("Invalid key size", ex);
                throw new IOException(ex.getMessage(), ex);
            }
            return this.signer;
        }

    }

    public static class VerifierProvider extends JWSMACSignatureProvider implements JWSSignatureProvider.VerifierCheckedProvider {

        @Inject
        VerifierProvider(@Signature SymmetricKeyProvider symKProv) {
            super(symKProv);
            this.log = LogManager.getLogger();
        }

        public VerifierProvider(SecretKey key) {
            super(key);
            this.log = LogManager.getLogger();
        }

        @Override
        public JWSVerifier get() throws IOException {
            try {
                init();
            } catch (JOSEException ex) {
                log.fatal("Invalid key size", ex);
                throw new IOException(ex.getMessage(), ex);
            }
            return this.verfier;
        }

    }

}
