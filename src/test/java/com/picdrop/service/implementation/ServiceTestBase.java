/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.service.implementation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.picdrop.guice.ApplicationModuleMock;
import com.picdrop.guice.AuthorizationModuleMock;
import com.picdrop.guice.CryptoModule;
import com.picdrop.guice.FileHandlingModuleMock;
import com.picdrop.guice.RepositoryModuleMockNoDB;
import com.picdrop.io.repository.FileRepository;
import com.picdrop.model.RequestContext;
import com.picdrop.model.Share;
import com.picdrop.model.TokenSet;
import com.picdrop.model.resource.Collection;
import com.picdrop.model.resource.Comment;
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.resource.Rating;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.AwareRepository;
import com.picdrop.repository.Repository;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;
import org.junit.Before;
import org.mockito.Mock;

/**
 *
 * @author nvonstein
 */
public abstract class ServiceTestBase {

    protected Repository<String, TokenSet> tokenSetRepo;
    protected Repository<String, Collection.CollectionItem> collectionItemRepo;
    protected Repository<String, RegisteredUser> registeredUserRepo;
    protected Repository<String, Comment> commentRepo;
    protected Repository<String, Rating> ratingRepo;

    protected AwareRepository<String, FileResource, User> fileResourceRepo;
    protected AwareRepository<String, Share, User> shareRepo;
    protected AwareRepository<String, Collection, User> collectionRepo;

    @Mock
    protected RequestContext ctx;
    @Mock
    protected FileRepository<String> fileHandlingRepo;

    protected ApplicationModuleMock appModule = new ApplicationModuleMock();
    protected RepositoryModuleMockNoDB repoModule = new RepositoryModuleMockNoDB();

    protected Injector inj;

    @Before
    public void setUp() {

        inj = Guice.createInjector(appModule,
                new AuthorizationModuleMock(ctx),
                new CryptoModule(),
                repoModule,
                new FileHandlingModuleMock(fileHandlingRepo),
                new RequestScopeModule());

        this.collectionRepo = repoModule.getCrepo();
        this.collectionItemRepo = repoModule.getCirepo();
        this.fileResourceRepo = repoModule.getRrepo();
        this.shareRepo = repoModule.getSrepo();
        this.tokenSetRepo = repoModule.getTsrepo();
        this.registeredUserRepo = repoModule.getUrepo();
        this.commentRepo = repoModule.getComrepo();
        this.ratingRepo = repoModule.getRatrepo();
    }
}
