/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.io.writer.FileReader;
import com.picdrop.model.resource.FileResource;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author i330120
 */
public class ResourceInputStreamProvider implements InputStreamProvider {

    FileResource res;
    FileReader reader;

    @AssistedInject
    public ResourceInputStreamProvider(
            FileReader reader,
            @Assisted FileResource res) {
        this.reader = reader;
        this.res = res;
    }

    @Override
    public InputStream get() throws IOException {
        return reader.read(res.getFileUri());
    }

}
