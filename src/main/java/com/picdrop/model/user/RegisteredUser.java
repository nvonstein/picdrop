/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Strings;
import com.picdrop.json.Views;
import com.picdrop.model.TokenSet;
import com.picdrop.model.TokenSetReference;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.NotSaved;

/**
 *
 * @author i330120
 */
@Entity("users")
public class RegisteredUser extends User {

    protected String lastname;
    protected String phash;
    @Indexed
    protected String email;

    protected long lastlogin;

    @NotSaved
    protected TokenSet activeToken;

    public RegisteredUser() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public RegisteredUser(String _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public RegisteredUser(ObjectId _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonView(value = Views.Public.class)
    public String getLastname() {
        return lastname;
    }

    @JsonView(value = Views.Public.class)
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean isRegistered() {
        return true;
    }

    @JsonView(value = Views.Ignore.class)
    public String getPhash() {
        return phash;
    }

    @JsonView(value = Views.Public.class)
    public void setPhash(String phash) {
        this.phash = phash;
    }

    @JsonView(value = Views.Public.class)
    public String getEmail() {
        return email;
    }

    @JsonView(value = Views.Public.class)
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getFullName() {
        return this.name + " " + this.lastname;
    }

    @JsonView(value = Views.Public.class)
    public long getLastlogin() {
        return lastlogin;
    }

    @JsonView(value = Views.Ignore.class)
    public void setLastlogin(long lastlogin) {
        this.lastlogin = lastlogin;
    }

    @JsonIgnore
    public void setLastLogin() {
        this.lastlogin = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonView(value = Views.Detailed.class)
    public TokenSet getActiveToken() {
        return activeToken;
    }

    @JsonIgnore
    public void setActiveToken(TokenSet activeToken) {
        this.activeToken = activeToken;
    }

    @Override
    public RegisteredUser merge(User update) throws IOException {
        super.merge(update);
        if (update instanceof RegisteredUser) {
            RegisteredUser nup = (RegisteredUser) update;
            if (nup.lastname != null) {
                this.lastname = nup.lastname;
            }
            if (!Strings.isNullOrEmpty(nup.email)) {
                this.email = nup.email;
            }
            if (!Strings.isNullOrEmpty(nup.phash)) {
                this.phash = nup.phash;
            }
        }
        return this;
    }

    @Override
    public RegisteredUserReference refer() {
        return new RegisteredUserReference(this);
    }

}
