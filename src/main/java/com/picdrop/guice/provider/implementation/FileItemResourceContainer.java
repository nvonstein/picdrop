/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.picdrop.guice.provider.ResourceContainer;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author i330120
 */
public class FileItemResourceContainer implements ResourceContainer {

    FileItem fi;

    @AssistedInject
    public FileItemResourceContainer(@Assisted FileItem fi) {
        this.fi = fi;
    }

    @Override
    public InputStream get() throws IOException {
        return this.fi.getInputStream();
    }

    @Override
    public String getName() {
        return this.fi.getName();
    }

    @Override
    public long getFileSize() {
        return fi.getSize();
    }

}
