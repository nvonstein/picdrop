/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
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
import com.picdrop.repository.AdvancedRepository;
import com.picdrop.repository.AwareAdvancedRepository;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.mongo.NamedQueries;
import com.picdrop.repository.Repository;
import com.picdrop.repository.mongo.MorphiaAdvancedRepository;
import com.picdrop.repository.mongo.MorphiaRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaAdvancedRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaRepository;
import java.util.Map;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author i330120
 */
public class RepositoryModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(new TypeLiteral<Map<String, String>>() {
        }).annotatedWith(Names.named("queries")).toInstance(NamedQueries.getQueries());

        Datastore ds = bindDatastore(binder);

        // Registered user repo
        bindRegisteredUserRepo(binder, ds);
        // Resource repo
        bindResourceRepo(binder, ds);
        // Collections repo
        bindCollectionsRepo(binder, ds);
        // Collectionitem repo
        bindCollectionItemRepo(binder, ds);
        // Share repo
        bindShareRepo(binder, ds);
        // TokenSet repo
        bindTokenSetRepo(binder, ds);

        // Static bindings
        bindStaticRepoReferences(binder, ds);
    }

    protected Datastore bindDatastore(Binder binder) {
        MongoClient client = new MongoClient();
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.picdrop.model");
        Datastore ds = morphia.createDatastore(client, "test");
        ds.ensureIndexes(true);

        binder.bind(MongoDatabase.class).toInstance(client.getDatabase("test"));
        binder.bind(Datastore.class).toInstance(ds);
        return ds;
    }

    protected void bindRegisteredUserRepo(Binder binder, Datastore ds) {
        binder.bind(new TypeLiteral<Repository<String, RegisteredUser>>() {
        }).toInstance(new MorphiaRepository<>(ds, RegisteredUser.class));
    }

    protected void bindResourceRepo(Binder binder, Datastore ds) {
        PrincipalAwareMorphiaAdvancedRepository<FileResource> repo = new PrincipalAwareMorphiaAdvancedRepository<>(ds, FileResource.class);

        binder.bind(new TypeLiteral<Repository<String, FileResource>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareRepository<String, FileResource, User>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AdvancedRepository<String, FileResource>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareAdvancedRepository<String, FileResource, User>>() {
        }).toInstance(repo);
    }

    protected void bindCollectionsRepo(Binder binder, Datastore ds) {
        PrincipalAwareMorphiaAdvancedRepository<Collection> repo = new PrincipalAwareMorphiaAdvancedRepository<>(ds, Collection.class);

        binder.bind(new TypeLiteral<Repository<String, Collection>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareRepository<String, Collection, User>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AdvancedRepository<String, Collection>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareAdvancedRepository<String, Collection, User>>() {
        }).toInstance(repo);
    }

    protected void bindCollectionItemRepo(Binder binder, Datastore ds) {
        binder.bind(new TypeLiteral<Repository<String, Collection.CollectionItem>>() {
        }).toInstance(new MorphiaRepository<>(ds, Collection.CollectionItem.class));
    }

    protected void bindShareRepo(Binder binder, Datastore ds) {
        PrincipalAwareMorphiaAdvancedRepository<Share> repo = new PrincipalAwareMorphiaAdvancedRepository<>(ds, Share.class);

        binder.bind(new TypeLiteral<Repository<String, Share>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareRepository<String, Share, User>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AdvancedRepository<String, Share>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AwareAdvancedRepository<String, Share, User>>() {
        }).toInstance(repo);
    }

    protected void bindTokenSetRepo(Binder binder, Datastore ds) {
        AdvancedRepository<String, TokenSet> repo = new MorphiaAdvancedRepository<>(ds, TokenSet.class);
        binder.bind(new TypeLiteral<Repository<String, TokenSet>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AdvancedRepository<String, TokenSet>>() {
        }).toInstance(repo);
    }

    protected void bindStaticRepoReferences(Binder binder, Datastore ds) {
        binder.requestStaticInjection(CollectionReference.class);
        binder.requestStaticInjection(FileResourceReference.class);
        binder.requestStaticInjection(ShareReference.class);
        binder.requestStaticInjection(RegisteredUserReference.class);
        binder.requestStaticInjection(Collection.CollectionItemReference.class);
        binder.requestStaticInjection(TokenSetReference.class);
    }
}
