/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.picdrop.guice.names.Config;
import com.picdrop.helper.EnvHelper;
import com.picdrop.model.Share;
import com.picdrop.model.TokenSet;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.mongo.MorphiaAdvancedRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaAdvancedRepository;
import java.util.Properties;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author i330120
 */
public final class RepositoryModule extends AbstractRepositoryModule {


    @Provides
    @Singleton
    @Override
    protected MongoDatabase provideDatabase(MongoClient client) {
        return client.getDatabase("picdrop");
    }
    
    @Provides
    @Singleton
    @Override
    protected Datastore provideDatastore(MongoClient client) {
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.picdrop.model");
        Datastore ds = morphia.createDatastore(client, "picdrop");
        ds.ensureIndexes(true);

        return ds;
    }

    @Provides
    @Singleton
    @Override
    protected MongoClient provideMongoClient(@Config Properties config) {
        String host = config.getProperty("service.db.host");
        if (Strings.isNullOrEmpty(host)) {
            host = EnvHelper.getSystemProperty("service.db.host");
        }
        if (Strings.isNullOrEmpty(host)) {
            host = EnvHelper.getSystemEnv("PICDROP_DB_HOST");
        }
        if (Strings.isNullOrEmpty(host)) {
            host = "127.0.0.1:27017";
        }

        return new MongoClient(host);
    }

    @Provides
    @Singleton
    @Override
    protected MorphiaAdvancedRepository<TokenSet> provideTokenSetRepo(Datastore ds) {
        return new MorphiaAdvancedRepository<>(ds, TokenSet.class);
    }

    @Provides
    @Singleton
    @Override
    protected MorphiaAdvancedRepository<Collection.CollectionItem> provideCollectionItemRepo(Datastore ds) {
        return new MorphiaAdvancedRepository<>(ds, Collection.CollectionItem.class);
    }

    @Provides
    @Singleton
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<Collection> provideCollectionRepo(Datastore ds) {
        return new PrincipalAwareMorphiaAdvancedRepository<>(ds, Collection.class);
    }

    @Provides
    @Singleton
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<FileResource> provideResourceRepo(Datastore ds) {
        return new PrincipalAwareMorphiaAdvancedRepository<>(ds, FileResource.class);
    }

    @Provides
    @Singleton
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<Share> provideShareRepo(Datastore ds) {
        return new PrincipalAwareMorphiaAdvancedRepository<>(ds, Share.class);
    }

    @Provides
    @Singleton
    @Override
    protected MorphiaAdvancedRepository<RegisteredUser> provideRegisteredUserRepo(Datastore ds) {
        return new MorphiaAdvancedRepository<>(ds, RegisteredUser.class);
    }
}
