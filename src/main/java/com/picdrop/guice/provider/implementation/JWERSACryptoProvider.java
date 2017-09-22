/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.Inject;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEProvider;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.picdrop.guice.names.Encryption;
import com.picdrop.guice.provider.JWECryptoProvider;
import com.picdrop.guice.provider.PKIXProvider;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;

/**
 *
 * @author nvonstein
 */
public abstract class JWERSACryptoProvider {

    private final PKIXProvider pki;

    private boolean isInit = false;

    protected JWEEncrypter encryptor;
    protected JWEDecrypter decrypter;

    public JWERSACryptoProvider(PKIXProvider pki) {
        this.pki = pki;
    }
    
    protected void init() throws IOException {
        init(this.pki);
    }

    private void init(PKIXProvider pki) throws IOException {
        if (!isInit) {
            if (pki == null) {
                throw new IOException("PKIProvider is null");
            }
            KeyPair kp = pki.get();
            PublicKey pck = kp.getPublic();
            if (!(pck instanceof RSAPublicKey)) {
                throw new IOException("Unsuitable public key provided");
            }

            this.encryptor = new RSAEncrypter((RSAPublicKey) pck);
            this.decrypter = new RSADecrypter(kp.getPrivate());
            this.isInit = true;
        }
    }

    public abstract JWEProvider get() throws IOException;

    public static class EncrypterProvider extends JWERSACryptoProvider implements JWECryptoProvider.EncrypterCheckedProvider {

        @Inject
        public EncrypterProvider(@Encryption PKIXProvider pki) {
            super(pki);
        }

        @Override
        public JWEEncrypter get() throws IOException {
            init();
            return super.encryptor;
        }

    }

    public static class DecrypterProvider extends JWERSACryptoProvider implements JWECryptoProvider.DecrypterCheckedProvider {

        @Inject
        public DecrypterProvider(@Encryption PKIXProvider pki) {
            super(pki);
        }

        @Override
        public JWEDecrypter get() throws IOException {
            init();
            return super.decrypter;
        }

    }
}
