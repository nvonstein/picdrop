/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.google.inject.Inject;
import com.picdrop.json.Views;
import com.picdrop.model.Identifiable;
import com.picdrop.model.Referable;
import com.picdrop.model.Resolvable;
import com.picdrop.model.user.NameOnlyUserReference;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.RegisteredUserReference;
import com.picdrop.repository.Repository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.NotSaved;

/**
 *
 * @author i330120
 */
@Entity("collections")
public class Collection extends Resource {

    @Embedded
    protected List<CollectionItemReference> items = new ArrayList<>();

    public Collection() {
        super();
    }

    public Collection(String _id) {
        super(_id);
    }

    public Collection(ObjectId _id) {
        super(_id);
    }

    @JsonView(value = Views.Public.class)
    public List<CollectionItemReference> getItems() {
        return items;
    }

    @JsonIgnore
    public List<CollectionItem> getItems(boolean deep) {
        List<CollectionItem> ret = new ArrayList<>();
        this.items.forEach(ciref -> ret.add(ciref.resolve(deep)));
        return ret;
    }

    @JsonView(value = Views.Ignore.class)
    public void setItems(List<CollectionItemReference> items) {
        this.items = items;
    }

    @JsonIgnore
    public Collection addItem(CollectionItemReference item) {
        this.items.add(item);
        return this;
    }

    @JsonIgnore
    public Collection removeItem(CollectionItemReference item) {
        this.items.remove(item);
        return this;
    }

    @JsonIgnore
    public Collection addItem(CollectionItem item) {
        this.items.add(item.refer());
        return this;
    }

    @JsonIgnore
    public Collection removeItem(CollectionItem item) {
        this.items.remove(item.refer());
        return this;
    }

    @Override
    public CollectionReference refer() {
        return new CollectionReference(this.getId());
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public Collection merge(Resource update) throws IOException {
        if (update == null) {
            return this;
        }
        super.merge(update);
        return this;
    }

    @Override
    public String toResourceString() {
        return String.format("/collections/%s", this.getId());
    }

    @Entity("citems")
    public static class CollectionItem extends Identifiable implements Referable<CollectionItemReference> {

        @Embedded
        FileResourceReference resource;
        @Embedded
        CollectionReference parentCollection;
        @Embedded
        List<Comment> comments = new ArrayList<>();
        @Embedded
        List<Rating> ratings = new ArrayList<>();
        @Embedded
        List<NameOnlyUserReference> blockings = new ArrayList<>();
        @Embedded
        RegisteredUserReference owner;

        public CollectionItem() {
        }

        public CollectionItem(String _id) {
            super(_id);
        }

        public CollectionItem(ObjectId _id) {
            super(_id);
        }

        @JsonView(value = Views.Detailed.class)
        public CollectionReference getParentCollection() {
            return parentCollection;
        }

        @JsonView(value = Views.Ignore.class)
        public void setParentCollection(CollectionReference parentCollection) {
            this.parentCollection = parentCollection;
        }

        @JsonIgnore
        public void setParentCollection(Collection parentCollection) {
            this.parentCollection = parentCollection.refer();
        }

        @JsonView(value = Views.Public.class)
        public FileResourceReference getResource() {
            return resource;
        }

        @JsonView(value = Views.Public.class)
        public void setResource(FileResourceReference resource) {
            this.resource = resource;
        }

        @JsonIgnore
        public FileResource getResource(boolean deep) {
            return resource.resolve(deep);
        }

        @JsonIgnore
        public void setResource(FileResource resource) {
            this.resource = resource.refer();
        }

        @JsonView(value = Views.Public.class)
        public List<Rating> getRatings() {
            return ratings;
        }

        @JsonView(value = Views.Ignore.class)
        public void setRatings(List<Rating> ratings) {
            this.ratings = ratings;
        }

        @JsonView(value = Views.Public.class)
        public List<NameOnlyUserReference> getBlockings() {
            return blockings;
        }

        @JsonView(value = Views.Ignore.class)
        public void setBlockings(List<NameOnlyUserReference> blockings) {
            this.blockings = blockings;
        }

        @JsonView(value = Views.Public.class)
        public List<Comment> getComments() {
            return comments;
        }

        @JsonView(value = Views.Ignore.class)
        public void setComments(List<Comment> comments) {
            this.comments = comments;
        }

        @JsonIgnore
        public CollectionItem addRating(Rating r) {
            this.ratings.add(r);
            return this;
        }

        @JsonIgnore
        public CollectionItem addComment(Comment c) {
            this.comments.add(c);
            return this;
        }

        @JsonView(value = Views.Detailed.class)
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

        @Override
        public CollectionItemReference refer() {
            CollectionItemReference ciref = new CollectionItemReference(this.getId());
            ciref.setResource(resource);
            return ciref;
        }

    }

    public static class CollectionItemReference extends Identifiable implements Resolvable<CollectionItem> {

        @Inject
        protected static Repository<String, CollectionItem> repo;

        @NotSaved
        protected CollectionItem ci;

        @NotSaved
        protected FileResourceReference resource;

        public CollectionItemReference() {
        }

        public CollectionItemReference(String _id) {
            super(_id);
        }

        public CollectionItemReference(ObjectId _id) {
            super(_id);
        }

        @Override
        public CollectionItem resolve(boolean deep) {
            if (this.ci == null) {
                this.ci = repo.get(this.getId());
                if (deep && (this.ci != null)) {
                    this.ci.getResource().resolve(true);
                    this.ci.getOwner().resolve(true);
                }
            }
            return ci;
        }

        @JsonView(value = Views.Ignore.class)
        public FileResourceReference getResource() {
            return resource;
        }

        @JsonView(value = Views.Public.class)
        public void setResource(FileResourceReference resource) {
            this.resource = resource;
        }

    }

    public static class Rating extends NameOnlyUserReference {

        int rate = 0;

        public Rating() {
        }

        @JsonView(value = Views.Public.class)
        public int getRate() {
            return rate;
        }

        @JsonView(value = Views.Public.class)
        public void setRate(int rate) {
            this.rate = (rate < 0)
                    ? 0
                    : (rate > 6)
                            ? 6
                            : rate;
        }
    }

    public static class Comment extends NameOnlyUserReference {

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

}
