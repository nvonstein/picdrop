/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.Produces;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nvonstein
 */
@Provider
public class WebApplicationExceptionHandler extends AbstractExceptionMapper<WebApplicationException> {

    public WebApplicationExceptionHandler() {
        this.log = LogManager.getLogger();
    }

    @Override
    @Produces("application/json")
    protected ErrorMessage processException(WebApplicationException ex) {
        ErrorMessage msg = new ErrorMessage()
                .devMessage(ex.getMessage())
                .trace(ex);

        // Client errors - 4xx
        if (ex instanceof NotFoundException) {
            return msg.level(Level.DEBUG).status(404).code(ErrorMessageCode.NOT_FOUND);
        }

        if (ex instanceof BadRequestException) {
            return msg.level(Level.DEBUG).status(400).code(ErrorMessageCode.BAD_REQUEST);
        }

        if (ex instanceof NotAllowedException) {
            return msg.level(Level.DEBUG).status(405);
        }

        if (ex instanceof NotAcceptableException) {
            return msg.level(Level.DEBUG).status(406);
        }

        if (ex instanceof NotSupportedException) {
            return msg.level(Level.DEBUG).status(415);
        }

        if (ex instanceof ForbiddenException) {
            return msg.level(Level.DEBUG).status(403);
        }

        if (ex instanceof ClientErrorException) {
            return msg.level(Level.WARN).status(400).devMessage(String.format("Unmapped client exception: %s", ex.getMessage()));
        }

        // Server errors - 5xx
        if (ex instanceof InternalServerErrorException) {
            return msg.level(Level.ERROR).status(500).code(ErrorMessageCode.ERROR_INTERNAL);
        }

        if (ex instanceof ServiceUnavailableException) {
            return msg.level(Level.ERROR).status(503);
        }

        if (ex instanceof ServerErrorException) {
            return msg.level(Level.ERROR).status(500).devMessage(String.format("Unmapped server exception: %s", ex.getMessage()));
        }

        return msg
                .code(ErrorMessageCode.ERROR_INTERNAL)
                .status(500)
                .devMessage(String.format("Unhandled web app exception [%s] with message: %s", ex.getClass().toString(), ex.getMessage()));

    }
}
