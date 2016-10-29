/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authorization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author i330120
 */
public enum Operation {
    COMMENT("comment"),
    BLOCK("block"),
    RATE("rate");

    private Operation(String name) {
        this.name = name;
    }

    private String name;

    @JsonValue
    @Override
    public String toString() {
        return name;
    }

    @JsonCreator
    public static Operation forName(String name) {
        for (Operation ft : Operation.values()) {
            if (ft.name.equalsIgnoreCase(name)) {
                return ft;
            }
        }
        throw new IllegalArgumentException(String.format("'%s' is not a known operation type", name));
    }

    public static boolean resolve(Operation[] entity, Operation[] op, boolean exclusive) {
        return resolve(Arrays.asList(entity), op, exclusive);
    }

    public static boolean resolve(List<Operation> entity, Operation[] op, boolean exclusive) {
        List<Operation> earoles = entity;
        if ((op == null) || (op.length == 0)) {
            return true;
        }
        if (earoles == null) {
            return false;
        }
        for (Operation a : op) {
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
