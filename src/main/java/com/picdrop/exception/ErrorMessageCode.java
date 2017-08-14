/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

/**
 *
 * @author nvonstein
 */
public enum ErrorMessageCode {
    
    ERROR_DELETE("ec00001", "Unable to delete file."),
    BAD_UPLOAD("ec00002", "Bad request: Unable to parse upload request."),
    ERROR_UPLOAD("ec00003", "Unable to process upload."),
    NOT_FOUND("ec00004","Resource not found");
    
    private String code;
    private String defaultMessage;

    private ErrorMessageCode(String code) {
        this.code = code;
    }
    
    private ErrorMessageCode(String code, String defMessage) {
        this.code = code;
        this.defaultMessage = defMessage;
    }

    @Override
    public String toString() {
        return code;
    }
    
    public static ErrorMessageCode forName(String name) {
        for (ErrorMessageCode ec : ErrorMessageCode.values()) {
            if (ec.code.equalsIgnoreCase(name)) {
                return ec;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
