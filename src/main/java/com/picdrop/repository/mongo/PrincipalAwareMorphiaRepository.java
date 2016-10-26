/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.RegisteredUser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

/**
 *
 * @author i330120
 */
public class PrincipalAwareMorphiaRepository<T> extends MorphiaRepository<T> {

    @Inject
    Provider<RequestContext> contextProv;

    @Inject
    public PrincipalAwareMorphiaRepository(Datastore ds, Class<T> entityType) {
        super(ds, entityType);
    }

    protected DBObject addPrincipalClause(DBObject dbObj) {
        RequestContext context = contextProv.get();
        RegisteredUser principal = context.getPrincipal();

        if ((principal == null)) {
            return null;
        }

        dbObj.put("owner",
                new DBRef(ds.getCollection(principal.getClass()).getName(), new ObjectId(context.getPrincipal().getId()))
        );

        return dbObj;
    }

    @Override
    public T update(String id, T entity) {
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj);
        
        if (dbObj == null) {
            return null;
            // TODO log
        }
        
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        ds.updateFirst(query, entity, false);
        return get(id);
    }

    @Override
    public boolean delete(String id) {
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj);
        
        if (dbObj == null) {
            return false;
            // TODO log
        }
        
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        return ds.delete(query).getN() > 0;
    }

    @Override
    public T get(String id) {
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj);

        if (dbObj == null) {
            return null;
            // TODO log
        }

        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        return query.get();
    }

    @Override
    public List<T> list() {
        DBObject dbObj = addPrincipalClause(new BasicDBObject());

        if (dbObj == null) {
            return new ArrayList<>();
            // TODO log
        }

        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        return query.asList();
    }

    @Override
    public List<T> queryNamed(String qname, Object... params) throws IOException {
        DBObject dbObj = compileQuery(qname, params);

        dbObj = addPrincipalClause(dbObj);

        if (dbObj == null) {
            return new ArrayList<>();
            // TODO log
        }

        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        return query.asList();
    }
}
