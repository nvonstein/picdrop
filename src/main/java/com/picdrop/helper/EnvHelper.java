/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.helper;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author i330120
 */
public abstract class EnvHelper {

    protected static Properties config;

    protected static String getEnv(String name) {
        return System.getProperty(name);
    }

    protected static Properties parsePropertyFile(String path) throws FileNotFoundException, IOException {
        FileReader reader = null;
        File f = new File(path);

        reader = new FileReader(f);
        Properties p = new Properties(getDefaultProperties());
        try {
            p.load(reader);
        } finally {
            reader.close();
        }
        return p;
    }

    public static void setConfig(Properties config) {
        EnvHelper.config = config;
    }

    protected static Properties getDefaultProperties() {
        Properties p = new Properties();

        p.put("picdrop.validation.email.regex", "^[^@]+[@][^@]+[.][^@]+$");

        p.put("service.cookie.enabled", "true");
        p.put("service.cookie.domain", "localhost");
        p.put("service.cookie.maxage", "900");
        p.put("service.cookie.http", "true");
        p.put("service.cookie.secure", "false");

        p.put("service.cookie.auth.name", "auth");
        p.put("service.cookie.refresh.name", "refresh");

        p.put("service.jwt.auth.exp", "60"); // 1 Hour
        p.put("service.jwt.refresh.exp", "43200");    // 30 Days
        p.put("service.jwt.iss", "picdrop");
        p.put("service.jwt.aud", "picdrop/app");

        p.put("token.signer.alg", "HS256");
        p.put("token.cipher.alg", "dir");
        p.put("token.cipher.meth", "A128CBC-HS256");

        p.put("service.file.store", "/Users/nvonstein/picdrop/store");

        p.put("service.upload.store", "/Users/nvonstein/picdrop/uploads");
        p.put("service.upload.maxmemory", "100000000"); // 100 MB
        p.put("service.upload.maxfilesize", "10000000"); // 10 MB
        p.put("service.upload.maxrequestsize", "100000000"); // 100 MB

        p.put("service.json.view", "public");

        p.put("service.tika.config", "");

        return p;
    }

    public static Properties getProperties() throws IOException {
        if (config == null) {
            String env = getEnv("picdrop.app.properties");
            if (!Strings.isNullOrEmpty(env)) {
                config = parsePropertyFile(env);
            } else {
                config = getDefaultProperties();
            }
        }
        return config;
    }

    public static Properties getPropertiesTest() {
        Properties p = new Properties();

        p.put("picdrop.validation.email.regex", "^[^@]+[@][^@]+[.][^@]+$");

        p.put("service.cookie.enabled", "true");
        p.put("service.cookie.domain", "localhost");
        p.put("service.cookie.maxage", "900");
        p.put("service.cookie.http", "true");
        p.put("service.cookie.secure", "false");

        p.put("service.cookie.auth.name", "auth");
        p.put("service.cookie.refresh.name", "refresh");

        p.put("service.jwt.auth.exp", "60"); // 1 Hour
        p.put("service.jwt.refresh.exp", "43200");    // 30 Days
        p.put("service.jwt.iss", "picdrop");
        p.put("service.jwt.aud", "picdrop/app");

        p.put("token.signer.alg", "HS256");
        p.put("token.cipher.alg", "dir");
        p.put("token.cipher.meth", "A128CBC-HS256");

        p.put("service.file.store", "/Users/nvonstein/picdrop/store");

        p.put("service.upload.store", Files.createTempDir().getAbsolutePath());
        p.put("service.upload.maxmemory", "100000000"); // 100 MB
        p.put("service.upload.maxfilesize", "10000000"); // 10 MB
        p.put("service.upload.maxrequestsize", "100000000"); // 100 MB

        p.put("service.json.view", "public");

        p.put("service.tika.config", "");

        return p;
    }
}
