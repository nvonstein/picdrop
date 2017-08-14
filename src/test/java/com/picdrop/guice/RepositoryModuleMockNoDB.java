/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.picdrop.model.resource.FileResource;
import com.picdrop.repository.Repository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaRepository;
import org.mockito.Mockito;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author nvonstein
 */
public class RepositoryModuleMockNoDB extends RepositoryModule {

    protected Repository<String, FileResource> resRepo;

    public RepositoryModuleMockNoDB(Repository<String, FileResource> resRepo) {
        this.resRepo = resRepo;
    }

    @Override
    protected Datastore bindDatastore(Binder binder) {
        binder.bind(Datastore.class).toInstance(Mockito.mock(Datastore.class));
        return null;
    }

    @Override
    protected void bindResourceRepo(Binder binder, Datastore ds) {
        binder.bind(new TypeLiteral<Repository<String, FileResource>>() {
        }).toInstance(this.resRepo);
    }

}
