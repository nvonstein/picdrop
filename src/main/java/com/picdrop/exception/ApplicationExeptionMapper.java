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
public class ApplicationExeptionMapper extends AbstractExceptionMapper<ApplicationException> {

    public ApplicationExeptionMapper() {
        this.log = LogManager.getLogger();
    }

    
    @Override
    protected ErrorMessage processException(ApplicationException ex) {
        return ex.toErrorMessage(true); // TODO app mode
    }

}
