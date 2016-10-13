/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import java.util.Objects;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

/**
 *
 * @author i330120
 */
public class Identifiable {
    
    @Id
    protected ObjectId _id;

    public Identifiable() {
    }

    public Identifiable(String _id) {
        this._id = new ObjectId(_id);
    }
    
    public Identifiable(ObjectId _id) {
        this._id = _id;
    }
    

    public String getId() {
        return _id.toHexString();
    }

    public void setId(String _id) {
        this._id = new ObjectId(_id);
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
