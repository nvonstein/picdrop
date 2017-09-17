/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.picdrop.guice.names.Config;
import com.picdrop.guice.names.Queries;
import com.picdrop.helper.EnvHelper;
import com.picdrop.model.Share;
import com.picdrop.model.TokenSet;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.mongo.MorphiaAdvancedRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaAdvancedRepository;
import com.picdrop.repository.mongo.RepositoryPrototype;
import java.util.Map;
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
    protected MorphiaAdvancedRepository<TokenSet> provideTokenSetRepo(RepositoryPrototype prototype) {
        return MorphiaAdvancedRepository.forType(TokenSet.class).from(prototype).build();
    }

    @Provides
    @Singleton
    @Override
    protected MorphiaAdvancedRepository<Collection.CollectionItem> provideCollectionItemRepo(RepositoryPrototype prototype) {
        return MorphiaAdvancedRepository.forType(Collection.CollectionItem.class).from(prototype).build();
    }

    @Provides
    @Singleton
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<Collection> provideCollectionRepo(RepositoryPrototype prototype) {
        return PrincipalAwareMorphiaAdvancedRepository.forType(Collection.class).from(prototype).build();
    }

    @Provides
    @Singleton
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<FileResource> provideResourceRepo(RepositoryPrototype prototype) {
        return PrincipalAwareMorphiaAdvancedRepository.forType(FileResource.class).from(prototype).build();
    }

    @Provides
    @Singleton
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<Share> provideShareRepo(RepositoryPrototype prototype) {
        return PrincipalAwareMorphiaAdvancedRepository.forType(Share.class).from(prototype).build();
    }

    @Provides
    @Singleton
    @Override
    protected MorphiaAdvancedRepository<RegisteredUser> provideRegisteredUserRepo(RepositoryPrototype prototype) {
        return MorphiaAdvancedRepository.forType(RegisteredUser.class).from(prototype).build();
    }

    @Provides
    @Singleton
    @Override
    protected RepositoryPrototype provideRepositoryPrototype(Datastore ds, ObjectMapper mapper, @Queries Map<String, String> queries) {
        return new RepositoryPrototype(ds, mapper, queries);
    }
}
