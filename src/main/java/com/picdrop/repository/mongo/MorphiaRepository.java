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
import com.picdrop.guice.names.Queries;
import com.picdrop.repository.Repository;
import static com.picdrop.helper.LogHelper.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    Logger log;

    public MorphiaRepository(Datastore ds, Class<T> entityType) {
        this.ds = ds;
        this.entityType = entityType;
        this.log = LogManager.getLogger();
    }

    @Inject
    public void setNamedQueries(@Queries Map<String, String> namedQueries) {
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
        log.traceEntry();
        log.debug(REPO_SAVE, "Saving entity of type '{}'", this.entityType.toString());
        Key<T> k = ds.save(entity);
        T e = ds.getByKey(entityType, k);
        log.traceExit();
        return e;
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

    public static <K> IntermediateStateBuilder<TypedRepositoryBuilder<K>> forType(Class<K> clazz) {
        return new IntermediateStateBuilder<>(new TypedRepositoryBuilder<>(clazz));
    }

    public static class TypedRepositoryBuilder<K> {

        Class<K> clazz;

        ObjectMapper mapper;
        Map<String, String> queries;
        Datastore ds;

        TypedRepositoryBuilder(Class<K> clazz) {
            this.clazz = clazz;
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

    public static class IntermediateStateBuilder<BUILDER extends TypedRepositoryBuilder<?>> {

        private final BUILDER builder;

        public IntermediateStateBuilder(BUILDER builder) {
            this.builder = builder;
        }

        public IntermediateStateBuilder<BUILDER> withMapper(ObjectMapper mapper) {
            this.builder.mapper = mapper;
            return this;
        }

        public IntermediateStateBuilder<BUILDER> withQueries(Map<String, String> queries) {
            this.builder.queries = queries;
            return this;
        }

        public BUILDER withDatastore(Datastore ds) {
            builder.ds = ds;
            return builder;
        }

        public BUILDER from(RepositoryPrototype prototype) {
            builder.ds = prototype.ds;
            builder.mapper = prototype.mapper;
            builder.queries = prototype.queries;
            return builder;
        }
    }
}
