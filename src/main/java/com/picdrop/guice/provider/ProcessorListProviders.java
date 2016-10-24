/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.picdrop.io.FileProcessor;
import com.picdrop.io.ImageProcessor;
import com.picdrop.model.resource.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author i330120
 */
public abstract class ProcessorListProviders {

    public static class PreStoreProcessorsProvider implements Provider<List<FileProcessor<Resource>>> {

        List<FileProcessor<Resource>> preStore = new ArrayList<>();

        @Inject
        public PreStoreProcessorsProvider() {
            // Adding handler
            preStore = Collections.unmodifiableList(preStore);
        }

        @Override
        public List<FileProcessor<Resource>> get() {
            return preStore;
        }

    }

    public static class PostStoreProcessorsProvider implements Provider<List<FileProcessor<Resource>>> {

        List<FileProcessor<Resource>> postStore = new ArrayList<>();

        @Inject
        public PostStoreProcessorsProvider(
                ImageProcessor imgp) {
            // Adding handler
            postStore.add(imgp);
            
            postStore = Collections.unmodifiableList(postStore);
        }

        @Override
        public List<FileProcessor<Resource>> get() {
            return postStore;
        }

    }
}
