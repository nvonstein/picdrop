/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.google.inject.Inject;
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
    public Collection resolve(boolean deep) {
        if (this.c == null) {
            this.c = repo.get(this.getId());
            if (deep && (this.c != null)) {
                for (Collection.CollectionItemReference ciref : this.c.getItems()) {
                    ciref.resolve(true);
                }
                this.c.getOwner().resolve(true);
            }
        }
        return c;
    }

    @Override
    public String toResourceString() {
        return String.format("/collections/%s", this.getId());
    }
}
