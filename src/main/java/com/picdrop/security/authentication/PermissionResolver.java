/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.authentication;

import com.google.common.base.Strings;

/**
 *
 * @author nvonstein
 */
public class PermissionResolver {

    private boolean isWildtype(String in) {
        return "*".equals(in);
    }

    private boolean hasNextNonTerminal(String[] array, int i) {
        return i < (array.length - 2);
    }

    private boolean isNextTerminal(String[] array, int i) {
        return i == (array.length - 2);
    }

    private boolean match(String s1, String s2) {
        return isWildtype(s1) || isWildtype(s2) || s1.equals(s2);
    }

    public boolean resolve(String required, String actual) {
        String[] reqArray = required.split("/");
        String[] acArray = actual.split("/");
        int i = 0;
        int j = 0;

        do {
            if (Strings.isNullOrEmpty(reqArray[i])) {
                i++;
            }
            if (Strings.isNullOrEmpty(acArray[j])) {
                j++;
            }
            if (isNextTerminal(reqArray, i) && !isNextTerminal(acArray, j)) {
                return false;
            }
            if (!match(reqArray[i], acArray[j])) {
                return false;
            }
            if (!isNextTerminal(reqArray, i)
                    && (!isWildtype(reqArray[i])
                    || (isWildtype(reqArray[i]) && hasNextNonTerminal(reqArray, i)))) {
                i++;
            }
            if (!isNextTerminal(acArray, j)
                    && (!isWildtype(acArray[j])
                    || (isWildtype(acArray[j]) && hasNextNonTerminal(acArray, j)))) {
                j++;
            }
        } while (!isNextTerminal(reqArray, i) || !isNextTerminal(acArray, j));

        return match(reqArray[i], acArray[j]) && reqArray[i + 1].equals(acArray[j + 1]);
    }
}
