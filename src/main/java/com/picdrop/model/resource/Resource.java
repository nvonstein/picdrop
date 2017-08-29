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
import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.exception.ApplicationException;
import com.picdrop.json.Views;
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

    @Embedded
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

    @JsonView(value = Views.Public.class)
    public long getCreated() {
        return created;
    }

    @JsonView(value = Views.Ignore.class)
    public void setCreated(long created) {
        this.created = created;
    }

    @JsonView(value = Views.Public.class)
    public String getName() {
        return name;
    }

    @JsonView(value = Views.Public.class)
    public void setName(String name) {
        this.name = name;
    }

    @JsonView(value = Views.Public.class)
    public RegisteredUserReference getOwner() {
        return owner;
    }

    @JsonView(value = Views.Ignore.class)
    public void setOwner(RegisteredUserReference owner) {
        this.owner = owner;
    }

    @JsonView(value = Views.Internal.class)
    public RegisteredUser getOwner(boolean deep) {
        return owner.resolve(deep);
    }

    @JsonView(value = Views.Internal.class)
    public void setOwner(RegisteredUser owner) {
        this.owner = owner.refer();
    }

    @JsonView(value = Views.Detailed.class)
    public List<ShareReference> getShares() {
        return shares;
    }

    @JsonView(value = Views.Internal.class)
    public Resource addShare(ShareReference share) {
        this.shares.add(share);
        return this;
    }

    @JsonView(value = Views.Internal.class)
    public Resource addShare(Share share) {
        this.shares.add(share.refer());
        return this;
    }

    @JsonView(value = Views.Internal.class)
    public Resource deleteShare(ShareReference share) {
        this.shares.remove(share);
        return this;
    }

    @JsonView(value = Views.Internal.class)
    public Resource deleteShare(Share share) {
        this.shares.remove(share.refer());
        return this;
    }

    @JsonView(value = Views.Ignore.class)
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

    @JsonView(value = Views.Ignore.class)
    public boolean isCollection() {
        return false;
    }

    @JsonView(value = Views.Ignore.class)
    public boolean isFileResource() {
        return false;
    }
    
    @JsonView(value = Views.Ignore.class)
    public abstract String toResourceString();

}
