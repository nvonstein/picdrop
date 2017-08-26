/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.google.inject.Inject;
import com.picdrop.exception.ApplicationException;
import com.picdrop.exception.ErrorMessageCode;
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
    public RegisteredUser resolve(boolean deep) throws ApplicationException {
        if (this.user == null) {
            this.user = repo.get(this.getId());
            if (this.user == null) {
                throw new ApplicationException()
                        .status(404)
                        .code(ErrorMessageCode.NOT_FOUND)
                        .devMessage(String.format("Object with id '%s' not found", this.getId()));
            }
        }
        return this.user;
    }

}
