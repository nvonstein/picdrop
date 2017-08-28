/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author i330120
 */
public enum RoleType {
    USER("user"),
    SHARE("share"),
    REGISTERED("registered");

    private RoleType(String name) {
        this.name = name;
    }

    private String name;

    @JsonValue
    @Override
    public String toString() {
        return name;
    }

    @JsonCreator
    public static RoleType forName(String name) {
        for (RoleType ft : RoleType.values()) {
            if (ft.name.equalsIgnoreCase(name)) {
                return ft;
            }
        }
        throw new IllegalArgumentException(String.format("'%s' is not a known role type", name));
    }

    public static boolean resolve(RoleType[] entity, RoleType[] accessRoles, boolean exclusive) {
        return resolve(Arrays.asList(entity), accessRoles, exclusive);
    }

    public static boolean resolve(List<RoleType> entity, RoleType[] accessRoles, boolean exclusive) {
        List<RoleType> earoles = entity;
        if ((accessRoles == null) || (accessRoles.length == 0)) {
            return true;
        }
        if (earoles == null) {
            return false;
        }
        for (RoleType a : accessRoles) {
            if (exclusive) {
                if (!earoles.contains(a)) {
                    return false;
                }
            } else if (earoles.contains(a)) {
                return true;
            }

        }
        return true;
    }
}
