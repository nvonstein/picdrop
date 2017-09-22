/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.throwingproviders.CheckedProvider;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author nvonstein
 */
public interface ResourceContainer extends CheckedProvider<InputStream>{
    String getName();

    @Override
    public InputStream get() throws IOException;
    
}
