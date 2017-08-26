/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.google.inject.Inject;
import com.picdrop.exception.ApplicationException;
import com.picdrop.exception.ErrorMessageCode;
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
    public Share resolve(boolean deep) throws ApplicationException {
        if (this.s == null) {
            this.s = repo.get(this.getId(), null);
            if (this.s == null) {
                throw new ApplicationException()
                        .status(404)
                        .code(ErrorMessageCode.NOT_FOUND)
                        .devMessage(String.format("Object with id '%s' not found", this.getId()));
            }
            if (deep) {
                this.s.getOwner().resolve(true);
                this.s.getResource().resolve(true);
            }
        }
        return this.s;
    }

}
