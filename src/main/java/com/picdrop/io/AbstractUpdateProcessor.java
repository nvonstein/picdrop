/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.model.Identifiable;
import com.picdrop.repository.Repository;
import java.io.IOException;

/**
 *
 * @author i330120
 */
public abstract class AbstractUpdateProcessor<T extends Identifiable> extends AbstractProcessor<T>{
    
    Repository<String, T> repo;

    AbstractUpdateProcessor(Repository<String, T> repo) {
        this.repo = repo;
    }

    @Override
    public T onPostStore(T entity, InputStreamProvider in) throws IOException {
        return repo.update(entity.getId(), entity);
    }
}
