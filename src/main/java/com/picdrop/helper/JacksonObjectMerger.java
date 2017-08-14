/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.inject.Inject;
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public class JacksonObjectMerger implements ObjectMerger {

    ObjectMapper mapper;

    @Inject
    public JacksonObjectMerger(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> T merge(T defaults, T update) throws IOException {
        // TODO take care of field erasures / How do we actually indicate erasures?
        ObjectReader reader = mapper.readerForUpdating(defaults);
        return reader.readValue(mapper.writeValueAsString(update));
    }

}
