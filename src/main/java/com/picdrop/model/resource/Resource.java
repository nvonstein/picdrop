/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.picdrop.exception.ApplicationException;
import com.picdrop.model.Identifiable;
import com.picdrop.model.Mergeable;
import com.picdrop.model.Referable;
import com.picdrop.model.Share;
import com.picdrop.model.ShareReference;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.RegisteredUserReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Embedded;

/**
 *
 * @author i330120
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type"
)
@JsonSubTypes({
    @Type(value = FileResource.class, name = "file")
    ,
    @Type(value = Collection.class, name = "collection")
})
public abstract class Resource extends Identifiable implements Mergeable<Resource>, Referable<ResourceReference> {

    protected long created;
    protected String name;

    @Embedded
    protected RegisteredUserReference owner;

    protected List<ShareReference> shares = new ArrayList<>();

    public Resource() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public Resource(String _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    public Resource(ObjectId _id) {
        super(_id);
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonProperty
    public long getCreated() {
        return created;
    }

    @JsonIgnore
    public void setCreated(long created) {
        this.created = created;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public RegisteredUserReference getOwner() {
        return owner;
    }

    @JsonIgnore
    public void setOwner(RegisteredUserReference owner) {
        this.owner = owner;
    }

    @JsonIgnore
    public RegisteredUser getOwnerResolved() throws ApplicationException {
        return owner.resolve(false);
    }

    @JsonIgnore
    public void setOwner(RegisteredUser owner) {
        this.owner = owner.refer();
    }

    @JsonProperty
    public List<ShareReference> getShares() {
        return shares;
    }

    @JsonIgnore
    public Resource addShareId(ShareReference id) {
        this.shares.add(id);
        return this;
    }

    @JsonIgnore
    public Resource addShareId(Share share) {
        this.shares.add(share.refer());
        return this;
    }

    @JsonIgnore
    public Resource deleteShareId(ShareReference id) {
        this.shares.remove(id);
        return this;
    }

    @JsonIgnore
    public Resource deleteShareId(Share share) {
        this.shares.remove(share.refer());
        return this;
    }

    @JsonIgnore
    public void setShares(List<ShareReference> shares) {
        this.shares = shares;
    }

    @Override
    public Resource merge(Resource update) throws IOException {
        if (update == null) {
            return this;
        }
        if (update.name != null) {
            this.name = update.name;
        }
        return this;
    }

    @JsonIgnore
    public boolean isCollection() {
        return false;
    }

    @JsonIgnore
    public boolean isFileResource() {
        return false;
    }

}
