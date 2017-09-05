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
import com.picdrop.model.RequestContext;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import static com.picdrop.helper.LogHelper.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

/**
 *
 * @author i330120
 */
public class PrincipalAwareMorphiaRepository<T> extends MorphiaRepository<T> implements AwareRepository<String, T, User> {

    @Inject
    Provider<RequestContext> contextProv;

    @Inject
    public PrincipalAwareMorphiaRepository(Datastore ds, Class<T> entityType) {
        super(ds, entityType);
        this.log = LogManager.getLogger();
    }

    protected DBObject addPrincipalClause(DBObject dbObj) {
        RequestContext context = contextProv.get();
        User principal = context.getPrincipal();

        return addPrincipalClause(dbObj, principal);
    }

    protected DBObject addPrincipalClause(DBObject dbObj, User principal) {
        if ((principal == null)) {
            return null;
        }
        if (!isValidIdentifier(principal.getId())) {
            return null;
        }

        log.debug(REPO, "Adding principle clause");
        dbObj.put("owner", new BasicDBObject("_id", new ObjectId(principal.getId())));

        return dbObj;
    }

    @Override
    public T update(String id, T entity) {
        log.traceEntry();
        if (!isValidIdentifier(id)) {
            return null;
        }
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj);

        if (dbObj == null) {
            return null;
            // TODO log
        }

        log.debug(REPO_UPDATE, "Updating entity of type '{}'", this.entityType.toString());
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        ds.updateFirst(query, entity, false);

        log.traceExit();
        return get(id);
    }

    @Override
    public boolean delete(String id) {
        log.traceEntry();
        if (!isValidIdentifier(id)) {
            return false;
        }
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj);

        if (dbObj == null) {
            return false;
            // TODO log
        }

        log.debug(REPO_DELETE, "Deleting entity of type '{}'", this.entityType.toString());
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return ds.delete(query).getN() > 0;
    }

    @Override
    public T get(String id) {
        log.traceEntry();
        if (!isValidIdentifier(id)) {
            return null;
        }
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj);

        if (dbObj == null) {
            return null;
            // TODO log
        }

        log.debug(REPO_GET, "Getting entity of type '{}'", this.entityType.toString());
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return query.get();
    }

    @Override
    public List<T> list() {
        log.traceEntry();
        DBObject dbObj = addPrincipalClause(new BasicDBObject());

        if (dbObj == null) {
            return new ArrayList<>();
            // TODO log
        }

        log.debug(REPO_GET, "Listing entity of type '{}'", this.entityType.toString());
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return query.asList();
    }

    @Override
    public List<T> queryNamed(String qname, Object... params) throws IOException {
        log.traceEntry();
        DBObject dbObj = compileQuery(qname, params);

        dbObj = addPrincipalClause(dbObj);

        if (dbObj == null) {
            return new ArrayList<>();
            // TODO log
        }

        log.debug(REPO_GET, "Querying entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return query.asList();
    }

    @Override
    public T save(T entity, User context) {
        return super.save(entity);
    }

    @Override
    public T get(String id, User context) {
        log.traceEntry();
        if (context == null) {
            return super.get(id);
        }
        if (!isValidIdentifier(id)) {
            return null;
        }
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj, context);

        if (dbObj == null) {
            return null;
            // TODO log
        }

        log.debug(REPO_GET, "Getting entity of type '{}'", this.entityType.toString());
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return query.get();
    }

    @Override
    public boolean delete(String id, User context) {
        log.traceEntry();
        if (context == null) {
            return super.delete(id);
        }
        if (!isValidIdentifier(id)) {
            return false;
        }
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj, context);

        if (dbObj == null) {
            return false;
            // TODO log
        }

        log.debug(REPO_DELETE, "Deleting entity of type '{}'", this.entityType.toString());
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return ds.delete(query).getN() > 0;
    }

    @Override
    public T update(String id, T entity, User context) {
        log.traceEntry();
        if (context == null) {
            return super.update(id, entity);
        }
        if (!isValidIdentifier(id)) {
            return null;
        }
        DBObject dbObj = new BasicDBObject("_id", new ObjectId(id));

        dbObj = addPrincipalClause(dbObj, context);

        if (dbObj == null) {
            return null;
            // TODO log
        }

        log.debug(REPO_UPDATE, "Updating entity of type '{}'", this.entityType.toString());
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        ds.updateFirst(query, entity, false);
        log.traceExit();
        return get(id);
    }

    @Override
    public List<T> list(User context) {
        log.traceEntry();
        if (context == null) {
            return super.list();
        }
        DBObject dbObj = addPrincipalClause(new BasicDBObject(), context);

        if (dbObj == null) {
            return new ArrayList<>();
            // TODO log
        }

        log.debug(REPO_GET, "Listing entity of type '{}'", this.entityType.toString());
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return query.asList();
    }

    @Override
    public List<T> queryNamed(String qname, User context, Object... params) throws IOException {
        log.traceEntry();
        if (context == null) {
            return super.queryNamed(qname, params);
        }
        DBObject dbObj = compileQuery(qname, params);

        dbObj = addPrincipalClause(dbObj, context);

        if (dbObj == null) {
            return new ArrayList<>();
            // TODO log
        }

        log.debug(REPO_GET, "Querying entity of type '{}' with query '{}'", this.entityType.toString(), qname);
        Query<T> query = ds.getQueryFactory().createQuery(ds, ds.getCollection(entityType), entityType, dbObj);
        log.traceExit();
        return query.asList();
    }
}
