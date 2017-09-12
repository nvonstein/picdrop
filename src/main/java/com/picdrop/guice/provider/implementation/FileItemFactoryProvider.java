/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import java.io.File;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 *
 * @author i330120
 */
public class FileItemFactoryProvider implements Provider<FileItemFactory> {

    FileItemFactory fac;

    @Inject
    public FileItemFactoryProvider(
            @Named("service.upload.maxmemory") int maxmem, 
            @Named("service.upload.store") String path) {
        File f = new File(path);
        if (!f.exists() || !f.canWrite() || !f.isDirectory()) {
            throw new IllegalArgumentException(String.format("'%s' is not a valid directory", path));
        }
        fac = new DiskFileItemFactory(maxmem, f);
    }

    @Override
    public FileItemFactory get() {
        return this.fac;
    }

}
