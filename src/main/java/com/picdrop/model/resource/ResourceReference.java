/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.picdrop.model.Identifiable;
import com.picdrop.model.Resolvable;
import org.bson.types.ObjectId;

/**
 *
 * @author nvonstein
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FileResourceReference.class, name = "file")
    ,
    @JsonSubTypes.Type(value = CollectionReference.class, name = "collection")
})
public abstract class ResourceReference extends Identifiable implements Resolvable<Resource> {

    public ResourceReference() {
    }

    public ResourceReference(String _id) {
        super(_id);
    }

    public ResourceReference(ObjectId _id) {
        super(_id);
    }

    @JsonIgnore
    public boolean isCollection() {
        return false;
    }

    @JsonIgnore
    public boolean isFileResource() {
        return false;
    }

    @Override
    @JsonIgnore
    public abstract Resource resolve(boolean deep);

    public abstract String toResourceString();
}
