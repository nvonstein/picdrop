/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    
    @NotSaved
    protected byte[] file;
    
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
    
    public long getCreated() {
        return created;
    }
    
    public void setCreated(long created) {
        this.created = created;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        String[] tmp = name.split("\\.");
        if (tmp.length > 1) {
            this.setExtension(tmp[tmp.length - 1]);
        }
    }
    
    public String getFileUri() {
        return fileUri;
    }
    
    @JsonIgnore
    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
    
    public String getExtension() {
        return extension;
    }
    
    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    public byte[] getFile() {
        return file;
    }
    
    public void setFile(byte[] file) {
        this.file = file;
    }
    
    public RegisteredUser getOwner() {
        return owner;
    }
    
    public void setOwner(RegisteredUser owner) {
        this.owner = owner;
    }

    public ResourceDescriptor getDescriptor() {
        return descriptor;
    }

    @JsonIgnore
    public void setDescriptor(ResourceDescriptor descriptor) {
        this.descriptor = descriptor;
    }
}
