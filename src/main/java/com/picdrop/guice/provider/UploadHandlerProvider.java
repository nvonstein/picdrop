/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author i330120
 */
public class UploadHandlerProvider implements Provider<ServletFileUpload> {

    @Inject
    FileItemFactory factory;

    int maxfilesize;
    int maxrequestsize;

    @Inject
    public UploadHandlerProvider(
            @Named("service.upload.maxfilesize") int maxfilesize,
            @Named("service.upload.maxrequestsize") int maxrequestsize) {
        this.maxfilesize = maxfilesize;
        this.maxrequestsize = maxrequestsize;
    }

    public FileItemFactory getFactory() {
        return factory;
    }

    public void setFactory(FileItemFactory factory) {
        this.factory = factory;
    }

    @Override
    public ServletFileUpload get() {
        ServletFileUpload handler = new ServletFileUpload(factory);
        
        handler.setFileSizeMax(maxfilesize);
        handler.setSizeMax(maxrequestsize);
        
        return handler;
    }

}
