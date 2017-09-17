/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.picdrop.guice.names.Queries;
import java.util.Map;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author nvonstein
 */
public class RepositoryPrototype {
    
    final ObjectMapper mapper;
    final Map<String, String> queries;
    final Datastore ds;

    @Inject
    public RepositoryPrototype(Datastore ds, ObjectMapper mapper, @Queries Map<String, String> queries) {
        this.ds = ds;
        this.mapper = mapper;
        this.queries = queries;
    }
    
}
