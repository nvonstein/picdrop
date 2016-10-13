/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop;

import com.picdrop.guice.ApplicationModule;
import com.google.inject.Module;
import com.picdrop.guice.RepositoryModule;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContext;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;

/**
 *
 * @author i330120
 */
public class ServletConfig extends GuiceResteasyBootstrapServletContextListener {

    @Override
    protected List<? extends Module> getModules(ServletContext context) {
        return Arrays.asList(new ApplicationModule(), new RepositoryModule());
    }
}
