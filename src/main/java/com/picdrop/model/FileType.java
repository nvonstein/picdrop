/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 *
 * @author i330120
 */
public enum FileType {
    WILDTYPE("*"),
    IMAGE_WILDTYPE("image/*"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_TIFF("image/tiff"),
    UNKNOWN("unknown");

    private String name;

    private FileType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @JsonCreator
    public static FileType forName(String name) {
        for (FileType ft : FileType.values()) {
            if (ft.name.equalsIgnoreCase(name)) {
                return ft;
            }
        }
        return FileType.UNKNOWN;
    }

    private static boolean isWildType(String type) {
        return "*".equals(type);
    }

    private static boolean covers(String[] source, String[] target) {
        if ((source == null) || (target == null)) {
            return false;
        }
        for (int i = 0; i < source.length; i++) {
            if (isWildType(source[i])) { // Wildtype
                return true;
            } else if (i >= target.length) { // target is less specific
                return false;
            } else if (!source[i].equalsIgnoreCase(target[i])) { // type mismatch
                return false;
            }
        }
        return true; // all matching
    }

    public boolean isCovering(FileType ft) {
        if (ft == null) {
            return false;
        }
        String[] extChain = ft.name.split("/");
        String[] intChain = this.name.split("/");

        return FileType.covers(intChain, extChain);
    }

    public boolean isCoveredBy(FileType ft) {
        if (ft == null) {
            return false;
        }
        String[] extChain = ft.name.split("/");
        String[] intChain = this.name.split("/");

        return FileType.covers(extChain, intChain);
    }
}
