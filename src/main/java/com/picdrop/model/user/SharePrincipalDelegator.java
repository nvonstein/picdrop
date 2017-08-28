/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.picdrop.model.Share;
import com.picdrop.security.authentication.Role;
import com.picdrop.security.authentication.RoleType;

/**
 *
 * @author nvonstein
 */
@Role(roles = RoleType.SHARE)
public class SharePrincipalDelegator extends User {

    User u;

    public SharePrincipalDelegator(User u) {
        this.u = u;
    }

    @Override
    public UserReference refer() {
        return this.u.refer();
    }

    @Override
    public String getName() {
        return u.getName();
    }

    @Override
    public String getFullName() {
        return u.getFullName();
    }

    @Override
    public long getCreated() {
        return u.getCreated();
    }

    @Override
    public boolean isRegistered() {
        return u.isRegistered();
    }

    @Override
    public String getId() {
        return u.getId();
    }
}
