/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.picdrop.exception.ApplicationException;

/**
 *
 * @author nvonstein
 */
public interface Resolvable<T> {
    public T resolve(boolean deep) throws ApplicationException;
}
