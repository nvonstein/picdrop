/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import com.google.common.base.Strings;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author i330120
 */
public abstract class HttpHelper {

    public static String getCookieValue(String withName, Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(withName)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    private static String parseFieldFromDispositionHeader(MultivaluedMap<String, String> header, String field) {
        String[] contentDispositionHeader = header.getFirst("Content-Disposition").split(";");
        for (String name : contentDispositionHeader) {
            if ((name.trim().startsWith(field))) {
                String[] tmp = name.split("=");
                return tmp[1].trim().replaceAll("\"", "");
            }
        }
        return "";
    }

    public static MediaType parseContentType(MultivaluedMap<String, String> header) {
        String[] contentTypeHeader = header.getFirst("Content-Type").split(":");
        if (contentTypeHeader.length > 1) {
            try {
                return MediaType.valueOf(contentTypeHeader[1]);
            } catch (Exception e) {
                return MediaType.WILDCARD_TYPE;
            }
        }
        return MediaType.WILDCARD_TYPE;
    }

    public static String parseFilename(MultivaluedMap<String, String> header) {
        return parseFieldFromDispositionHeader(header, "filename");
    }

    public static String parseFilename(MultivaluedMap<String, String> header, String otherwise) {
        String fn = parseFilename(header);
        return (Strings.isNullOrEmpty(fn)) ? otherwise : fn;
    }

    public static String parseName(MultivaluedMap<String, String> header) {
        return parseFieldFromDispositionHeader(header, "name");
    }
}
