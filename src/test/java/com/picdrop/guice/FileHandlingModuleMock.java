/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.throwingproviders.CheckedProvides;
import com.picdrop.guice.names.File;
import com.picdrop.guice.provider.FileRepositoryProvider;
import com.picdrop.io.repository.FileRepository;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author nvonstein
 */
public class FileHandlingModuleMock extends AbstractFileHandlingModule {

    protected FileRepository<String> fp;

    public FileHandlingModuleMock(FileRepository<String> fp) {
        this.fp = fp;
    }

    @Override
    @CheckedProvides(FileRepositoryProvider.class)
    @File
    FileRepository<String> provideFileRepository(Properties config) throws IOException {
        return fp;
    }

}
