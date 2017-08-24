/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.picdrop.model.Share;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
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
    protected Repository<String, Collection> cRepo;
    protected AwareRepository<String, Share, User> shareRepo;

    private RepositoryModuleMockNoDB() {
    }

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
        if (this.resRepo != null) {
            binder.bind(new TypeLiteral<Repository<String, FileResource>>() {
            }).toInstance(this.resRepo);
        } else {
            super.bindResourceRepo(binder, ds);
        }
    }

    @Override
    protected void bindShareRepo(Binder binder, Datastore ds) {
        if (this.shareRepo != null) {
            binder.bind(new TypeLiteral<AwareRepository<String, Share, User>>() {
            }).toInstance(this.shareRepo);
            binder.bind(new TypeLiteral<Repository<String, Share>>() {
            }).toInstance(this.shareRepo);
        } else {
            super.bindShareRepo(binder, ds);
        }
    }

    @Override
    protected void bindCollectionsRepo(Binder binder, Datastore ds) {
        if (this.cRepo != null) {
            binder.bind(new TypeLiteral<Repository<String, Collection>>() {
            }).toInstance(this.cRepo);
        } else {
            super.bindCollectionsRepo(binder, ds);
        }
    }

    public static RepositoryModuleBuilder builder() {
        return new RepositoryModuleBuilder();
    }

    public static class RepositoryModuleBuilder {

        protected Repository<String, FileResource> resRepo;
        protected Repository<String, Collection> cRepo;
        protected AwareRepository<String, Share, User> shareRepo;

        private RepositoryModuleBuilder() {

        }

        public RepositoryModuleMockNoDB build() {
            RepositoryModuleMockNoDB module = new RepositoryModuleMockNoDB();

            module.resRepo = this.resRepo;
            module.shareRepo = this.shareRepo;
            module.cRepo = this.cRepo;

            return module;
        }

        public RepositoryModuleBuilder resRepo(Repository<String, FileResource> repo) {
            this.resRepo = repo;
            return this;
        }

        public RepositoryModuleBuilder cRepo(Repository<String, Collection> repo) {
            this.cRepo = repo;
            return this;
        }

        public RepositoryModuleBuilder shareRepo(AwareRepository<String, Share, User> repo) {
            this.shareRepo = repo;
            return this;
        }
    }

}
