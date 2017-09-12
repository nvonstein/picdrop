/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import java.io.IOException;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public abstract class JWETokenDirectEncrypterDecrypterProvider {

    Logger log = LogManager.getLogger();

    private final SymmetricKeyProvider symKProv;
    private final SecretKey KEY;

    private boolean isInit = false;

    protected JWEEncrypter encryptor;
    protected JWEDecrypter decrypter;

    JWETokenDirectEncrypterDecrypterProvider(SymmetricKeyProvider symKProv) {
        this.symKProv = symKProv;
        this.KEY = null;
    }

    public JWETokenDirectEncrypterDecrypterProvider(SecretKey key) {
        this.symKProv = null;
        this.KEY = key;
    }

    protected void init() throws IOException, KeyLengthException {
        if (!isInit) {
            SecretKey lkey = (this.KEY == null)
                    ? symKProv.get()
                    : this.KEY;

            this.encryptor = new DirectEncrypter(lkey);
            this.decrypter = new DirectDecrypter(lkey);
            isInit = true;
        }
    }

    public static class JWETokenDirectEncrypterProvider extends JWETokenDirectEncrypterDecrypterProvider implements JWETokenCryptoProvider.EncrypterCheckedProvider {

        @Inject
        JWETokenDirectEncrypterProvider(@Named("security.crypto.sym.key.provider") SymmetricKeyProvider symKProv) {
            super(symKProv);
            log = LogManager.getLogger();
        }

        public JWETokenDirectEncrypterProvider(SecretKey key) {
            super(key);
            log = LogManager.getLogger();
        }

        @Override
        public JWEEncrypter get() throws IOException {
            try {
                init();
            } catch (KeyLengthException ex) {
                log.fatal("Invalid key size", ex);
                throw new IOException(ex.getMessage(), ex);
            }
            return this.encryptor;
        }

    }

    public static class JWETokenDirectDecrypterProvider extends JWETokenDirectEncrypterDecrypterProvider implements JWETokenCryptoProvider.DecrypterCheckedProvider {

        @Inject
        JWETokenDirectDecrypterProvider(@Named("security.crypto.sym.key.provider") SymmetricKeyProvider symKProv) {
            super(symKProv);
            log = LogManager.getLogger();
        }

        public JWETokenDirectDecrypterProvider(SecretKey key) {
            super(key);
            log = LogManager.getLogger();
        }

        @Override
        public JWEDecrypter get() throws IOException {
            try {
                init();
            } catch (KeyLengthException ex) {
                log.fatal("Invalid key size", ex);
                throw new IOException(ex.getMessage(), ex);
            }
            return this.decrypter;
        }

    }
}
