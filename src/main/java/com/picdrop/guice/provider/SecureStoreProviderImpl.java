/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.security.SecureStore;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public class SecureStoreProviderImpl implements SecureStoreProvider {

    Logger log = LogManager.getLogger();

    private final String storePath;
    private SecureStore ss;

    @Inject
    public SecureStoreProviderImpl(@Named("service.security.store") String storePath) {
        this.storePath = storePath;
    }

    @Override
    public SecureStore get() throws IOException {
        if (this.ss == null) {
            try {
                ss = new SecureStore(storePath, true);
            } catch (FileNotFoundException | NoSuchAlgorithmException | CertificateException | KeyStoreException ex) {
                log.fatal("Unable to load Keystore", ex);
                throw new IOException(ex.getMessage(), ex);
            }
        }
        return this.ss;
    }
}
