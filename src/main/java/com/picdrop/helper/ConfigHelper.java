/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 *
 * @author nvonstein
 */
public abstract class ConfigHelper {

    public static List<Entry<Object, Object>> listChildProperties(Properties config, String root, boolean terminalOnly) {
        String id = String.format("%s.", root);
        return config.entrySet().stream()
                .filter(e -> ((String) e.getKey()).startsWith(id))
//                .filter(e -> !(terminalOnly && !((String) e.getKey()).replace(id, "").contains(".")))
                .collect(Collectors.toList());
    }

}
