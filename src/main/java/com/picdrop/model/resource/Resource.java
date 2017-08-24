/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.picdrop.model.Identifiable;
import com.picdrop.model.Mergeable;
import com.picdrop.model.user.RegisteredUser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author i330120
 */
public abstract class Resource extends Identifiable implements Mergeable<Resource>{
    
    protected long created;
    protected String name;
    
    @Reference
    protected RegisteredUser owner;
    
    protected List<String> shareIds = new ArrayList<>();

    public Resource() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public Resource(String _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public Resource(ObjectId _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonProperty
    public long getCreated() {
        return created;
    }

    @JsonIgnore
    public void setCreated(long created) {
        this.created = created;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public RegisteredUser getOwner() {
        return owner;
    }

    @JsonIgnore
    public void setOwner(RegisteredUser owner) {
        this.owner = owner;
    }

    @JsonProperty
    public List<String> getShareIds() {
        return shareIds;
    }
    
    @JsonIgnore
    public Resource addShareId(String id) {
        this.shareIds.add(id);
        return this;
    }
    
    @JsonIgnore
    public Resource deleteShareId(String id) {
        this.shareIds.remove(id);
        return this;
    }

    @JsonIgnore
    public void setShareIds(List<String> shareIds) {
        this.shareIds = shareIds;
    }
    
    @Override
    public Resource merge(Resource update) throws IOException {
        if (update == null) {
            return this;
        }
        if (update.name != null) {
            this.name = update.name;
        }
        return this;
    }

    @JsonIgnore
    public boolean isCollection() {
        return false;
    }
    
    public boolean isFile() {
        return false;
    }
    
}
