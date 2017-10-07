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
import java.io.IOException;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
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

    protected long sizeLimit;
    protected long sizeUsage;

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

    @JsonView(value = Views.Public.class)
    public long getSizeLimit() {
        return sizeLimit;
    }

    @JsonView(value = Views.Ignore.class)
    public void setSizeLimit(long sizeLimit) {
        this.sizeLimit = sizeLimit;
    }

    @JsonView(value = Views.Public.class)
    public long getSizeUsage() {
        return sizeUsage;
    }

    @JsonView(value = Views.Ignore.class)
    public void setSizeUsage(long sizeUsage) {
        this.sizeUsage = sizeUsage;
    }
    
    public void incSizeUsage(long sizeUsage) {
        this.sizeUsage = this.sizeUsage + sizeUsage;
    }

    @Override
    public String getFullName() {
        String sep = Strings.isNullOrEmpty(this.lastname) ? "" : " ";
        return String.format("%s%s%s", Strings.nullToEmpty(this.name), sep, Strings.nullToEmpty(this.lastname));
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
        }
        return this;
    }

    @Override
    public RegisteredUserReference refer() {
        return new RegisteredUserReference(this);
    }

}
