/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.google.inject.Inject;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.NotSaved;

/**
 *
 * @author nvonstein
 */
public class ShareReference extends Identifiable implements Resolvable<Share> {

    @Inject
    protected static AwareRepository<String, Share, User> repo;

    @NotSaved
    protected Share s;

    public ShareReference(String _id) {
        super(_id);
    }

    public ShareReference(ObjectId _id) {
        super(_id);
    }

    @Override
    public Share resolve(boolean deep) {
        if (this.s == null) {
            this.s = repo.get(this.getId(), null);
            if (deep && (this.s != null)) {
                this.s.getOwner().resolve(true);
                this.s.getResource().resolve(true);
            }
        }
        return this.s;
    }

}
