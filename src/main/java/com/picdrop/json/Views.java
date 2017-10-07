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

    public static interface Public {
    };

    public static interface Detailed extends Public {
    };

    public static interface Ignore extends Detailed {
    };
}
