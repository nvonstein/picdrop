/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {

    Logger log = LogManager.getLogger(this.getClass());

    @Inject
    protected static ObjectWriter writer;

    protected void log(ErrorMessage msg, Throwable e) {
        Level lvl = msg.lvl;
        if (lvl == null) {
            lvl = msg.status >= 500 ? Level.ERROR : Level.DEBUG;
        }
        this.log.log(lvl, Strings.isNullOrEmpty(msg.devMessage)
                ? msg.message
                : msg.devMessage, e);
    }

    protected abstract ErrorMessage processException(T ex);

    @Override
    public Response toResponse(T exception) {
        ErrorMessage msg = processException(exception);

        log(msg, exception);

        try {
            return Response.status(msg.status)
                    .entity(writer.writeValueAsString(msg.maskFields(true)))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (JsonProcessingException ex) {
            log.error("Error while processing error message to JSON.", ex);
            return Response.status(msg.status).entity("{}").build();
        }
    }

}
