/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;
import com.picdrop.model.Identifiable;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.RegisteredUserReference;
import com.picdrop.model.user.UserReference;

/**
 *
 * @author i330120
 */
public abstract class InteractionBase extends Identifiable {

    RegisteredUserReference user;
    Collection.CollectionItemReference parent;
    String name;

    public InteractionBase() {
    }

    @JsonView(value = Views.Detailed.class)
    public UserReference getUser() {
        return user;
    }

    @JsonView(value = Views.Ignore.class)
    public void setUser(RegisteredUserReference user) {
        this.user = user;
    }

    @JsonIgnore
    public void setUser(RegisteredUser user) {
        this.user = user.refer();
        this.name = user.getFullName();
    }

    @JsonView(value = Views.Public.class)
    public String getName() {
        return name;
    }

    @JsonView(value = Views.Public.class)
    public void setName(String name) {
        this.name = name;
    }

    @JsonView(value = Views.Ignore.class)
    public Collection.CollectionItemReference getParent() {
        return parent;
    }

    @JsonView(value = Views.Ignore.class)
    public void setParent(Collection.CollectionItemReference parent) {
        this.parent = parent;
    }

    @JsonIgnore
    public void setParent(Collection.CollectionItem citem) {
        this.parent = citem.refer();
    }

}
