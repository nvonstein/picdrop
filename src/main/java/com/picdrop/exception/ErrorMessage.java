/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.Level;

/**
 *
 * @author nvonstein
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class ErrorMessage {

    @JsonIgnore
    protected int status = 0;
    @JsonIgnore
    protected Level lvl;
    protected String code;
    protected String lang = "en";
    protected String message;
    protected String devMessage;
    protected String trace;

    public ErrorMessage level(Level lvl) {
        this.lvl = lvl;
        return this;
    }
    
    public ErrorMessage status(int status) {
        this.status = status;
        return this;
    }

    public ErrorMessage code(String code) {
        this.code = code;
        return this;
    }

    public ErrorMessage code(ErrorMessageCode code) {
        this.code = code.getCode();
        this.message = code.getDefaultMessage();
        return this;
    }

    public ErrorMessage lang(String lang) {
        this.lang = lang;
        return this;
    }

    public ErrorMessage message(String message) {
        this.message = message;
        return this;
    }

    public ErrorMessage devMessage(String devMessage) {
        this.devMessage = devMessage;
        return this;
    }

    public ErrorMessage trace(String trace) {
        this.trace = trace;
        return this;
    }

    public ErrorMessage trace(Throwable ex) {
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        this.trace = sw.toString();
        return this;
    }

    public ErrorMessage maskFields(boolean debug) {
        if (!debug) {
            return new ErrorMessage().code(code)
                    .lang(lang)
                    .message(message);
        }
        return this;
    }
}
