/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import com.picdrop.guice.names.Config;
import com.picdrop.helper.EnvHelper;
import java.util.Properties;

/**
 *
 * @author nvonstein
 */
public class ApplicationModuleMock extends ApplicationModule {
    
    @Override
    protected void bindProperties(Binder binder) {
        EnvHelper ehlp = new EnvHelper("");
        ehlp.setConfig(ehlp.getPropertiesTest());
        
        binder.bind(EnvHelper.class).toInstance(ehlp);
        
        Names.bindProperties(binder, ehlp.getPropertiesTest());
        binder.bind(Properties.class)
                .annotatedWith(Config.class)
                .toInstance(ehlp.getPropertiesTest());
    }
    
    
}
