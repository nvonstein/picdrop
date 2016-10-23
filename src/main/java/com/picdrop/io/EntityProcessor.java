/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import java.io.IOException;

/**
 *
 * @author i330120
 */
public interface EntityProcessor<T> {

    T process(T entity) throws IOException;
}
