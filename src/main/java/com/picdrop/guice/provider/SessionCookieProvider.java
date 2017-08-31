/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice.provider;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;
import javax.ws.rs.core.NewCookie;

/**
 *
 * @author i330120
 */
public class SessionCookieProvider implements CookieProvider {

    String name;
    int ttl;
    boolean http;
    boolean secure;
    String domain;

    String value;

    @AssistedInject
    public SessionCookieProvider(
            @Named("service.cookie.maxage") int ttl,
            @Named("service.cookie.http") boolean http,
            @Named("service.cookie.secure") boolean secure,
            @Named("service.cookie.domain") String domain,
            @Assisted("name") String name,
            @Assisted("value") String value) {
        this.name = name;
        this.http = http;
        this.ttl = ttl;
        this.secure = secure;
        this.value = value;
        this.domain = domain;
    }

    @Override
    public NewCookie get() {
        NewCookie c = new NewCookie(name, value, "/*", this.domain, "", ttl, secure);
        return c;
    }

}
