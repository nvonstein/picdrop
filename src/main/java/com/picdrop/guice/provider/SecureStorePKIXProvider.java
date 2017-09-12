/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Inject;
import com.picdrop.security.SecureStore;
import java.io.IOException;
import java.security.KeyPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public class SecureStorePKIXProvider implements PKIXProvider {

    Logger log = LogManager.getLogger();

    protected SecureStoreProvider ssProv;
    private KeyPair key;

    @Inject
    public SecureStorePKIXProvider(SecureStoreProvider ssProv) {
        this.ssProv = ssProv;
    }

    @Override
    public KeyPair get() throws IOException {
        if (key == null) {
            SecureStore ss = this.ssProv.get();
            try {
                this.key = new KeyPair(ss.getPublicKey(), ss.getPrivateKey());
            } catch (Exception ex) {
                log.fatal("Unable to read key from store", ex);
                throw new IOException(ex.getMessage(), ex);
            }
        }
        return key;
    }

}
