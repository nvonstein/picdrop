/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.name.Names;
import com.picdrop.helper.EnvHelper;

/**
 *
 * @author nvonstein
 */
public class ApplicationModuleMock extends ApplicationModule {

    @Override
    protected void bindProperties(Binder binder) {
        Names.bindProperties(binder, EnvHelper.getPropertiesTest());
    }
    
}
