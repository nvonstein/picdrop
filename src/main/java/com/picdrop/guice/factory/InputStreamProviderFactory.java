/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.factory;

import com.google.inject.name.Named;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.model.resource.Resource;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author i330120
 */
public interface InputStreamProviderFactory {

    @Named("inputstream.resource")
    InputStreamProvider create(Resource res);

    @Named("inputstream.fileitem")
    InputStreamProvider create(FileItem fi);
}
