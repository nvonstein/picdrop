/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.guice.provider.FileItemFactoryProvider;
import java.io.File;
import java.io.IOException;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 *
 * @author i330120
 */
public class FileItemFactoryProviderImpl implements FileItemFactoryProvider {

    protected final File root;
    protected final int maxmem;

    private FileItemFactory fac;

    @Inject
    public FileItemFactoryProviderImpl(
            @Named("service.upload.maxmemory") int maxmem,
            @Named("service.upload.store") String path) {
        this.root = new File(path);
        this.maxmem = maxmem;
    }

    protected void init() throws IOException {
        if (this.fac == null) {
            if (!root.exists() || !root.canWrite() || !root.isDirectory()) {
                throw new IOException(String.format("'%s' is not a valid directory", this.root.getAbsolutePath()));
            }
            DiskFileItemFactory dfac = new DiskFileItemFactory(maxmem, root);
            fac = dfac;
        }
    }

    @Override
    public FileItemFactory get() throws IOException {
        init();
        return this.fac;
    }

}
