/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.throwingproviders.CheckedProvider;
import com.picdrop.guice.provider.UploadHandlerProvider;
import java.io.IOException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author i330120
 */
public class UploadHandlerProviderImpl implements UploadHandlerProvider {

    private final FileItemFactoryProviderImpl factoryProv;

    protected long maxfilesize;
    protected long maxrequestsize;

    @Inject
    public UploadHandlerProviderImpl(
            @Named("service.upload.maxfilesize") long maxfilesize,
            @Named("service.upload.maxrequestsize") long maxrequestsize,
            FileItemFactoryProviderImpl fac) {
        this.maxfilesize = maxfilesize;
        this.maxrequestsize = maxrequestsize;
        this.factoryProv = fac;
    }

    @Override
    public ServletFileUpload get() throws IOException {
        ServletFileUpload handler = new ServletFileUpload(this.factoryProv.get());

        handler.setFileSizeMax(maxfilesize);
        handler.setSizeMax(maxrequestsize);

        return handler;
    }

}
