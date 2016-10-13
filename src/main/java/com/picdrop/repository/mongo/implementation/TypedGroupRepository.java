/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo.implementation;

import com.picdrop.repository.mongo.MorphiaRepository;
import com.google.inject.Inject;
import com.picdrop.model.Group;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author i330120
 */
public class TypedGroupRepository extends MorphiaRepository<Group> {

//    @Inject
//    public TypedGroupRepository(DB db) {
//        super(db, "groups", Group.class);
//    }
    
    @Inject
    public TypedGroupRepository(Datastore ds) {
        super(ds, Group.class);
    }

}
