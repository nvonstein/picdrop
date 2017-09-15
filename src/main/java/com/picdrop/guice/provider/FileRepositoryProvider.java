/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.throwingproviders.CheckedProvider;
import com.picdrop.io.FileRepository;
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public interface FileRepositoryProvider extends CheckedProvider<FileRepository<String>>{

    @Override
    public FileRepository<String> get() throws IOException;
    
}
