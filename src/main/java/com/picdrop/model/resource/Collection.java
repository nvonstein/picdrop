/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.resource;

import com.picdrop.model.user.NameOnlyUserReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.picdrop.model.Identifiable;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author i330120
 */
@Entity("collections")
public class Collection extends Resource {

    @Reference
    protected List<CollectionItem> items;

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
    public List<CollectionItem> getResources() {
        return items;
    }

    @JsonIgnore
    public void setResources(List<CollectionItem> resources) {
        this.items = resources;
    }

    @JsonIgnore
    public Collection addResource(CollectionItem resource) {
        this.items.add(resource);
        return this;
    }

    @JsonIgnore
    public Collection removeResource(CollectionItem resource) {
        this.items.remove(resource);
        return this;
    }

    @Entity("citems")
    public static class CollectionItem extends Identifiable {

        @Reference
        FileResource resource;
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

        @JsonProperty
        public FileResource getResource() {
            return resource;
        }

        @JsonProperty
        public void setResource(FileResource resource) {
            this.resource = resource;
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

    @Override
    public boolean isCollection() {
        return true;}
    
}
