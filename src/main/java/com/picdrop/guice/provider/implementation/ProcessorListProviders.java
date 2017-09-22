/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider.implementation;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.picdrop.io.ImageProcessor;
import com.picdrop.io.Processor;
import com.picdrop.model.resource.FileResource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author i330120
 */
public class ProcessorListProviders implements Provider<List<Processor<FileResource>>> {

    List<Processor<FileResource>> pro = new ArrayList<>();

    @Inject
    public ProcessorListProviders(
            ImageProcessor imgp) {
        // Adding handler
        pro.add(imgp);

        pro = Collections.unmodifiableList(pro);
    }

    @Override
    public List<Processor<FileResource>> get() {
        return pro;
    } 
}
