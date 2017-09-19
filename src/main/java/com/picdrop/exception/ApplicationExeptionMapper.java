/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import com.google.common.base.Strings;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.Level;
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
    protected void log(ErrorMessage msg, Throwable e) {
        Level lvl = msg.lvl;
        if (lvl == null) {
            lvl = msg.status >= 500 ? Level.ERROR : Level.DEBUG;
        }
        String stringMsg = Strings.isNullOrEmpty(msg.devMessage)
                ? msg.message
                : msg.devMessage;

        if (e.getCause() == null) {
            this.log.log(lvl, stringMsg);
        } else {
            this.log.log(lvl, stringMsg, e.getCause());
        }
    }

    @Override
    protected ErrorMessage processException(ApplicationException ex) {

        return ex.toErrorMessage(true);
    }

}
