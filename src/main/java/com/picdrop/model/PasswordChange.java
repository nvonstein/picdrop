/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.picdrop.json.Views;

/**
 *
 * @author nvonstein
 */
public class PasswordChange {

    String oldPassword;
    String newPassword;

    @JsonView(value = Views.Ignore.class)
    public String getOldPassword() {
        return oldPassword;
    }

    @JsonView(value = Views.Public.class)
    @JsonProperty("old")
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @JsonView(value = Views.Ignore.class)
    public String getNewPassword() {
        return newPassword;
    }

    @JsonView(value = Views.Public.class)
    @JsonProperty("new")
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
