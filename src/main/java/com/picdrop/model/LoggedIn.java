/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.picdrop.model.user.RegisteredUser;
import java.util.Calendar;
import java.util.Date;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author i330120
 */
@Entity("logins")
public class LoggedIn extends Identifiable {

    @Indexed(options = @IndexOptions(expireAfterSeconds = 3600))
    Date lastActive;
    @Reference
    RegisteredUser user;

    public LoggedIn(String _id, RegisteredUser user) {
        super(_id);
        this.user = user;
        this.lastActive = Calendar.getInstance().getTime(); // TODO ensure UTC
    }

    public LoggedIn(ObjectId _id, RegisteredUser user) {
        super(_id);
        this.user = user;
        this.lastActive = Calendar.getInstance().getTime(); // TODO ensure UTC
    }

    public LoggedIn(RegisteredUser user) {
        this.user = user;
        this.lastActive = Calendar.getInstance().getTime(); // TODO ensure UTC
    }

    public LoggedIn(LoggedIn loggedIn) {
        this(loggedIn._id, loggedIn.user);
        this.lastActive = Calendar.getInstance().getTime(); // TODO ensure UTC
    }

    public Date getLastActive() {
        return lastActive;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public LoggedIn renew() {
        this.lastActive = Calendar.getInstance().getTime(); // TODO ensure UTC
        return this;
    }
}
