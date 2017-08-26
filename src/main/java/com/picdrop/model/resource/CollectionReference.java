/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CollectionReference extends ResourceReference {

    @Inject
    protected static Repository<String, Collection> repo;

    @NotSaved
    protected Collection c;

    public CollectionReference(String _id) {
        super(_id);
    }

    public CollectionReference(ObjectId _id) {
        super(_id);
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public Collection resolve(boolean deep) throws ApplicationException {
        if (this.c == null) {
            this.c = repo.get(this.getId());
            if (this.c == null) {
                throw new ApplicationException()
                        .status(404)
                        .code(ErrorMessageCode.NOT_FOUND)
                        .devMessage(String.format("Object with id '%s' not found", this.getId()));
            }
            if (deep) {
                for (Collection.CollectionItemReference ciref : this.c.getItems()) {
                    ciref.resolve(true);
                }
                this.c.getOwner().resolve(true);
            }
        }
        return c;
    }

}
