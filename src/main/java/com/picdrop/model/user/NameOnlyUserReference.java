/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;
import java.util.Objects;

/**
 *
 * @author i330120
 */
public class NameOnlyUserReference {

    String user;
    String name;

    public NameOnlyUserReference() {
    }

    @JsonView(value = Views.Detailed.class)
    public String getUser() {
        return user;
    }

    @JsonView(value = Views.Ignore.class)
    public void setUser(String userId) {
        this.user = userId;
    }
    
    @JsonIgnore
    public void setUser(User user) {
        this.user = user.getId();
        this.name = user.getFullName();
    }

    @JsonView(value = Views.Public.class)
    public String getName() {
        return name;
    }

    @JsonView(value = Views.Ignore.class)
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
        if ((this.user != null) && !this.user.equals(other.getUser())) {
            return false;
        }
        return true;
    }

}
