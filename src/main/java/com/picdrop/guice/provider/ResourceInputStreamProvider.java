/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.picdrop.io.writer.FileReader;
import com.picdrop.model.resource.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author i330120
 */
public class ResourceInputStreamProvider implements InputStreamProvider {

    Resource res;
    FileReader reader;

    @AssistedInject
    public ResourceInputStreamProvider(
            FileReader reader,
            @Assisted Resource res) {
        this.reader = reader;
        this.res = res;
    }

    @Override
    public InputStream get() throws IOException {
        return reader.read(res.getFileUri());
    }

}
