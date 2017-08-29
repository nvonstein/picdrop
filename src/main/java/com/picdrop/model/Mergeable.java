/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public interface Mergeable<T> {
    @JsonView(value = Views.Internal.class)
    public T merge(T update) throws IOException;   
}
