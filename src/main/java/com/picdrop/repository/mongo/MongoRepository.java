/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository.mongo;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.picdrop.repository.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bson.types.ObjectId;

/**
 *
 * @author i330120
 */
public abstract class MongoRepository implements Repository<String, String> {
    
    Map<String,String> NAMED_QUERIES;

    final MongoDatabase db;
    final String collection;
    

    public MongoRepository(MongoDatabase db, String collection) {
        this.db = db;
        this.collection = collection;
    }

    @Inject
    public void setNamedQueries(@Named("queries") Map<String, String> namedQueries) {
        this.NAMED_QUERIES = namedQueries;
    }
    

    @Override
    public String save(String entity) {
        Document d = Document.parse(entity);
        this.db.getCollection(this.collection).insertOne(d);
        String res = d.toJson(new JsonWriterSettings(JsonMode.STRICT));
        return res;
    }

    @Override
    public String get(String id) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
        FindIterable<Document> res = this.db.getCollection(this.collection).find(query);
        return res.first().toJson();
    }

    @Override
    public boolean delete(String id) {
        BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
        this.db.getCollection(this.collection).findOneAndDelete(query);
        return true;
    }

    @Override
    public String update(String id, String entity) {
        Document d = Document.parse(entity);
        BasicDBObject query = new BasicDBObject("_id", d.getObjectId(d));
        d = this.db.getCollection(this.collection).findOneAndUpdate(query, d, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
        return d.toJson();
    }

    @Override
    public List<String> list() {
        final List<String> res = new ArrayList<>();
        this.db.getCollection(collection).find().forEach(new Consumer<Document>() {
            @Override
            public void accept(Document t) {
                res.add(t.toJson());
            }
        });
        return res;
    }

    @Override
    public List<String> queryNamed(String name, Object... params) {
        final List<String> res = new ArrayList<>();
        String rawquery = this.NAMED_QUERIES.get(name);
        if (rawquery == null) {
            throw new IllegalArgumentException("Query not found");
        }
        
        Document query = Document.parse(rawquery);
        this.db.getCollection(this.collection).find(query).forEach(new Consumer<Document>() {
            @Override
            public void accept(Document t) {
                res.add(t.toJson());
            }
        });
        return res;
    }

}
