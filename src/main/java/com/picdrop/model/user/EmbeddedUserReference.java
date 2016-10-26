/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import org.mongodb.morphia.annotations.Embedded;

/**
 *
 * @author i330120
 */
public class EmbeddedUserReference implements UserReference {

    @Embedded
    User reference;

    public EmbeddedUserReference(User user) {
        this.reference = user;
    }

    @Override
    public User get() {
        return reference;
    }

}
