/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.picdrop.guice.names.Config;
import com.picdrop.guice.names.Queries;
import com.picdrop.model.Share;
import com.picdrop.model.ShareReference;
import com.picdrop.model.TokenSet;
import com.picdrop.model.TokenSetReference;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.CollectionReference;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.resource.FileResourceReference;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.RegisteredUserReference;
import com.picdrop.model.user.User;
import com.picdrop.repository.Repository;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.Repository;
import com.picdrop.repository.mongo.NamedQueries;
import java.util.Map;
import java.util.Properties;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractRepositoryModule implements Module {

    @Override
    public void configure(Binder binder) {
//        binder.bind(new TypeLiteral<Map<String, String>>() {
//        }).annotatedWith(Queries.class).toInstance(NamedQueries.getQueries());
        // Registered user repo
        bindRegisteredUserRepo(binder);
        // Resource repo
        bindResourceRepo(binder);
        // Collections repo
        bindCollectionsRepo(binder);
        // Collectionitem repo
        bindCollectionItemRepo(binder);
        // Share repo
        bindShareRepo(binder);
        // TokenSet repo
        bindTokenSetRepo(binder);
        // Static bindings
        bindStaticRepoReferences(binder);
    }
    
    @Provides
    @Queries
    @Singleton
    protected Map<String,String> provideNamedQueries() {
        return NamedQueries.getQueries();
    }

    protected void bindRegisteredUserRepo(Binder binder) {
        Repository<String, RegisteredUser> repo = provideRegisteredUserRepo();

        binder.bind(new TypeLiteral<Repository<String, RegisteredUser>>() {
        }).toInstance(repo);
    }

    protected void bindResourceRepo(Binder binder) {
        AwareRepository<String, FileResource, User> repo = provideResourceRepo();

        binder.bind(new TypeLiteral<Repository<String, FileResource>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareRepository<String, FileResource, User>>() {
        }).toInstance(repo);
    }

    protected void bindCollectionsRepo(Binder binder) {
        AwareRepository<String, Collection, User> repo = provideCollectionRepo();

        binder.bind(new TypeLiteral<Repository<String, Collection>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareRepository<String, Collection, User>>() {
        }).toInstance(repo);
    }

    protected void bindCollectionItemRepo(Binder binder) {
        Repository<String, Collection.CollectionItem> repo = provideCollectionItemRepo();

        binder.bind(new TypeLiteral<Repository<String, Collection.CollectionItem>>() {
        }).toInstance(repo);
    }

    protected void bindShareRepo(Binder binder) {
        AwareRepository<String, Share, User> repo = provideShareRepo();

        binder.bind(new TypeLiteral<Repository<String, Share>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareRepository<String, Share, User>>() {
        }).toInstance(repo);
    }

    protected void bindTokenSetRepo(Binder binder) {
        Repository<String, TokenSet> repo = provideTokenSetRepo();

        binder.bind(new TypeLiteral<Repository<String, TokenSet>>() {
        }).toInstance(repo);
    }

    protected void bindStaticRepoReferences(Binder binder) {
        binder.requestStaticInjection(CollectionReference.class);
        binder.requestStaticInjection(FileResourceReference.class);
        binder.requestStaticInjection(ShareReference.class);
        binder.requestStaticInjection(RegisteredUserReference.class);
        binder.requestStaticInjection(Collection.CollectionItemReference.class);
        binder.requestStaticInjection(TokenSetReference.class);
    }

    protected abstract MongoDatabase provideDatabase(MongoClient client);

    protected abstract Datastore provideDatastore(MongoClient client);

    protected abstract MongoClient provideMongoClient(@Config Properties config);

    protected abstract Repository<String, TokenSet> provideTokenSetRepo();

    protected abstract Repository<String, Collection.CollectionItem> provideCollectionItemRepo();

    protected abstract Repository<String, RegisteredUser> provideRegisteredUserRepo();

    protected abstract AwareRepository<String, Collection, User> provideCollectionRepo();

    protected abstract AwareRepository<String, FileResource, User> provideResourceRepo();

    protected abstract AwareRepository<String, Share, User> provideShareRepo();

}
