/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.user.RegisteredUser;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author i330120
 */
@Entity("shares")
public class Share extends Identifiable {
      
    protected long created;
   
    @Indexed
    protected String uri;
    
    @Reference
    protected Resource resource;
    
    @Reference
    protected RegisteredUser owner;

    public Share() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public Share(String _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public Share(ObjectId _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonProperty
    public long getCreated() {
        return created;
    }

    @JsonProperty
    public void setCreated(long created) {
        this.created = created;
    }

    @JsonProperty
    public String getUri() {
        return uri;
    }

    @JsonIgnore
    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonProperty
    public Resource getResource() {
        return resource;
    }

    @JsonProperty
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @JsonProperty
    public RegisteredUser getOwner() {
        return owner;
    }

    @JsonIgnore
    public void setOwner(RegisteredUser owner) {
        this.owner = owner;
    }
    
    
}
