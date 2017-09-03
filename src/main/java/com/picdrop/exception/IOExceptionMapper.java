/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.Level;

/**
 *
 * @author nvonstein
 */
@Provider
public class IOExceptionMapper extends AbstractExceptionMapper<IOException> {

    @Override
    protected ErrorMessage processException(IOException ex) {
        ErrorMessage msg = new ErrorMessage()
                .trace(ex);

        if (ex instanceof JsonParseException) {
            JsonParseException exx = (JsonParseException) ex;
            return msg.status(400).code(ErrorMessageCode.BAD_JSON).devMessage(exx.getMessage());
        }

        if (ex instanceof JsonMappingException) {
            JsonMappingException exx = (JsonMappingException) ex;
            return msg.level(Level.FATAL).status(500).code(ErrorMessageCode.ERROR_INTERNAL).devMessage(exx.getMessage());
        }

        return msg
                .code(ErrorMessageCode.ERROR_INTERNAL)
                .status(500)
                .devMessage("General or unmapped IOExeption occured: " + ex.getMessage());
    }

}
