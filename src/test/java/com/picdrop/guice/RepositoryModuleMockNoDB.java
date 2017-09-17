/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Provides;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
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
import java.util.Properties;
import javax.enterprise.util.TypeLiteral;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author nvonstein
 */
public class RepositoryModuleMockNoDB extends AbstractRepositoryModule {

    protected MorphiaAdvancedRepository<TokenSet> tsrepo;
    protected MorphiaAdvancedRepository<Collection.CollectionItem> cirepo;
    protected MorphiaAdvancedRepository<RegisteredUser> urepo;

    protected PrincipalAwareMorphiaAdvancedRepository<Collection> crepo;
    protected PrincipalAwareMorphiaAdvancedRepository<FileResource> rrepo;
    protected PrincipalAwareMorphiaAdvancedRepository<Share> srepo;

    public RepositoryModuleMockNoDB() {
        this.tsrepo = mock(new TypeLiteral<MorphiaAdvancedRepository<TokenSet>>() {
        }.getRawType());
        this.cirepo = mock(new TypeLiteral<MorphiaAdvancedRepository<Collection.CollectionItem>>() {
        }.getRawType());
        this.urepo = mock(new TypeLiteral<MorphiaAdvancedRepository<RegisteredUser>>() {
        }.getRawType());

        this.crepo = mock(new TypeLiteral<PrincipalAwareMorphiaAdvancedRepository<Collection>>() {
        }.getRawType());
        this.rrepo = mock(new TypeLiteral<PrincipalAwareMorphiaAdvancedRepository<FileResource>>() {
        }.getRawType());
        this.srepo = mock(new TypeLiteral<PrincipalAwareMorphiaAdvancedRepository<Share>>() {
        }.getRawType());

    }

    @Provides
    @Override
    protected MorphiaAdvancedRepository<RegisteredUser> provideRegisteredUserRepo(Datastore ds) {
        return urepo;
    }

    @Provides
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<Share> provideShareRepo(Datastore ds) {
        return srepo;
    }

    @Provides
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<FileResource> provideResourceRepo(Datastore ds) {
        return rrepo;
    }

    @Provides
    @Override
    protected PrincipalAwareMorphiaAdvancedRepository<Collection> provideCollectionRepo(Datastore ds) {
        return crepo;
    }

    @Provides
    @Override
    protected MorphiaAdvancedRepository<Collection.CollectionItem> provideCollectionItemRepo(Datastore ds) {
        return cirepo;
    }

    @Provides
    @Override
    protected MorphiaAdvancedRepository<TokenSet> provideTokenSetRepo(Datastore ds) {
        return tsrepo;
    }

    @Provides
    @Override
    protected MongoClient provideMongoClient(Properties config) {
        return mock(MongoClient.class);
    }

    @Provides
    @Override
    protected Datastore provideDatastore(MongoClient client) {
        return mock(Datastore.class);
    }

    @Provides
    @Override
    protected MongoDatabase provideDatabase(MongoClient client) {
        return mock(MongoDatabase.class);
    }

    public MorphiaAdvancedRepository<TokenSet> getTsrepo() {
        return tsrepo;
    }

    public void setTsrepo(MorphiaAdvancedRepository<TokenSet> tsrepo) {
        this.tsrepo = tsrepo;
    }

    public MorphiaAdvancedRepository<Collection.CollectionItem> getCirepo() {
        return cirepo;
    }

    public void setCirepo(MorphiaAdvancedRepository<Collection.CollectionItem> cirepo) {
        this.cirepo = cirepo;
    }

    public MorphiaAdvancedRepository<RegisteredUser> getUrepo() {
        return urepo;
    }

    public void setUrepo(MorphiaAdvancedRepository<RegisteredUser> urepo) {
        this.urepo = urepo;
    }

    public PrincipalAwareMorphiaAdvancedRepository<Collection> getCrepo() {
        return crepo;
    }

    public void setCrepo(PrincipalAwareMorphiaAdvancedRepository<Collection> crepo) {
        this.crepo = crepo;
    }

    public PrincipalAwareMorphiaAdvancedRepository<FileResource> getRrepo() {
        return rrepo;
    }

    public void setRrepo(PrincipalAwareMorphiaAdvancedRepository<FileResource> rrepo) {
        this.rrepo = rrepo;
    }

    public PrincipalAwareMorphiaAdvancedRepository<Share> getSrepo() {
        return srepo;
    }

    public void setSrepo(PrincipalAwareMorphiaAdvancedRepository<Share> srepo) {
        this.srepo = srepo;
    }

}
