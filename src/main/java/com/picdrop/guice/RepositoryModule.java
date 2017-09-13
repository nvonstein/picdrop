/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
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
import com.picdrop.repository.AdvancedRepository;
import com.picdrop.repository.AwareAdvancedRepository;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.mongo.NamedQueries;
import com.picdrop.repository.Repository;
import com.picdrop.repository.mongo.MorphiaAdvancedRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaAdvancedRepository;
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
        }).annotatedWith(Queries.class).toInstance(NamedQueries.getQueries());

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
        AdvancedRepository<String, RegisteredUser> repo = createRegisteredUserRepo(ds);

        binder.bind(new TypeLiteral<AdvancedRepository<String, RegisteredUser>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<Repository<String, RegisteredUser>>() {
        }).toInstance(repo);
    }

    protected void bindResourceRepo(Binder binder, Datastore ds) {
        AwareAdvancedRepository<String, FileResource, User> repo = createResourceRepo(ds);

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
        AwareAdvancedRepository<String, Collection, User> repo = createCollectionRepo(ds);

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
        AdvancedRepository<String, Collection.CollectionItem> repo = createCollectionItemRepo(ds);

        binder.bind(new TypeLiteral<Repository<String, Collection.CollectionItem>>() {
        }).toInstance(repo);
        binder.bind(new TypeLiteral<AdvancedRepository<String, Collection.CollectionItem>>() {
        }).toInstance(repo);
    }

    protected void bindShareRepo(Binder binder, Datastore ds) {
        AwareAdvancedRepository<String, Share, User> repo = createShareRepo(ds);

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
        AdvancedRepository<String, TokenSet> repo = createTokenSetRepo(ds);
        
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

    protected AdvancedRepository<String, TokenSet> createTokenSetRepo(Datastore ds) {
        return new MorphiaAdvancedRepository<>(ds, TokenSet.class);
    }

    protected AdvancedRepository<String, Collection.CollectionItem> createCollectionItemRepo(Datastore ds) {
        return new MorphiaAdvancedRepository<>(ds, Collection.CollectionItem.class);
    }

    protected AwareAdvancedRepository<String, Collection, User> createCollectionRepo(Datastore ds) {
        return new PrincipalAwareMorphiaAdvancedRepository<>(ds, Collection.class);
    }

    protected AwareAdvancedRepository<String, FileResource, User> createResourceRepo(Datastore ds) {
        return new PrincipalAwareMorphiaAdvancedRepository<>(ds, FileResource.class);
    }

    protected AwareAdvancedRepository<String, Share, User> createShareRepo(Datastore ds) {
        return new PrincipalAwareMorphiaAdvancedRepository<>(ds, Share.class);
    }

    protected AdvancedRepository<String, RegisteredUser> createRegisteredUserRepo(Datastore ds) {
        return new MorphiaAdvancedRepository<>(ds, RegisteredUser.class);
    }
}
