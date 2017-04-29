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
import com.picdrop.model.Group;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.mongo.NamedQueries;
import com.picdrop.repository.Repository;
import com.picdrop.repository.mongo.MorphiaRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaRepository;
import com.picdrop.repository.mongo.implementation.TypedGroupRepository;
import com.picdrop.repository.mongo.implementation.TypedUserRepository;
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

        binder.bind(new TypeLiteral<Repository<String, User>>() {
        }).annotatedWith(Names.named("users")).to(TypedUserRepository.class);

        binder.bind(new TypeLiteral<Repository<String, Group>>() {
        }).annotatedWith(Names.named("groups")).to(TypedGroupRepository.class);

        MongoClient client = new MongoClient();
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.picdrop.model");
        Datastore ds = morphia.createDatastore(client, "test");

        binder.bind(MongoDatabase.class).toInstance(client.getDatabase("test"));
        binder.bind(Datastore.class).toInstance(ds);

        // Registered user repo
        binder.bind(new TypeLiteral<Repository<String, RegisteredUser>>() {
        }).toInstance(new MorphiaRepository<>(ds, RegisteredUser.class));
        // Resource repo
        binder.bind(new TypeLiteral<Repository<String, FileResource>>() {
        }).toInstance(new PrincipalAwareMorphiaRepository<>(ds, FileResource.class));
        // Collections repo
        binder.bind(new TypeLiteral<Repository<String, Collection>>() {
        }).toInstance(new PrincipalAwareMorphiaRepository<>(ds, Collection.class));
        // Collectionitem repo
        binder.bind(new TypeLiteral<Repository<String, Collection.CollectionItem>>() {
        }).toInstance(new MorphiaRepository<>(ds, Collection.CollectionItem.class));
    }

}
