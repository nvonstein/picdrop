/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.picdrop.model.Share;
import com.picdrop.model.TokenSet;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.AdvancedRepository;
import com.picdrop.repository.AwareAdvancedRepository;
import com.picdrop.repository.mongo.MorphiaAdvancedRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaAdvancedRepository;
import javax.enterprise.util.TypeLiteral;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author nvonstein
 */
public class RepositoryModuleMockNoDB extends RepositoryModule {

    protected AdvancedRepository<String, TokenSet> tsrepo;
    protected AdvancedRepository<String, Collection.CollectionItem> cirepo;
    protected AwareAdvancedRepository<String, Collection, User> crepo;
    protected AwareAdvancedRepository<String, FileResource, User> rrepo;
    protected AwareAdvancedRepository<String, Share, User> srepo;
    protected AdvancedRepository<String, RegisteredUser> urepo;

    public RepositoryModuleMockNoDB() {
        this.tsrepo = mock(new TypeLiteral<AdvancedRepository<String, TokenSet>>() {
        }.getRawType());
        this.cirepo = mock(new TypeLiteral<AdvancedRepository<String, Collection.CollectionItem>>() {
        }.getRawType());
        this.crepo = mock(new TypeLiteral<AwareAdvancedRepository<String, Collection, User>>() {
        }.getRawType());
        this.rrepo = mock(new TypeLiteral<AwareAdvancedRepository<String, FileResource, User>>() {
        }.getRawType());
        this.srepo = mock(new TypeLiteral<AwareAdvancedRepository<String, Share, User>>() {
        }.getRawType());
        this.urepo = mock(new TypeLiteral<AdvancedRepository<String, RegisteredUser>>() {
        }.getRawType());
    }

    @Override
    protected Datastore bindDatastore(Binder binder) {
        binder.bind(Datastore.class).toInstance(Mockito.mock(Datastore.class));
        return null;
    }

    @Override
    protected AdvancedRepository<String, RegisteredUser> createRegisteredUserRepo(Datastore ds) {
        return this.urepo;
    }

    @Override
    protected AwareAdvancedRepository<String, Share, User> createShareRepo(Datastore ds) {
        return this.srepo;
    }

    @Override
    protected AwareAdvancedRepository<String, FileResource, User> createResourceRepo(Datastore ds) {
        return this.rrepo;
    }

    @Override
    protected AwareAdvancedRepository<String, Collection, User> createCollectionRepo(Datastore ds) {
        return this.crepo;
    }

    @Override
    protected AdvancedRepository<String, Collection.CollectionItem> createCollectionItemRepo(Datastore ds) {
        return this.cirepo;
    }

    @Override
    protected AdvancedRepository<String, TokenSet> createTokenSetRepo(Datastore ds) {
        return this.tsrepo;
    }

    public AdvancedRepository<String, TokenSet> getTsrepo() {
        return tsrepo;
    }

    public AdvancedRepository<String, Collection.CollectionItem> getCirepo() {
        return cirepo;
    }

    public AwareAdvancedRepository<String, Collection, User> getCrepo() {
        return crepo;
    }

    public AwareAdvancedRepository<String, FileResource, User> getRrepo() {
        return rrepo;
    }

    public AwareAdvancedRepository<String, Share, User> getSrepo() {
        return srepo;
    }

    public AdvancedRepository<String, RegisteredUser> getUrepo() {
        return urepo;
    }

    public void setTsrepo(AdvancedRepository<String, TokenSet> tsrepo) {
        this.tsrepo = tsrepo;
    }

    public void setCirepo(AdvancedRepository<String, Collection.CollectionItem> cirepo) {
        this.cirepo = cirepo;
    }

    public void setCrepo(AwareAdvancedRepository<String, Collection, User> crepo) {
        this.crepo = crepo;
    }

    public void setRrepo(AwareAdvancedRepository<String, FileResource, User> rrepo) {
        this.rrepo = rrepo;
    }

    public void setSrepo(AwareAdvancedRepository<String, Share, User> srepo) {
        this.srepo = srepo;
    }

    public void setUrepo(AdvancedRepository<String, RegisteredUser> urepo) {
        this.urepo = urepo;
    }

}
