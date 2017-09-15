/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.picdrop.guice.names.File;
import com.picdrop.guice.provider.ResourceContainer;
import com.picdrop.io.repository.FileRepository;
import com.picdrop.io.writer.FileReader;
import com.picdrop.model.resource.FileResource;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author i330120
 */
public class FileResourceContainer implements ResourceContainer {

    FileResource res;
    FileRepository<String> fileRepo;

    @AssistedInject
    public FileResourceContainer(
            @File FileRepository<String> fileRepo,
            @Assisted FileResource res) {
        this.fileRepo = fileRepo;
        this.res = res;
    }

    @Override
    public InputStream get() throws IOException {
        return fileRepo.read(res.getFileId());
    }

    @Override
    public String getName() {
        return this.res.getName();
    }

}
