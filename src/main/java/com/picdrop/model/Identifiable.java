/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

/**
 *
 * @author i330120
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Identifiable {

    @Id
    protected ObjectId _id;

    public Identifiable() {
    }

    public Identifiable(String _id) {
        try {
            this._id = new ObjectId(_id);
        } catch (Exception e) {
            this._id = null;
        }
    }

    public Identifiable(ObjectId _id) {
        this._id = _id;
    }

    @JsonView(value = Views.Public.class)
    public String getId() {
        return (_id != null) ? _id.toHexString() : null;
    }

    @JsonProperty("id")
    @JsonView(value = Views.Public.class)
    public void setId(String _id) {
        this._id = _id != null
                ? new ObjectId(_id)
                : null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Identifiable)) {
            return false;
        }
        final Identifiable other = (Identifiable) obj;
        if (!Objects.equals(this._id, other._id)) {
            return false;
        }
        return true;
    }

}
