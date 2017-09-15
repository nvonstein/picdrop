/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io.repository;

import com.picdrop.guice.provider.ResourceContainer;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author i330120
 */
public interface FileRepository<T> {
    T write(T entity, ResourceContainer cnt) throws IOException;
    InputStream read(T entity) throws IOException;
    boolean delete(T entity) throws IOException;
    void init(boolean generate) throws IOException;
}
