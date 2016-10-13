/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository;

import com.picdrop.repository.mongo.MongoRepository;
import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;

/**
 *
 * @author i330120
 */
public class UserRepository extends MongoRepository {
    
    @Inject
    public UserRepository(MongoDatabase db) {
        super(db, "users");
    }
    
}
