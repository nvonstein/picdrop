/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model.merger;

import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public interface Mergeable<T> {
    public T merge(T update) throws IOException;   
}
