/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.InternalServerErrorException;

/**
 *
 * @author nvonstein
 */
@Provider
@Produces("application/json")
public class FailureExceptionMapper extends AbstractExceptionMapper<Failure> {

    public FailureExceptionMapper() {
        this.log = LogManager.getLogger();
    }  
    
    @Override
    protected ErrorMessage processException(Failure ex) {
        ErrorMessage msg = new ErrorMessage()
                .trace(ex)
                .status(ex.getErrorCode())
                .level(Level.DEBUG)
                .devMessage(ex.getMessage());

        if (ex instanceof BadRequestException) {
            return msg.level(Level.DEBUG).code(ErrorMessageCode.BAD_REQUEST);
        }

        if (ex instanceof InternalServerErrorException) {
            return msg.level(Level.ERROR).code(ErrorMessageCode.ERROR_INTERNAL);
        }

        return msg;
    }

}
