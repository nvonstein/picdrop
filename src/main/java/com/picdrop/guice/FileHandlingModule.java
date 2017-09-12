/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.picdrop.guice.factory.InputStreamProviderFactory;
import com.picdrop.guice.provider.implementation.FileItemFactoryProvider;
import com.picdrop.guice.provider.implementation.FileItemInputStreamProvider;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.guice.provider.implementation.ProcessorListProviders;
import com.picdrop.guice.provider.implementation.ResourceInputStreamProvider;
import com.picdrop.guice.provider.implementation.UploadHandlerProvider;
import com.picdrop.io.ImageProcessor;
import com.picdrop.io.Processor;
import com.picdrop.io.MurmurFileRepository;
import com.picdrop.io.writer.FileReader;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.io.writer.MurmurFileReaderWriter;
import com.picdrop.model.resource.FileResource;
import java.util.List;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.picdrop.io.FileRepository;

/**
 *
 * @author i330120
 */
public class FileHandlingModule implements Module {

    @Override
    public void configure(Binder binder) {
        // Upload handeling
        bindUploadHandler(binder);

        // File writing
        bindFileStreamProvider(binder);
        bindFileIOProcessors(binder);

        // File processors
        bindProcessorList(binder);

        bindProcessors(binder);
    }

    protected void bindUploadHandler(Binder binder) {
        binder.bind(FileItemFactory.class).toProvider(FileItemFactoryProvider.class).asEagerSingleton();
        binder.bind(ServletFileUpload.class).toProvider(UploadHandlerProvider.class);
    }

    protected void bindFileIOProcessors(Binder binder) {
        binder.bind(FileWriter.class).to(MurmurFileReaderWriter.class);
        binder.bind(FileReader.class).to(MurmurFileReaderWriter.class);

        binder.bind(new TypeLiteral<FileRepository<String>>() {
        }).annotatedWith(Names.named("repository.file.main")).to(MurmurFileRepository.class).in(Singleton.class);
        binder.bind(new TypeLiteral<FileRepository<String>>() {
        }).annotatedWith(Names.named("repository.file.thumbnails")).to(MurmurFileRepository.class).in(Singleton.class);
    }

    protected void bindFileStreamProvider(Binder binder) {
        binder.install(new FactoryModuleBuilder()
                .implement(InputStreamProvider.class, Names.named("inputstream.resource"), ResourceInputStreamProvider.class)
                .implement(InputStreamProvider.class, Names.named("inputstream.fileitem"), FileItemInputStreamProvider.class)
                .build(InputStreamProviderFactory.class)
        );
    }

    protected void bindProcessorList(Binder binder) {
        binder.bind(new TypeLiteral<List<Processor<FileResource>>>() {
        }).annotatedWith(Names.named("processors")).toProvider(ProcessorListProviders.class);
    }

    protected void bindProcessors(Binder binder) {
        binder.bind(ImageProcessor.class);
    }
}
