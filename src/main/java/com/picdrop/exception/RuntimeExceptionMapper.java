/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import javax.ws.rs.ext.Provider;

/**
 *
 * @author nvonstein
 */
@Provider
public class RuntimeExceptionMapper extends AbstractExceptionMapper<RuntimeException> {

    @Override
    protected ErrorMessage processException(RuntimeException ex) {
        return new ErrorMessage().code(ErrorMessageCode.ERROR_INTERNAL)
                .devMessage(ex.getMessage())
                .status(500)
                .trace(ex);
    }

}
