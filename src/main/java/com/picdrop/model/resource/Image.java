/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.picdrop.model.Identifiable;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author i330120
 */
@Entity("images")
public class Image extends Identifiable {
    
    
    @Indexed
    @Reference
    protected Resource parent;

    public Image() {
    }

    public Image(String _id) {
        super(_id);
    }

    public Image(ObjectId _id) {
        super(_id);
    }

    @JsonIgnore
    @Override
    public void setId(String _id) {
        super.setId(_id);
    }

    @JsonIgnore
    @Override
    public String getId() {
        return super.getId();
    }
    
    @JsonIgnore
    public Resource getParent() {
        return parent;
    }

    @JsonIgnore
    public void setParent(Resource parent) {
        this.parent = parent;
    }
    
    static public Image withParent(Resource parent) {
        Image i = new Image();
        i.setParent(parent);
        return i;
    }
}
