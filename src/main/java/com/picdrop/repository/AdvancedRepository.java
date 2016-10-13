/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.repository;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author i330120
 */
public interface AdvancedRepository<ID, T> extends Repository<ID, T> {

    int deleteNamed(String qname, Object... params) throws IOException;

    List<T> updateNamed(T entity, String qname, Object... params) throws IOException;
}
