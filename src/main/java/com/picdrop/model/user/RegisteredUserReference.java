/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.google.inject.Inject;
import com.picdrop.repository.Repository;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.NotSaved;

/**
 *
 * @author nvonstein
 */
public class RegisteredUserReference extends UserReference {

    @Inject
    protected static Repository<String, RegisteredUser> repo;

    @NotSaved
    protected RegisteredUser user;

    public RegisteredUserReference() {
    }

    public RegisteredUserReference(String _id) {
        super(_id);
    }

    public RegisteredUserReference(ObjectId _id) {
        super(_id);
    }

    RegisteredUserReference(RegisteredUser user) {
        super(user.getId());
        this.user = user;
    }

    @Override
    public RegisteredUser resolve(boolean deep) {
        if (this.user == null) {
            this.user = repo.get(this.getId());
            if ((this.user != null) && deep) {
                this.user.getTokens(deep);
            }
        }
        return this.user;
    }

}
