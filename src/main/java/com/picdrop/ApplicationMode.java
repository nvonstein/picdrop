/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop;

/**
 *
 * @author nvonstein
 */
public enum ApplicationMode {

    PROD("prod"),
    DEBUG("debug");

    private final String name;

    private ApplicationMode(String name) {
        this.name = name;
    }

    public static ApplicationMode forName(String name) {
        if ((name == null) || name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }
        for (ApplicationMode appmode : ApplicationMode.values()) {
            if (appmode.name.equals(name.trim().toLowerCase())) {
                return appmode;
            }
        }
        throw new IllegalArgumentException("unknown mode");
    }

    public boolean isDebug() {
        return this == DEBUG;
    }

    public boolean isProd() {
        return this == PROD;
    }
}
