/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;

/**
 *
 * @author i330120
 */
@Entity("registeredusers")
public class RegisteredUser extends User {

    protected String lastname;
    protected String phash;
    @Indexed
    protected String email;

    protected long lastlogin;

    public RegisteredUser() {
    }

    public RegisteredUser(String _id) {
        super(_id);
    }

    public RegisteredUser(ObjectId _id) {
        super(_id);
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean isRegistered() {
        return true;
    }

    @JsonIgnore
    public String getPhash() {
        return phash;
    }

    @JsonProperty("phash")
    public void setPhash(String phash) {
        this.phash = phash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(long lastlogin) {
        this.lastlogin = lastlogin;
    }

    public void setLastLogin() {
        this.lastlogin = DateTime.now(DateTimeZone.UTC).getMillis();
    }
}
