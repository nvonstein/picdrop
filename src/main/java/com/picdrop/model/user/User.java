/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Strings;
import com.picdrop.json.Views;
import com.picdrop.model.Identifiable;
import com.picdrop.model.Mergeable;
import com.picdrop.model.Referable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.NotSaved;

/**
 *
 * @author i330120
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonTypeName(value = "user")
@Entity("users")
public abstract class User extends Identifiable implements Mergeable<User>, Referable<UserReference> {

    protected String name;
    protected long created;
    @NotSaved
    List<String> permissions = new ArrayList<>();

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

    @JsonView(value = Views.Public.class)
    public String getName() {
        return name;
    }

    @JsonView(value = Views.Public.class)
    public void setName(String name) {
        this.name = name;
    }

    @JsonView(value = Views.Ignore.class)
    public String getFullName() {
        return getName();
    }

    @JsonView(value = Views.Public.class)
    public long getCreated() {
        return created;
    }

    @JsonView(value = Views.Ignore.class)
    public void setCreated(long created) {
        this.created = created;
    }

    @JsonView(value = Views.Ignore.class)
    public boolean isRegistered() {
        return false;
    }

    @JsonView(value = Views.Detailed.class)
    public List<String> getPermissions() {
        return permissions;
    }

    @JsonView(value = Views.Ignore.class)
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @JsonIgnore
    public User addPermission(String perm) {
        if (!Strings.isNullOrEmpty(perm)) {
           this.permissions.add(perm); 
        }    
        return this;
    }

    @JsonIgnore
    public User removePermission(String perm) {
        this.permissions.remove(perm);
        return this;
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
    public <T extends User> T to(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        if (!type.isInstance(this)) {
            throw new IllegalArgumentException(String.format("cannot cast to type '%s'", type.getName())); // TODO maybe change to IOException
        }
        return type.cast(this);
    }

    @Override
    public User merge(User update) throws IOException {
        if (update.name != null) {
            this.name = update.name;
        }
        return this;
    }
}
