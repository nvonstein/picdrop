/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.name.Named;

/**
 *
 * @author i330120
 */
public interface CookieProviderFactory {

    @Named("cookie.session")
    CookieProvider getSessionCookieProvider(String value);
}
