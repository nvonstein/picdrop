/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.google.inject.Inject;
import static com.picdrop.model.ShareReference.repo;
import com.picdrop.repository.Repository;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.NotSaved;

/**
 *
 * @author nvonstein
 */
public class TokenSetReference extends Identifiable implements Resolvable<TokenSet> {

    @Inject
    protected static Repository<String, TokenSet> repo;

    @NotSaved
    protected TokenSet ts;

    public TokenSetReference() {
    }

    public TokenSetReference(String _id) {
        super(_id);
    }

    public TokenSetReference(ObjectId _id) {
        super(_id);
    }

    @Override
    public TokenSet resolve(boolean deep) {
        if (this.ts == null) {
            this.ts = repo.get(this.getId());
        }
        return this.ts;
    }

}
