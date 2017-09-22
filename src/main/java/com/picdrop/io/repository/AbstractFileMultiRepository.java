/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io.repository;

import com.google.common.base.Strings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractFileMultiRepository implements FileRepository<String>{

    protected boolean isInit = false;
    protected Map<String, FileRepository<String>> repos = new HashMap<>();
    protected List<String> names = new ArrayList<>();
    protected final Pattern pattern;
    
    // group(0) - /abc/123/xyz
    // group(1) - abc
    private final String regex = "^[\\/]*([^\\/]+)\\/.+$";

    public AbstractFileMultiRepository() {
        this.pattern = Pattern.compile(regex);
    }
    

    @Override
    public void init(boolean generate) throws IOException {
        if (!isInit) {
            if (this.names.isEmpty()) {
                throw new IOException("No file repositories registered");
            }
            for (FileRepository<String> repo : this.repos.values()) {
                repo.init(generate);
            }
            this.isInit = true;
        }
    }

    protected String maskRepository(String id, String name) {
        return id.replace("/" + name, "");
    }

    public String registerRepository(FileRepository<String> repo) {
        String name = String.format("store%d", this.names.size());
        registerRepository(name, repo);
        return name;
    }

    public AbstractFileMultiRepository registerRepository(String name, FileRepository<String> repo) {
        if (Strings.isNullOrEmpty(name) || (repo == null)) {
            return this;
        }
        this.repos.put(name, repo);
        this.names.add(name);
        this.isInit = false;
        return this;
    }

    protected String resolveRepository(String id) {
        Matcher mtch = pattern.matcher(id);
        if (!mtch.matches()) {
            return null;
        }
        return mtch.group(1);
    }

    protected void checkInit() {
        if (!isInit) {
            throw new IllegalStateException("File repository not initialized");
        }
    }

    
}
