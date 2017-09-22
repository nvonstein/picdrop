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
import com.picdrop.repository.mongo.MorphiaRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaRepository;
import java.util.Properties;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 *
 * @author i330120
 */
public class RepositoryModule extends AbstractRepositoryModule {

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

    @Override
    protected MorphiaRepository<TokenSet> provideTokenSetRepo() {
        return MorphiaRepository.forType(TokenSet.class).uninitialized();
    }

    @Override
    protected MorphiaRepository<Collection.CollectionItem> provideCollectionItemRepo() {
        return MorphiaRepository.forType(Collection.CollectionItem.class).uninitialized();
    }

    @Override
    protected PrincipalAwareMorphiaRepository<Collection> provideCollectionRepo() {
        return PrincipalAwareMorphiaRepository.forType(Collection.class).uninitialized();
    }

    @Override
    protected PrincipalAwareMorphiaRepository<FileResource> provideResourceRepo() {
        return PrincipalAwareMorphiaRepository.forType(FileResource.class).uninitialized();
    }

    @Override
    protected PrincipalAwareMorphiaRepository<Share> provideShareRepo() {
        return PrincipalAwareMorphiaRepository.forType(Share.class).uninitialized();
    }

    @Override
    protected MorphiaRepository<RegisteredUser> provideRegisteredUserRepo() {
        return MorphiaRepository.forType(RegisteredUser.class).uninitialized();
    }

}
