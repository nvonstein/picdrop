/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Provides;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.picdrop.model.Share;
import com.picdrop.model.TokenSet;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.repository.mongo.MorphiaRepository;
import com.picdrop.repository.mongo.PrincipalAwareMorphiaRepository;
import java.util.Properties;
import javax.enterprise.util.TypeLiteral;
import static org.mockito.Mockito.mock;
import org.mongodb.morphia.Datastore;

/**
 *
 * @author nvonstein
 */
public class RepositoryModuleMockNoDB extends AbstractRepositoryModule {

    protected MorphiaRepository<TokenSet> tsrepo;
    protected MorphiaRepository<Collection.CollectionItem> cirepo;
    protected MorphiaRepository<RegisteredUser> urepo;

    protected PrincipalAwareMorphiaRepository<Collection> crepo;
    protected PrincipalAwareMorphiaRepository<FileResource> rrepo;
    protected PrincipalAwareMorphiaRepository<Share> srepo;

    public RepositoryModuleMockNoDB() {
        this.tsrepo = mock(new TypeLiteral<MorphiaRepository<TokenSet>>() {
        }.getRawType());
        this.cirepo = mock(new TypeLiteral<MorphiaRepository<Collection.CollectionItem>>() {
        }.getRawType());
        this.urepo = mock(new TypeLiteral<MorphiaRepository<RegisteredUser>>() {
        }.getRawType());

        this.crepo = mock(new TypeLiteral<PrincipalAwareMorphiaRepository<Collection>>() {
        }.getRawType());
        this.rrepo = mock(new TypeLiteral<PrincipalAwareMorphiaRepository<FileResource>>() {
        }.getRawType());
        this.srepo = mock(new TypeLiteral<PrincipalAwareMorphiaRepository<Share>>() {
        }.getRawType());

    }

    @Override
    protected MorphiaRepository<RegisteredUser> provideRegisteredUserRepo() {
        return urepo;
    }

    @Override
    protected PrincipalAwareMorphiaRepository<Share> provideShareRepo() {
        return srepo;
    }

    @Override
    protected PrincipalAwareMorphiaRepository<FileResource> provideResourceRepo() {
        return rrepo;
    }

    @Override
    protected PrincipalAwareMorphiaRepository<Collection> provideCollectionRepo() {
        return crepo;
    }

    @Override
    protected MorphiaRepository<Collection.CollectionItem> provideCollectionItemRepo() {
        return cirepo;
    }

    @Override
    protected MorphiaRepository<TokenSet> provideTokenSetRepo() {
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

    public MorphiaRepository<TokenSet> getTsrepo() {
        return tsrepo;
    }

    public void setTsrepo(MorphiaRepository<TokenSet> tsrepo) {
        this.tsrepo = tsrepo;
    }

    public MorphiaRepository<Collection.CollectionItem> getCirepo() {
        return cirepo;
    }

    public void setCirepo(MorphiaRepository<Collection.CollectionItem> cirepo) {
        this.cirepo = cirepo;
    }

    public MorphiaRepository<RegisteredUser> getUrepo() {
        return urepo;
    }

    public void setUrepo(MorphiaRepository<RegisteredUser> urepo) {
        this.urepo = urepo;
    }

    public PrincipalAwareMorphiaRepository<Collection> getCrepo() {
        return crepo;
    }

    public void setCrepo(PrincipalAwareMorphiaRepository<Collection> crepo) {
        this.crepo = crepo;
    }

    public PrincipalAwareMorphiaRepository<FileResource> getRrepo() {
        return rrepo;
    }

    public void setRrepo(PrincipalAwareMorphiaRepository<FileResource> rrepo) {
        this.rrepo = rrepo;
    }

    public PrincipalAwareMorphiaRepository<Share> getSrepo() {
        return srepo;
    }

    public void setSrepo(PrincipalAwareMorphiaRepository<Share> srepo) {
        this.srepo = srepo;
    }

}
