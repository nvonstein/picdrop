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
import com.picdrop.guice.factory.ResourceContainerFactory;
import com.picdrop.guice.names.Config;
import com.picdrop.guice.names.File;
import com.picdrop.guice.names.Resource;
import com.picdrop.guice.provider.FileItemFactoryProvider;
import com.picdrop.guice.provider.FileRepositoryProvider;
import com.picdrop.guice.provider.ResourceContainer;
import com.picdrop.guice.provider.UploadHandlerProvider;
import com.picdrop.guice.provider.implementation.FileItemFactoryProviderImpl;
import com.picdrop.guice.provider.implementation.FileItemResourceContainer;
import com.picdrop.guice.provider.implementation.FileResourceContainer;
import com.picdrop.guice.provider.implementation.ProcessorListProviders;
import com.picdrop.guice.provider.implementation.UploadHandlerProviderImpl;
import com.picdrop.io.ImageProcessor;
import com.picdrop.io.Processor;
import com.picdrop.io.repository.FileRepository;
import com.picdrop.io.repository.MurmurFileRepository;
import com.picdrop.io.writer.FileReader;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.io.writer.MurmurFileReaderWriter;
import com.picdrop.model.resource.FileResource;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractFileHandlingModule implements Module {

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
        ThrowingProviderBinder.create(binder)
                .bind(FileItemFactoryProvider.class, FileItemFactory.class)
                .to(FileItemFactoryProviderImpl.class)
                .in(Singleton.class);
        
        ThrowingProviderBinder.create(binder)
                .bind(UploadHandlerProvider.class, ServletFileUpload.class)
                .to(UploadHandlerProviderImpl.class)
                .in(Singleton.class);
    }

    protected void bindFileIOProcessors(Binder binder) {
        binder.bind(FileWriter.class).to(MurmurFileReaderWriter.class);
        binder.bind(FileReader.class).to(MurmurFileReaderWriter.class);
    }

    protected void bindFileStreamProvider(Binder binder) {
        binder.install(new FactoryModuleBuilder()
                .implement(ResourceContainer.class, Resource.class, FileResourceContainer.class)
                .implement(ResourceContainer.class, File.class, FileItemResourceContainer.class)
                .build(ResourceContainerFactory.class));
    }

    protected void bindProcessorList(Binder binder) {
        binder.bind(new TypeLiteral<List<Processor<FileResource>>>() {
        }).annotatedWith(File.class).toProvider(ProcessorListProviders.class);
    }

    protected void bindProcessors(Binder binder) {
        binder.bind(ImageProcessor.class);
    }

    abstract FileRepository<String> provideFileRepository(Properties config) throws IOException;

}
