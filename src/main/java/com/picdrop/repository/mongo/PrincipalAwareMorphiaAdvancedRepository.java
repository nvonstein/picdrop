/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import com.mongodb.DBObject;
import static com.picdrop.helper.LogHelper.*;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareAdvancedRepository;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateResults;

/**
 *
 * @author nvonstein
 */
public class PrincipalAwareMorphiaAdvancedRepository<T> extends PrincipalAwareMorphiaRepository<T> implements AwareAdvancedRepository<String, T, User> {

    public PrincipalAwareMorphiaAdvancedRepository(Datastore ds, Class<T> entityType) {
        super(ds, entityType);
        this.log = LogManager.getLogger();
    }

    @Override
    public int deleteNamed(String qname, User context, Object... params) throws IOException {
        log.traceEntry();
        DBObject dbObj = compileQuery(qname, params);

        if (context != null) {
            dbObj = addPrincipalClause(dbObj, context);
        }

        log.debug(REPO_DELETE, "Deleting entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        log.traceExit();
        return ds.delete(query).getN();
    }

    @Override
    public List<T> updateNamed(T entity, String qname, User context, Object... params) throws IOException {
        log.traceEntry();
        DBObject dbObj = compileQuery(qname, params);

        if (context != null) {
            dbObj = addPrincipalClause(dbObj, context);
        }

        log.debug(REPO_UPDATE, "Updating entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        UpdateResults ur = ds.updateFirst(query, entity, false);
        log.traceExit();
        return Arrays.asList();
    }

    @Override
    public int deleteNamed(String qname, Object... params) throws IOException {
        log.traceEntry();
        DBObject dbObj = compileQuery(qname, params);

        dbObj = addPrincipalClause(dbObj);

        log.debug(REPO_DELETE, "Deleting entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        log.traceExit();
        return ds.delete(query).getN();
    }

    @Override
    public List<T> updateNamed(T entity, String qname, Object... params) throws IOException {
        log.traceEntry();
        DBObject dbObj = compileQuery(qname, params);

        dbObj = addPrincipalClause(dbObj);

        log.debug(REPO_UPDATE, "Updating entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);

        UpdateResults ur = ds.updateFirst(query, entity, false);
        log.traceExit();
        return Arrays.asList();
    }

    public static <K> IntermediateStateBuilder<K> forType(Class<K> clazz) {
        return new IntermediateStateBuilder<>(clazz);
    }

    public static class IntermediateStateBuilder<K> extends PrincipalAwareMorphiaRepository.IntermediateStateBuilder<K> {

        IntermediateStateBuilder(Class<K> clazz) {
            super(clazz);
        }

        @Override
        public TypedRepositoryBuilder<K> from(RepositoryPrototype prototype) {
            super.from(prototype);
            return new TypedRepositoryBuilder<>(this);
        }

        @Override
        public TypedRepositoryBuilder<K> withDatastore(Datastore ds) {
            super.withDatastore(ds);
            return new TypedRepositoryBuilder<>(this);
        }

    }

    public static class TypedRepositoryBuilder<K> extends PrincipalAwareMorphiaRepository.TypedRepositoryBuilder<K> {

        public TypedRepositoryBuilder(IntermediateStateBuilder<K> state) {
            super(state);
        }

        @Override
        public PrincipalAwareMorphiaAdvancedRepository<K> build() {
            return setFields(new PrincipalAwareMorphiaAdvancedRepository<>(this.ds, this.clazz));
        }

    }
}
