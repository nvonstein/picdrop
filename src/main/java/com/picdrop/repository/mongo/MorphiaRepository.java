/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.picdrop.guice.names.Queries;
import static com.picdrop.helper.LogHelper.*;
import com.picdrop.repository.Repository;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateResults;

/**
 *
 * @author i330120
 */
public class MorphiaRepository<T> implements Repository<String, T> {

    Class<T> entityType;

    // Must use field injection as Mockito disturbes any other type of injection
    @Inject
    @Queries
    Map<String, String> namedQueries;
    @Inject
    ObjectMapper mapper;
    @Inject
    Datastore ds;

    Logger log;

    MorphiaRepository(Class<T> entityType) {
        this.entityType = entityType;
        this.log = LogManager.getLogger();
    }

    public MorphiaRepository(Datastore ds, Class<T> entityType) {
        this.ds = ds;
        this.entityType = entityType;
        this.log = LogManager.getLogger();
    }

    public void setDatastore(Datastore ds) {
        this.ds = ds;
    }

    public void setNamedQueries(Map<String, String> namedQueries) {
        this.namedQueries = namedQueries;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    protected boolean isValidIdentifier(String in) {
        return !Strings.isNullOrEmpty(in) && (in.length() == 24);
    }

    @Override
    public T save(T entity) {
        log.traceEntry();
        log.debug(REPO_SAVE, "Saving entity of type '{}'", this.entityType.toString());
        Key<T> k = ds.save(entity, ds.getDefaultWriteConcern());
        return entity;
    }

    @Override
    public T get(String id) {
        log.traceEntry();
        log.debug(REPO_GET, "Getting entity of type '{}'", this.entityType.toString());
        if (!isValidIdentifier(id)) {
            return null;
        }
        T e = ds.get(entityType, new ObjectId(id));
        log.traceExit();
        return e;
    }

    @Override
    public boolean delete(String id) {
        log.traceEntry();
        log.debug(REPO_DELETE, "Deleting entity of type '{}'", this.entityType.toString());
        if (!isValidIdentifier(id)) {
            return false;
        }
        WriteResult wr = ds.delete(entityType, new ObjectId(id));
        log.traceExit();
        return wr.getN() > 0;
    }

    @Override
    public T update(String id, T entity) {
        log.traceEntry();
        log.debug(REPO_UPDATE, "Updating entity of type '{}'", this.entityType.toString());
        if (!isValidIdentifier(id)) {
            return null;
        }
        Query<T> q = ds.createQuery(entityType).field("_id").equal(new ObjectId(id));
        ds.updateFirst(q, entity, false);
        log.traceExit();
        return get(id);
    }

    @Override
    public List<T> list() {
        log.debug(REPO_GET, "Listing entity of type '{}'", this.entityType.toString());
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
        log.traceEntry();
        log.debug(REPO_GET, "Querying entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        DBObject dbObj = compileQuery(qname, params);

        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return query.asList();
    }
    
     @Override
    public int deleteNamed(String qname, Object... params) throws IOException {
        log.traceEntry();
        log.debug(REPO_DELETE, "Deleting entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        DBObject dbObj = compileQuery(qname, params);

        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        log.traceExit();
        return ds.delete(query).getN();
    }

    @Override
    public List<T> updateNamed(T entity, String qname, Object... params) throws IOException {
        log.traceEntry();
        log.debug(REPO_UPDATE, "Updating entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        DBObject dbObj = compileQuery(qname, params);

        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        // TODO This must be changed if multi-update is desired on named queries
        UpdateResults ur = ds.updateFirst(query, entity, false);

        log.traceExit();
        return Arrays.asList();
    }

    public static <K> IntermediateStateBuilder<K> forType(Class<K> clazz) {
        return new IntermediateStateBuilder<>(clazz);
    }

    public static class IntermediateStateBuilder<K> {

        Class<K> clazz;

        ObjectMapper mapper;
        Map<String, String> queries;
        Datastore ds;

        IntermediateStateBuilder(Class<K> clazz) {
            this.clazz = clazz;
        }

        private IntermediateStateBuilder<K> unwrapPrototype(RepositoryPrototype proto) {
            this.withQueries(proto.queries)
                    .withMapper(proto.mapper)
                    .withDatastore(proto.ds);
            return this;
        }

        public IntermediateStateBuilder<K> withMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public IntermediateStateBuilder<K> withQueries(Map<String, String> queries) {
            this.queries = queries;
            return this;
        }

        public TypedRepositoryBuilder<K> withDatastore(Datastore ds) {
            this.ds = ds;
            return new TypedRepositoryBuilder<>(this);
        }

        public MorphiaRepository<K> uninitialized() {
            return new MorphiaRepository<>(this.clazz);
        }

        public TypedRepositoryBuilder<K> from(RepositoryPrototype prototype) {
            return new TypedRepositoryBuilder<>(unwrapPrototype(prototype));
        }

    }

    public static class TypedRepositoryBuilder<K> extends IntermediateStateBuilder<K> {

        private TypedRepositoryBuilder(Class<K> clazz) {
            super(clazz);
        }

        TypedRepositoryBuilder(IntermediateStateBuilder<K> state) {
            this(state.clazz);
            this.ds = state.ds;
            this.mapper = state.mapper;
            this.queries = state.queries;
        }

        protected <U extends MorphiaRepository<K>> U setFields(U repo) {
            repo.setMapper(this.mapper);
            repo.setNamedQueries(this.queries);
            return repo;
        }

        public MorphiaRepository<K> build() {
            return setFields(new MorphiaRepository<>(this.ds, this.clazz));
        }
    }

}
