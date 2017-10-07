/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;
import com.picdrop.guice.names.Queries;
import static com.picdrop.helper.LogHelper.*;
import com.picdrop.repository.Repository;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
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
    WriteConcern wc = WriteConcern.ACKNOWLEDGED;

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

    public void setWriteConcern(WriteConcern wc) {
        this.wc = wc;
    }

    protected boolean isValidIdentifier(String in) {
        return !Strings.isNullOrEmpty(in) && (in.length() == 24);
    }

    @Override
    public T save(T entity) {
        log.traceEntry();
        log.debug(REPO_SAVE, "Saving entity of type '{}'", this.entityType.toString());
        Key<T> k = ds.save(entity, this.wc);
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
        Query<T> q = ds.createQuery(entityType).field("_id").equal(new ObjectId(id));
        WriteResult wr = ds.delete(q, wc);
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

    protected UpdateOperations<T> compileUpdateOperation(Map<String, Object> flist) throws IOException {
        UpdateOperations<T> uo = ds.createUpdateOperations(entityType);
        for (Entry<String, Object> e : flist.entrySet()) {
            if (Strings.isNullOrEmpty(e.getKey())) {
                throw new IOException("invalid field provided must not be 'null' or empty");
            }
            if (e.getValue().getClass().isArray()) {
                Object[] values = (Object[]) e.getValue();
                String nfield = e.getKey().substring(1);

                char op = e.getKey().charAt(0);
                switch (op) {
                    case '+':
                        for (Object value : values) {
                            uo.add(nfield, value, true);
                        }
                        continue;
                    case '?':
                        for (Object value : values) {
                            uo.add(nfield, value, false);
                        }
                        continue;
                    default: // Threat as regular field
                        break;
                }
            }

            if (e.getValue() != null) {
                uo.set(e.getKey(), e.getValue());
            } else {
                uo.unset(e.getKey());
            }
        }

        return uo;
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

        List<T> result = queryNamedInternal(dbObj);
        log.traceExit();
        return result;
    }

    protected List<T> queryNamedInternal(DBObject dbObj) {
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        return query.asList();
    }

    @Override
    public int deleteNamed(String qname, Object... params) throws IOException {
        log.traceEntry();
        log.debug(REPO_DELETE, "Deleting entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        DBObject dbObj = compileQuery(qname, params);

        int n = deleteNamedInternal(dbObj);

        log.traceExit();
        return n;
    }

    protected int deleteNamedInternal(DBObject dbObj) {
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        return ds.delete(query).getN();
    }

    @Override
    public List<T> updateNamed(T entity, String qname, Object... params) throws IOException {
        log.traceEntry();
        log.debug(REPO_UPDATE, "Updating entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        DBObject dbObj = compileQuery(qname, params);

        T result = updateNamedInternal(entity, dbObj);

        log.traceExit();
        return Arrays.asList(result);
    }

    protected T updateNamedInternal(T entity, DBObject dbObj) {
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        UpdateResults ur = ds.updateFirst(query, entity, false);
        return entity;
    }

    @Override
    public int updateNamed(Map<String, Object> flist, String qname, Object... params) throws IOException {
        log.traceEntry();
        log.debug(REPO_UPDATE, "Updating entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        DBObject dbObj = compileQuery(qname, params);

        int n = updateNamedInternal(flist, dbObj);

        log.debug(REPO_UPDATE, "Updated '{}' entities", n);
        log.traceExit();
        return n;
    }

    protected int updateNamedInternal(Map<String, Object> flist, DBObject dbObj) throws IOException {
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        UpdateOperations<T> uo = compileUpdateOperation(flist);

        UpdateResults ur = ds.update(query, uo, false);

        return ur.getUpdatedCount();
    }

    public static class BuildState<BUILDER extends AbstractRepositoryBuilder> {

        BUILDER builder;

        BuildState(BUILDER builder) {
            this.builder = builder;
        }

        public BuildState<BUILDER> withMapper(ObjectMapper mapper) {
            this.builder.withMapper(mapper);
            return this;
        }

        public BuildState<BUILDER> withQueries(Map<String, String> queries) {
            this.builder.withQueries(queries);
            return this;
        }

        public BuildState<BUILDER> withWriteConcern(WriteConcern wc) {
            this.builder.withWriteConcern(wc);
            return this;
        }

        public BUILDER withDatastore(Datastore ds) {
            this.builder.withDatastore(ds);
            return builder;
        }
    }

    protected static abstract class AbstractRepositoryBuilder<BUILDER extends AbstractRepositoryBuilder<BUILDER, TARGET, TYPE>, TARGET extends MorphiaRepository<TYPE>, TYPE> {

        protected final Class<TYPE> clazz;
        private final Class<BUILDER> builderType;

        protected ObjectMapper mapper;
        protected Map<String, String> queries;
        protected WriteConcern wc;
        protected Datastore ds;

        protected AbstractRepositoryBuilder(Class<TYPE> clazz) {
            this.clazz = clazz;
            this.builderType = (Class) new TypeLiteral<BUILDER>() {
            }.getRawType();
        }

        protected BUILDER doReturn() {
            return builderType.cast(this);
        }

        public BUILDER withMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return doReturn();
        }

        public BUILDER withQueries(Map<String, String> queries) {
            this.queries = queries;
            return doReturn();
        }

        public BUILDER withWriteConcern(WriteConcern wc) {
            this.wc = wc;
            return doReturn();
        }

        public BUILDER withDatastore(Datastore ds) {
            this.ds = ds;
            return doReturn();
        }

        public abstract TARGET build();

    }

    public static class Builder<TYPE> extends AbstractRepositoryBuilder<Builder<TYPE>, MorphiaRepository<TYPE>, TYPE> {

        Builder(Class<TYPE> clazz) {
            super(clazz);
        }

        public static <TYPE> BuildState<Builder<TYPE>> forType(Class<TYPE> clazz) {
            return new BuildState<>(new Builder<>(clazz));
        }

        @Override
        public MorphiaRepository<TYPE> build() {
            MorphiaRepository<TYPE> repo = new MorphiaRepository<>(this.ds, this.clazz);
            repo.setDatastore(this.ds);
            repo.setMapper(this.mapper);
            repo.setNamedQueries(this.queries);

            return repo;
        }

    }

}
