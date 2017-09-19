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

    ERROR_DELETE("ec00001", "Unable to delete resource."),
    BAD_UPLOAD("ec00002", "Bad request: Unable to parse upload request."),
    ERROR_UPLOAD("ec00003", "Unable to process upload."),
    NOT_FOUND("ec00004", "Resource not found"),
    ERROR_OBJ_MERGE("ec00005", "Error while merging state with database."),
    BAD_PHASH("ec00006","Bad request: No password provided."),
    BAD_EMAIL("ec00007","Bad request: No or invalid email provided."),
    BAD_OPERATION("ec00008","Bad request: This operation is not possible on this entity type."),
    BAD_NAME("ec00009","Bad request: Name has not a valid format or is too long."), 
    BAD_RESOURCE("ec000010","Bad request: Resource is not set or invalid."), 
    BAD_REQUEST_BODY("ec000011","Bad request: No request body."), 
    BAD_CITEM("ec000012","Bad request: Collection item is not set or invalid."), 
    BAD_CITEM_NOT_FOUND("ec000013","Bad request: Collection item's resource could not be resolved given id."),
    BAD_COMMENT("ec000014","Bad request: Invalid comment or unable to find/resolve a name."),
    ERROR_INTERNAL("ec000015","An internal server error occured. Please try again later."),
    BAD_JSON("ec000016","Bad Request: Invalid JSON"),
    BAD_REQUEST("ec000017","Bad Request: Invalid request performed."), 
    BAD_UPLOAD_MIME("ec000018","Bad Request: Unprocessable mime type."),
    BAD_RESOURCE_NAME("ec000019","Bad request: Resource file name is too long."), 
    BAD_NAME_TOO_LONG("ec000019","Bad request: Name exceeds limit of 256 characters.");

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
