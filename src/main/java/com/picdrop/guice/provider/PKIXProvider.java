/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.throwingproviders.CheckedProvider;
import java.io.IOException;
import java.security.KeyPair;

/**
 *
 * @author nvonstein
 */
public interface PKIXProvider extends CheckedProvider<KeyPair> {

    @Override
    public KeyPair get() throws IOException;

}
