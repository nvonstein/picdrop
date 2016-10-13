/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo.implementation;

import com.picdrop.repository.mongo.MorphiaRepository;
import com.google.inject.Inject;
import com.mongodb.DB;
import com.picdrop.model.user.User;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author i330120
 */
public class TypedUserRepository extends MorphiaRepository<User> {

//    @Inject
//    public TypedUserRepository(DB db) {
//        super(db, "users", User.class);
//    }
    @Inject
    public TypedUserRepository(Datastore ds) {
        super(ds, User.class);
    }

}
