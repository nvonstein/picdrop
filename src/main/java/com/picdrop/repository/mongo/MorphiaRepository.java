/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.picdrop.repository.Repository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;

/**
 *
 * @author i330120
 */
public class MorphiaRepository<T> implements Repository<String, T> {

    Datastore ds;
    Class<T> entityType;

    Map<String, String> namedQueries;
    ObjectMapper mapper;

    public MorphiaRepository(Datastore ds, Class<T> entityType) {
        this.ds = ds;
        this.entityType = entityType;
    }

    @Inject
    public void setNamedQueries(@Named("queries") Map<String, String> namedQueries) {
        this.namedQueries = namedQueries;
    }

    @Inject
    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    protected boolean isValidIdentifier(String in) {
        return !Strings.isNullOrEmpty(in) && (in.length() == 24);
    }

    @Override
    public T save(T entity) {
        Key<T> k = ds.save(entity);
        return ds.getByKey(entityType, k);
    }

    @Override
    public T get(String id) {
        if (!isValidIdentifier(id)) {
            return null;
        }
        return ds.get(entityType, new ObjectId(id));
    }

    @Override
    public boolean delete(String id) {
        if (!isValidIdentifier(id)) {
            return false;
        }
        WriteResult wr = ds.delete(entityType, new ObjectId(id));
        return wr.getN() > 0;
    }

    @Override
    public T update(String id, T entity) {
        if (!isValidIdentifier(id)) {
            return null;
        }
        Query<T> q = ds.createQuery(entityType).field("_id").equal(new ObjectId(id));
        ds.updateFirst(q, entity, false);
        return get(id);
    }

    @Override
    public List<T> list() {
        return ds.find(entityType).asList();
    }

    protected DBObject compileQuery(String qname, Object... params) throws IOException {
        String rawquery = this.namedQueries.get(qname);
        if (rawquery == null) {
            throw new IllegalArgumentException("Query not found");
        }

        if (params.length > 0) {
            int i = 0;
            for (Object p : params) {
                rawquery = rawquery.replaceAll(String.format("[?]%s", i), Matcher.quoteReplacement(mapper.writeValueAsString(p)));
                i++;
            }
        }

        return BasicDBObject.parse(rawquery);
    }

    @Override
    public List<T> queryNamed(String qname, Object... params) throws IOException {
        DBObject dbObj = compileQuery(qname, params);

        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        return query.asList();
    }

}
