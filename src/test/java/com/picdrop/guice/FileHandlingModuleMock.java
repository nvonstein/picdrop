/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.picdrop.guice.names.File;
import com.picdrop.io.MurmurFileRepository;
import com.picdrop.io.writer.FileReader;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.model.resource.FileResource;
import com.picdrop.io.FileRepository;


/**
 *
 * @author nvonstein
 */
public class FileHandlingModuleMock extends FileHandlingModule {
    
    protected FileWriter writer;
    protected FileReader reader;
    protected FileRepository<String> fp;

    public FileHandlingModuleMock(FileWriter writer, FileReader reader,FileRepository<String> fp) {
        this.writer = writer;
        this.reader = reader;
        this.fp = fp;
    }

    @Override
    protected void bindFileIOProcessors(Binder binder) {
        binder.bind(FileWriter.class).toInstance(writer);
        binder.bind(FileReader.class).toInstance(reader);
        
        binder.bind(new TypeLiteral<FileRepository<String>>() {
        }).annotatedWith(File.class).toInstance(fp);
    }
    
    
}
