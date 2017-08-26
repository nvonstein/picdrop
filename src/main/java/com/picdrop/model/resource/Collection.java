/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.picdrop.model.user.NameOnlyUserReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.picdrop.model.Identifiable;
import com.picdrop.model.Referable;
import com.picdrop.model.Resolvable;
import com.picdrop.repository.Repository;
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

    @JsonProperty
    public List<CollectionItemReference> getItems() {
        return items;
    }

    @JsonIgnore
    public List<CollectionItem> getItems(boolean deep) {
        List<CollectionItem> ret = new ArrayList<>();
        this.items.forEach(ciref -> ret.add(ciref.resolve(deep)));
        return ret;
    }

    @JsonIgnore
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

    @JsonIgnore
    @Override
    public CollectionReference refer() {
        return new CollectionReference(this.getId());
    }

    @Override
    public boolean isCollection() {
        return true;
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

        public CollectionItem() {
        }

        public CollectionItem(String _id) {
            super(_id);
        }

        public CollectionItem(ObjectId _id) {
            super(_id);
        }

        @JsonIgnore
        public CollectionReference getParentCollection() {
            return parentCollection;
        }

        @JsonIgnore
        public void setParentCollection(CollectionReference parentCollection) {
            this.parentCollection = parentCollection;
        }

        @JsonIgnore
        public void setParentCollection(Collection parentCollection) {
            this.parentCollection = parentCollection.refer();
        }

        @JsonProperty
        public FileResourceReference getResource() {
            return resource;
        }

        @JsonProperty
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

        @JsonProperty
        public List<Rating> getRatings() {
            return ratings;
        }

        @JsonIgnore
        public void setRatings(List<Rating> ratings) {
            this.ratings = ratings;
        }

        @JsonProperty
        public List<NameOnlyUserReference> getBlockings() {
            return blockings;
        }

        @JsonIgnore
        public void setBlockings(List<NameOnlyUserReference> blockings) {
            this.blockings = blockings;
        }

        @JsonProperty
        public List<Comment> getComments() {
            return comments;
        }

        @JsonIgnore
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

        @Override
        public CollectionItemReference refer() {
            return new CollectionItemReference(this.getId());
        }
    }

    public static class CollectionItemReference extends Identifiable implements Resolvable<CollectionItem> {

        @Inject
        protected static Repository<String, CollectionItem> repo;

        @NotSaved
        protected CollectionItem ci;

        @NotSaved
        protected FileResourceReference resource;

        public CollectionItemReference(String _id) {
            super(_id);
        }

        public CollectionItemReference(ObjectId _id) {
            super(_id);
        }

        @Override
        @JsonIgnore
        public CollectionItem resolve(boolean deep) {
            if (this.ci == null) {
                this.ci = repo.get(this.getId());
                if (deep && (this.ci != null)) {
                    this.ci.getResource().resolve(true);
                }
            }
            return ci;
        }

        @JsonIgnore
        public FileResourceReference getResource() {
            return resource;
        }

        @JsonProperty
        public void setResource(FileResourceReference resource) {
            this.resource = resource;
        }

    }

    public static class Rating extends NameOnlyUserReference {

        int rate = 0;

        public Rating() {
        }

        public int getRate() {
            return rate;
        }

        public void setRate(int rate) {
            this.rate = (rate < 0) ? 0 : rate % 6;
        }
    }

    public static class Comment extends NameOnlyUserReference {

        String comment = "";
        long created;

        public Comment() {
            this.created = DateTime.now(DateTimeZone.UTC).getMillis();
        }

        @JsonProperty
        public String getComment() {
            return comment;
        }

        @JsonProperty
        public void setComment(String comment) {
            this.comment = comment;
        }

        @JsonProperty
        public long getCreated() {
            return created;
        }

        @JsonIgnore
        public void setCreated(long created) {
            this.created = created;
        }
    }

}
