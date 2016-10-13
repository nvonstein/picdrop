/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.bulk.UpdateRequest;
import com.mongodb.client.result.UpdateResult;
import com.picdrop.repository.AdvancedRepository;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateResults;

/**
 *
 * @author i330120
 */
public class MorphiaAdvancedRepository<T> extends MorphiaRepository<T> implements AdvancedRepository<String, T> {

    public MorphiaAdvancedRepository(Datastore ds, Class<T> entityType) {
        super(ds, entityType);
    }

    @Override
    public int deleteNamed(String qname, Object... params) throws IOException {
        DBObject dbObj = compileQuery(qname, params);

        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        return ds.delete(query).getN();
    }

    @Override
    public List<T> updateNamed(T entity, String qname, Object... params) throws IOException {
        DBObject dbObj = compileQuery(qname, params);
        
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        UpdateResults ur = ds.updateFirst(query, entity, false);
        return Arrays.asList();
    }

}
