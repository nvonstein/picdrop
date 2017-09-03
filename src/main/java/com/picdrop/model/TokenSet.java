/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.RegisteredUserReference;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;

/**
 *
 * @author nvonstein
 */
@Entity("tokens")
@Indexes(
        {
            @Index(fields = {
        @Field("authJti")
        ,@Field("owner")})
            ,
        @Index(fields = {
        @Field("refreshJti")
        ,@Field("owner")})})
public class TokenSet extends Identifiable implements Referable<TokenSetReference> {

    protected String authJti;
    protected String refreshJti;
    @Indexed(options = @IndexOptions(expireAfterSeconds = 0))
    protected Date expireAt;
    protected long created;
    protected String name;
    protected RegisteredUserReference owner;

    public TokenSet() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonView(Views.Detailed.class)
    public String getAuthJti() {
        return authJti;
    }

    @JsonView(Views.Ignore.class)
    public void setAuthJti(String authId) {
        this.authJti = authId;
    }

    @JsonView(Views.Detailed.class)
    public String getRefreshJti() {
        return refreshJti;
    }

    @JsonView(Views.Ignore.class)
    public void setRefreshJti(String refreshId) {
        this.refreshJti = refreshId;
    }

    @JsonView(Views.Detailed.class)
    public Date getExpireAt() {
        return expireAt;
    }

    @JsonView(Views.Ignore.class)
    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    @JsonView(Views.Public.class)
    public long getCreated() {
        return created;
    }

    @JsonView(Views.Ignore.class)
    public void setCreated(long created) {
        this.created = created;
    }

    @JsonIgnore
    public void setCreated() {
        this.created = DateTime.now(DateTimeZone.UTC).getMillis();
    }

    @JsonView(Views.Public.class)
    public String getName() {
        return name;
    }

    @JsonView(Views.Ignore.class)
    public void setName(String name) {
        this.name = name;
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
    public TokenSetReference refer() {
        return new TokenSetReference(this._id);
    }

    @JsonIgnore
    public JsonWrapper toJsonWrapper() {
        return new JsonWrapper()
                .auth(this.authJti)
                .refresh(this.refreshJti);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class JsonWrapper {

        @JsonView(Views.Public.class)
        protected String auth;
        @JsonView(Views.Public.class)
        protected String refresh;
        @JsonView(Views.Public.class)
        protected String nonce;

        public String getAuth() {
            return auth;
        }

        public String getRefresh() {
            return refresh;
        }

        public String getNonce() {
            return nonce;
        }

        public JsonWrapper auth(String in) {
            this.auth = in;
            return this;
        }

        public JsonWrapper refresh(String in) {
            this.refresh = in;
            return this;
        }

        public JsonWrapper nonce(String in) {
            this.nonce = in;
            return this;
        }
    }
}
