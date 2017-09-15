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
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.picdrop.guice.names.Config;
import com.picdrop.guice.names.File;
import com.picdrop.guice.names.Resource;
import com.picdrop.guice.provider.FileRepositoryProvider;
import com.picdrop.guice.provider.implementation.FileItemFactoryProvider;
import com.picdrop.guice.provider.implementation.FileItemResourceContainer;
import com.picdrop.guice.provider.implementation.ProcessorListProviders;
import com.picdrop.guice.provider.implementation.FileResourceContainer;
import com.picdrop.guice.provider.implementation.UploadHandlerProvider;
import com.picdrop.io.ImageProcessor;
import com.picdrop.io.Processor;
import com.picdrop.io.repository.MurmurFileRepository;
import com.picdrop.io.writer.FileReader;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.io.writer.MurmurFileReaderWriter;
import com.picdrop.model.resource.FileResource;
import java.util.List;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.picdrop.io.repository.FileRepository;
import com.picdrop.io.repository.RoundRobinFileRepository;
import java.io.IOException;
import java.util.Properties;
import com.picdrop.guice.factory.ResourceContainerFactory;
import com.picdrop.guice.provider.ResourceContainer;
import com.picdrop.helper.ConfigHelper;
import com.picdrop.io.repository.ScopedRoundRobinFileRepository;
import java.util.Map.Entry;

/**
 *
 * @author i330120
 */
public class FileHandlingModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(ThrowingProviderBinder.forModule(this));
        // Upload handeling
        bindUploadHandler(binder);

        // File writing
        bindFileStreamProvider(binder);
        bindFileIOProcessors(binder);
//        bindFileRepository(binder);

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
    }

    protected void bindFileRepository(Binder binder) {
        binder.bind(new TypeLiteral<FileRepository<String>>() {
        }).annotatedWith(File.class).to(MurmurFileRepository.class).in(Singleton.class);
    }

    protected void bindFileStreamProvider(Binder binder) {
        binder.install(new FactoryModuleBuilder()
                .implement(ResourceContainer.class, Resource.class, FileResourceContainer.class)
                .implement(ResourceContainer.class, File.class, FileItemResourceContainer.class)
                .build(ResourceContainerFactory.class)
        );
    }

    protected void bindProcessorList(Binder binder) {
        binder.bind(new TypeLiteral<List<Processor<FileResource>>>() {
        }).annotatedWith(File.class).toProvider(ProcessorListProviders.class);
    }

    protected void bindProcessors(Binder binder) {
        binder.bind(ImageProcessor.class);
    }

    @CheckedProvides(FileRepositoryProvider.class)
    @File
    FileRepository<String> provideFileRepository(@Config Properties config) throws IOException {
        ScopedRoundRobinFileRepository rrFRepo = new ScopedRoundRobinFileRepository();
        boolean gen;
        try {
            List<Entry<Object, Object>> props = ConfigHelper.listChildProperties(config, "service.file.stores.active", true);
            props.forEach(e -> rrFRepo.registerActiveRepository(
                    ((String) e.getKey()).replace("service.file.stores.active.", ""),
                    new MurmurFileRepository((String) e.getValue())));

            props = ConfigHelper.listChildProperties(config, "service.file.stores", true);
            props.forEach(e -> rrFRepo.registerRepository(
                    ((String) e.getKey()).replace("service.file.stores.", ""),
                    new MurmurFileRepository((String) e.getValue())));

            gen = Boolean.valueOf(config.getProperty("service.file.store.generate", "true"));
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
        rrFRepo.init(gen);
        return rrFRepo;
    }
}
