/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.picdrop.guice.provider.SymmetricKeyProvider;
import java.io.IOException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public class StaticSymmetricKeyProvider implements SymmetricKeyProvider {

    Logger log = LogManager.getLogger();

    private int keysize = 256;
    private String alg = "AES";

    private SecretKey key;

    public StaticSymmetricKeyProvider(SecretKey key) {
        this.key = key;
    }

    public StaticSymmetricKeyProvider(int keysize, String alg) {
        this.alg = alg;
        this.keysize = keysize;
    }

    public StaticSymmetricKeyProvider() {
    }

    @Override
    public SecretKey get() throws IOException {
        if (this.key == null) {
            try {
                KeyGenerator kgen = KeyGenerator.getInstance(this.alg);
                kgen.init(this.keysize);
                this.key = kgen.generateKey();
            } catch (Exception ex) {
                log.fatal(String.format("Unable to generate key with algorithm '%s' and keysize '%d'", this.alg, this.keysize), ex);
                throw new IOException(ex.getMessage(), ex);
            }
        }
        return key;
    }

}
