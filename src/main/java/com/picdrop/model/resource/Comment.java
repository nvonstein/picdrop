/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Entity;

/**
 *
 * @author nvonstein
 */
@Entity("comments")
public class Comment extends InteractionBase {
    
    String comment = "";
    long created;

    public Comment() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonView(value = Views.Public.class)
    public String getComment() {
        return comment;
    }

    @JsonView(value = Views.Public.class)
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonView(value = Views.Public.class)
    public long getCreated() {
        return created;
    }

    @JsonView(value = Views.Ignore.class)
    public void setCreated(long created) {
        this.created = created;
    }
    
}
