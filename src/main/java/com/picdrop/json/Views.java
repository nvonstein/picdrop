/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.json;

/**
 *
 * @author nvonstein
 */
public abstract class Views {

    public static abstract class Public {
    };

    public static abstract class Detailed extends Public {
    };

    public static abstract class Ignore extends Detailed {
    };
}
