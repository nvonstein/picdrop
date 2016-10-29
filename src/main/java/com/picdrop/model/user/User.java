/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.picdrop.model.Identifiable;
import com.picdrop.security.authentication.Role;
import com.picdrop.security.authentication.RoleType;
import java.io.IOException;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author i330120
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonTypeName(value = "user")
@Entity("users")
@Role(roles = RoleType.USER)
public class User extends Identifiable {

    protected String name;
    protected long created;

    public User() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public User(String _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public User(ObjectId _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return getName();
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public boolean isRegistered() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this._id, other._id)) {
            return false;
        }
        return true;
    }
    
    @JsonIgnore
    public <T extends User> T to(Class<T> type) throws IOException {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        if (!type.isInstance(this)) {
            throw new IOException(String.format("cannot cast to type '%s'", type.getName()));
        }
        return type.cast(this);
    }
}
