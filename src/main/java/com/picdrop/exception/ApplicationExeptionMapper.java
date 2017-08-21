/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import java.util.logging.Level;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author nvonstein
 */
@Provider
@Produces("application/json")
public class ApplicationExeptionMapper implements ExceptionMapper<ApplicationException> {

    Logger log = LogManager.getRootLogger();

    @Inject
    ObjectMapper mapper;

    @Override
    public Response toResponse(ApplicationException exception) {
        try {
            if (exception.getCause() != null) {
                log.debug(Strings.isNullOrEmpty(exception.getDevMessage())
                        ? exception.getMessage()
                        : exception.getDevMessage(),
                        exception);
            } else {
                log.debug(Strings.isNullOrEmpty(exception.getDevMessage())
                        ? exception.getMessage()
                        : exception.getDevMessage());
            }
            return Response
                    .status(exception.getStatus())
                    .entity(
                            mapper.writeValueAsString(
                                    exception.toErrorMessage(false))
                    ).build();
        } catch (JsonProcessingException ex) {
            log.debug("Error while processing error message to JSON.", ex);
            return Response.status(exception.getStatus()).entity("{}").build();
        }
    }

}
