/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author nvonstein
 */
@Provider
@Produces("application/json")
public class RuntimeExceptionMapper extends AbstractExceptionMapper<RuntimeException> {

    public RuntimeExceptionMapper() {
        this.log = LogManager.getLogger();
    }

    
    @Override
    protected ErrorMessage processException(RuntimeException ex) {
        ErrorMessage msg = new ErrorMessage()
                .devMessage(ex.getMessage())
                .trace(ex);
        
        return msg
                .code(ErrorMessageCode.ERROR_INTERNAL)
                .status(500)
                .devMessage(String.format("Unhandled runtime exception [%s] with message: %s", ex.getClass().toString(), ex.getMessage()));
    }

}
