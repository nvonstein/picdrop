/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.picdrop.model.Identifiable;
import com.picdrop.model.Resolvable;
import org.bson.types.ObjectId;

/**
 *
 * @author nvonstein
 */
public abstract class UserReference extends Identifiable implements Resolvable<User> {

    UserReference() {
    }

    public UserReference(String _id) {
        super(_id);
    }

    public UserReference(ObjectId _id) {
        super(_id);
    }

    @Override
    @JsonIgnore
    public abstract User resolve(boolean deep);

}
