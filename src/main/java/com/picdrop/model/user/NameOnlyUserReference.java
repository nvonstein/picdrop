/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.picdrop.model.user.User;
import java.util.Objects;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author i330120
 */
public class NameOnlyUserReference {

    String userId;
    String name;

    public NameOnlyUserReference() {
    }

    @JsonIgnore
    public String getUser() {
        return userId;
    }

    @JsonIgnore
    public void setUser(String userId) {
        this.userId = userId;
    }
    
    @JsonIgnore
    public void setUser(User user) {
        this.userId = user.getId();
        this.name = user.getFullName();
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isInstance(obj)) {
            return false;
        }
        final NameOnlyUserReference other = (NameOnlyUserReference) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if ((this.userId != null) && !this.userId.equals(other.getUser())) {
            return false;
        }
        return true;
    }

}
