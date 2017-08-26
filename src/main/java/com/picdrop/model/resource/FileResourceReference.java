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
public class FileResourceReference extends ResourceReference {

    @Inject
    protected static Repository<String, FileResource> repo;

    @NotSaved
    FileResource fr;

    public FileResourceReference(String _id) {
        super(_id);
    }

    public FileResourceReference(ObjectId _id) {
        super(_id);
    }

    @Override
    public boolean isFileResource() {
        return true;
    }

    @Override
    public FileResource resolve(boolean deep) {
        if (this.fr == null) {
            this.fr = repo.get(this.getId());
            if (deep && (this.fr != null)) {
                this.fr.getOwner().resolve(true);
            }
        }
        return this.fr;
    }

}
