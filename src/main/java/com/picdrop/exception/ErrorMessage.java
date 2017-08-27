/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.exception;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 *
 * @author nvonstein
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
public class ErrorMessage {
    protected String code;
    protected String lang;
    protected String message;
    protected String devMessage;
    protected String trace;
}
