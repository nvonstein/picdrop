/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop;

import com.google.inject.Module;
import com.picdrop.guice.ApplicationModule;
import com.picdrop.guice.AuthorizationModule;
import com.picdrop.guice.CryptoModule;
import com.picdrop.guice.FileHandlingModule;
import com.picdrop.guice.RepositoryModule;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContext;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;

/**
 *
 * @author i330120
 */
public class ServletConfig extends GuiceResteasyBootstrapServletContextListener {

    @Override
    protected List<? extends Module> getModules(ServletContext context) {
        List<Module> l = Arrays.asList(
                new ApplicationModule(),
                new CryptoModule(),
                new AuthorizationModule(),
                new FileHandlingModule(),
                new RepositoryModule(),
                new RequestScopeModule());

        return l;
    }
}
