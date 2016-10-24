/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.picdrop.guice.factory.InputStreamProviderFactory;
import com.picdrop.guice.provider.FileItemFactoryProvider;
import com.picdrop.guice.provider.FileItemInputStreamProvider;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.guice.provider.ProcessorListProviders;
import com.picdrop.guice.provider.ResourceInputStreamProvider;
import com.picdrop.guice.provider.UploadHandlerProvider;
import com.picdrop.io.FileProcessor;
import com.picdrop.io.ImageProcessor;
import com.picdrop.io.Processor;
import com.picdrop.io.ResourceWriteProcessor;
import com.picdrop.io.writer.FileReader;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.io.writer.MurmurFileReaderWriter;
import com.picdrop.model.resource.Resource;
import java.util.List;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author i330120
 */
public class FileHandlingModule implements Module {

    @Override
    public void configure(Binder binder) {
        // Upload handeling
        binder.bind(FileItemFactory.class).toProvider(FileItemFactoryProvider.class).asEagerSingleton();
        binder.bind(ServletFileUpload.class).toProvider(UploadHandlerProvider.class);

        // File writing
        binder.bind(FileWriter.class).to(MurmurFileReaderWriter.class);
        binder.bind(FileReader.class).to(MurmurFileReaderWriter.class);
        binder.bind(new TypeLiteral<FileProcessor<Resource>>() {
        }).annotatedWith(Names.named("processor.write")).to(ResourceWriteProcessor.class);

        binder.install(new FactoryModuleBuilder()
                .implement(InputStreamProvider.class, Names.named("inputstream.resource"), ResourceInputStreamProvider.class)
                .implement(InputStreamProvider.class, Names.named("inputstream.fileitem"), FileItemInputStreamProvider.class)
                .build(InputStreamProviderFactory.class)
        );

        // File processors
        binder.bind(new TypeLiteral<List<Processor<Resource>>>() {
        }).annotatedWith(Names.named("processors")).toProvider(ProcessorListProviders.class);

        binder.bind(ImageProcessor.class);
    }

}
