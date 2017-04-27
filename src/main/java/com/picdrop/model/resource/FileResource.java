/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author i330120
 */
@Entity("files")
//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
//@JsonSubTypes({
//    @JsonSubTypes.Type(value = Image.class, name = "image")})
public class FileResource extends Resource {

    protected String extension;
    protected String fileId;
    @Deprecated
    protected String fileUri;

    @Embedded
    ResourceDescriptor descriptor;

    public FileResource() {
        super();
    }

    public FileResource(String _id) {
        super(_id);

    }

    public FileResource(ObjectId _id) {
        super(_id);
    }

    @JsonIgnore
    @Override
    public void setName(String name) {
        this.name = name;
        String[] tmp = name.split("\\.");
        if (tmp.length > 1) {
            this.setExtension(tmp[tmp.length - 1]);
        }
    }

    @JsonProperty
    @Deprecated
    public String getFileUri() {
        return fileUri;
    }

    @JsonIgnore
    @Deprecated
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
    public ResourceDescriptor getDescriptor() {
        return descriptor;
    }

    @JsonIgnore
    public void setDescriptor(ResourceDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    
}
