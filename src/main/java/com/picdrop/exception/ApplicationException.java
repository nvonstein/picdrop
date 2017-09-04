/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author nvonstein
 */
public class ApplicationException extends Exception {

    protected int status;
    protected ErrorMessageCode code;
    protected String lang = "EN";
    protected String message;
    protected String devMessage;

    public ApplicationException() {
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException status(int status) {
        this.status = status;
        return this;
    }

    public ApplicationException code(ErrorMessageCode code) {
        this.code = code;
        this.message = code.getDefaultMessage();
        return this;
    }

    public ApplicationException lang(String lang) {
        this.lang = lang;
        return this;
    }

    public ApplicationException message(String message) {
        this.message = message;
        return this;
    }

    public ApplicationException devMessage(String devMessage) {
        this.devMessage = devMessage;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ErrorMessageCode getCode() {
        return code;
    }

    public String getLang() {
        return lang;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDevMessage() {
        return devMessage;
    }

    public ErrorMessage toErrorMessage(boolean debug) {
        ErrorMessage msg = new ErrorMessage();
        msg.code = code != null ? code.getCode() : null;
        msg.lang = lang;
        msg.message = message;
        msg.status = status;

        if (debug) {
            msg.devMessage = devMessage;
            msg.trace(this);
        }

        return msg;
    }

}
