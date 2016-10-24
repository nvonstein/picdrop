/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.factory;

import com.google.inject.name.Named;
import com.picdrop.guice.provider.CookieProvider;

/**
 *
 * @author i330120
 */
public interface CookieProviderFactory {

    @Named("cookie.session")
    CookieProvider getSessionCookieProvider(String value);
}
