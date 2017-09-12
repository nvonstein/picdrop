/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import java.io.IOException;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public abstract class JWSTokenMACSignerVerifierProvider {

    Logger log = LogManager.getLogger();

    private final SymmetricKeyProvider symKProv;
    private final SecretKey KEY;

    private boolean isInit = false;

    protected JWSSigner signer;
    protected JWSVerifier verfier;

    JWSTokenMACSignerVerifierProvider(SymmetricKeyProvider symKProv) {
        this.symKProv = symKProv;
        this.KEY = null;
    }

    public JWSTokenMACSignerVerifierProvider(SecretKey key) {
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

    public static class JWSTokenMACSignerProvider extends JWSTokenMACSignerVerifierProvider implements JWSTokenSignatureProvider.SignerCheckedProvider {

        @Inject
        JWSTokenMACSignerProvider(@Named("security.signature.key.provider") SymmetricKeyProvider symKProv) {
            super(symKProv);
            this.log = LogManager.getLogger();
        }

        public JWSTokenMACSignerProvider(SecretKey key) {
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

    public static class JWSTokenMACVerifierProvider extends JWSTokenMACSignerVerifierProvider implements JWSTokenSignatureProvider.VerifierCheckedProvider {

        @Inject
        JWSTokenMACVerifierProvider(@Named("security.signature.key.provider") SymmetricKeyProvider symKProv) {
            super(symKProv);
            this.log = LogManager.getLogger();
        }

        public JWSTokenMACVerifierProvider(SecretKey key) {
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
