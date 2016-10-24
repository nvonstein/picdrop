/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.picdrop.model.Identifiable;
import com.picdrop.model.user.RegisteredUser;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author i330120
 */
@Entity("resources")
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
//@JsonSubTypes({
//    @JsonSubTypes.Type(value = Image.class, name = "image")})
public class Resource extends Identifiable {
    
    protected long created;
    protected String name;
    protected String extension;
    
    protected String fileUri;
    
    @Embedded
    ResourceDescriptor descriptor;
    
    @Reference
    protected RegisteredUser owner;
    
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
        String[] tmp = name.split("\\.");
        if (tmp.length > 1) {
            this.setExtension(tmp[tmp.length - 1]);
        }
    }
    
    @JsonProperty
    public String getFileUri() {
        return fileUri;
    }
    
    @JsonIgnore
    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
    
    @JsonProperty
    public String getExtension() {
        return extension;
    }
    
    @JsonProperty
    public void setExtension(String extension) {
        this.extension = extension;
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
    public ResourceDescriptor getDescriptor() {
        return descriptor;
    }

    @JsonIgnore
    public void setDescriptor(ResourceDescriptor descriptor) {
        this.descriptor = descriptor;
    }
}
