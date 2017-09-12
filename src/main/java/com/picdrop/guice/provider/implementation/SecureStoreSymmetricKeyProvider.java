/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.picdrop.guice.provider.SecureStoreProvider;
import com.picdrop.guice.provider.SymmetricKeyProvider;
import com.picdrop.security.SecureStore;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import javax.crypto.SecretKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public class SecureStoreSymmetricKeyProvider implements SymmetricKeyProvider {

    Logger log = LogManager.getLogger();

    private SecureStoreProvider ssProv;
    private final String keyName;

    private SecretKey key;

    public SecureStoreSymmetricKeyProvider(SecureStoreProvider ssProv, String keyName) {
        this.ssProv = ssProv;
        this.keyName = keyName;
    }

    @Override
    public SecretKey get() throws IOException {
        if (key == null) {
            SecureStore ss = this.ssProv.get();
            try {
                if (!ss.hasValue(keyName)) {
                    log.fatal("Keystore does not provide any value for alias '{}'", this.keyName);
                    throw new IOException(String.format("Keystore does not provide any value for alias '%s'", this.keyName));
                }
                key = ss.readValueRaw(keyName);
            } catch (Exception ex) {
                log.fatal("Unable to read key from store", ex);
                throw new IOException(ex.getMessage(), ex);
            }
        }
        return key;
    }

}
