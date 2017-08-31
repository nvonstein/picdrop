/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.factory;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.picdrop.guice.provider.CookieProvider;

/**
 *
 * @author i330120
 */
public interface CookieProviderFactory {

    @Named("service.cookie.factory")
    CookieProvider getSessionCookieProvider(@Assisted("name") String name,
            @Assisted("value") String value);
}
