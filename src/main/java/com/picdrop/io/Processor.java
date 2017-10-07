/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.picdrop.exception.ApplicationException;
import com.picdrop.guice.provider.ResourceContainer;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public interface Processor<T> {

    T onPreStore(T entity, ResourceContainer cnt) throws IOException, ApplicationException;

    T onPostStore(T entity, ResourceContainer cnt) throws IOException, ApplicationException;

    void onPreDelete(T entity) throws IOException, ApplicationException;

    void onPostDelete(T entity) throws IOException, ApplicationException;
}
