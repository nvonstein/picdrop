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
import com.picdrop.model.resource.FileResource;
import com.picdrop.model.user.RegisteredUser;
import com.picdrop.model.user.User;
import com.picdrop.repository.AdvancedRepository;
import com.picdrop.repository.AwareAdvancedRepository;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author nvonstein
 */
public abstract class ServiceTestBase {

    AdvancedRepository<String, TokenSet> tokenSetRepo;
    AdvancedRepository<String, Collection.CollectionItem> collectionItemRepo;
    AdvancedRepository<String, RegisteredUser> registeredUserRepo;

    AwareAdvancedRepository<String, FileResource, User> fileResourceRepo;
    AwareAdvancedRepository<String, Share, User> shareRepo;
    AwareAdvancedRepository<String, Collection, User> collectionRepo;

    @Mock
    RequestContext ctx;
    @Mock
    FileRepository<String> fileHandlingRepo;

    ApplicationModuleMock appModule = new ApplicationModuleMock();
    RepositoryModuleMockNoDB repoModule = new RepositoryModuleMockNoDB();

    Injector inj;

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
    }
}
