/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

/**
 *
 * @author nvonstein
 */
public class RegisteredUserDelegate extends User {

    User u;

    public RegisteredUserDelegate(User u) {
        this.u = u;
    }

    @Override
    public UserReference refer() {
        return this.u.refer();
    }

    @Override
    public long getCreated() {
        return u.getCreated();
    }

    @Override
    public boolean isRegistered() {
        return false;
    }

    @Override
    public String getId() {
        return u.getId();
    }
}
