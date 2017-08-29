/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author nvonstein
 */
public interface Resolvable<T> {
    @JsonIgnore
    public T resolve(boolean deep);
}
