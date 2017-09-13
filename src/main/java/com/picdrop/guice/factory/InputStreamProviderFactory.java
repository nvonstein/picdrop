/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.factory;

import com.picdrop.guice.names.File;
import com.picdrop.guice.names.Resource;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.model.resource.FileResource;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author i330120
 */
public interface InputStreamProviderFactory {

    @Resource
    InputStreamProvider create(FileResource res);

    @File
    InputStreamProvider create(FileItem fi);
}
