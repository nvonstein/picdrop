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
 * @author nvonstein
 */
public interface AwareAdvancedRepository<ID, T, K> extends AdvancedRepository<ID, T>, AwareRepository<ID, T, K> {

    int deleteNamed(String qname, K context, Object... params) throws IOException;

    List<T> updateNamed(T entity, String qname, K context, Object... params) throws IOException;
}
