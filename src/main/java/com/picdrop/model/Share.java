/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.base.Strings;
import com.picdrop.json.Views;
import com.picdrop.model.resource.Resource;
import com.picdrop.model.resource.ResourceReference;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.RegisteredUserReference;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;

/**
 *
 * @author i330120
 */
@Entity("shares")
public class Share extends Identifiable implements Mergeable<Share>, Referable<ShareReference> {

    protected long created;

    @Indexed
    protected String uri;

    @Embedded
    protected ResourceReference resource;

    @Embedded
    protected RegisteredUserReference owner;

    protected boolean allowComment = false;
    protected boolean allowRating = false;

    public Share() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public Share(String _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public Share(ObjectId _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonView(value = Views.Public.class)
    public long getCreated() {
        return created;
    }

    @JsonView(value = Views.Public.class)
    public void setCreated(long created) {
        this.created = created;
    }

    @JsonView(value = Views.Public.class)
    public String getUri() {
        return uri;
    }

    @JsonView(value = Views.Public.class)
    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonView(value = Views.Public.class)
    public ResourceReference getResource() {
        return resource;
    }

    @JsonView(value = Views.Public.class)
    public void setResource(ResourceReference resource) {
        this.resource = resource;
    }

    @JsonIgnore
    public Resource getResource(boolean deep) {
        return resource.resolve(deep);
    }

    @JsonIgnore
    public void setResource(Resource resource) {
        this.resource = resource.refer();
    }

    @JsonView(value = Views.Public.class)
    public RegisteredUserReference getOwner() {
        return owner;
    }

    @JsonView(value = Views.Ignore.class)
    public void setOwner(RegisteredUserReference owner) {
        this.owner = owner;
    }

    @JsonIgnore
    public RegisteredUser getOwner(boolean deep) {
        return owner.resolve(deep);
    }

    @JsonIgnore
    public void setOwner(RegisteredUser owner) {
        this.owner = owner.refer();
    }

    @JsonView(value = Views.Public.class)
    public boolean isAllowComment() {
        return allowComment;
    }

    @JsonView(value = Views.Public.class)
    public void setAllowComment(boolean allowComment) {
        this.allowComment = allowComment;
    }

    @JsonView(value = Views.Public.class)
    public boolean isAllowRating() {
        return allowRating;
    }

    @JsonView(value = Views.Public.class)
    public void setAllowRating(boolean allowRating) {
        this.allowRating = allowRating;
    }

    @Override
    public Share merge(Share update) throws IOException {
        if (this.allowComment != update.allowComment) {
            this.allowComment = update.allowComment;
        }
        if (this.allowRating != update.allowRating) {
            this.allowRating = update.allowRating;
        }
        if (!Strings.isNullOrEmpty(update.uri)) {
            this.uri = update.uri;
        }
        return this;
    }

    @Override
    public ShareReference refer() {
        return new ShareReference(this.getId());
    }

}
