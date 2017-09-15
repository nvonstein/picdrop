/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.common.base.Strings;
import com.picdrop.guice.provider.ResourceContainer;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.DateTime;

/**
 *
 * @author nvonstein
 */
public class RoundRobinFileRepository implements FileRepository<String> {
    
    protected Map<String, FileRepository<String>> repos = new HashMap<>();
    protected List<String> names = new ArrayList<>();

    // group(0) - /abc/123/xyz
    // group(1) - abc
    private final String regex = "^[\\/]*([^\\/]+)\\/.+$";
    private final Pattern pattern;
    
    protected boolean isInit = false;
    
    public RoundRobinFileRepository() {
        this.pattern = Pattern.compile(regex);
    }
    
    protected void checkInit() {
        if (!isInit) {
            throw new IllegalStateException("File repository not initialized");
        }
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
    
    public String registerRepository(FileRepository<String> repo) {
        String name = String.format("store%d", this.names.size());
        registerRepository(name, repo);
        return name;
    }
    
    public RoundRobinFileRepository registerRepository(String name, FileRepository<String> repo) {
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
    
    protected String chooseRepository() {
        if (this.names.size() > 1) {
            int i = DateTime.now().getMillisOfSecond() % this.names.size();
            return this.names.get(i);
        }
        return this.names.get(0);
    }
    
    protected String maskRepository(String id, String name) {
        return id.replace("/" + name, "");
    }
    
    @Override
    public String write(String entity, ResourceContainer cnt) throws IOException {
        checkInit();
        String name = (entity == null)
                ? chooseRepository()
                : resolveRepository(entity);
        
        FileRepository<String> repo = this.repos.get(name);
        String id = repo.write((entity == null)
                        ? entity
                        : maskRepository(entity, name), cnt);
        
        String sep = (id.startsWith("/"))
                ? ""
                : "/";
        
        return String.format("/%s%s%s", name, sep, id);
    }
    
    @Override
    public InputStream read(String entity) throws IOException {
        checkInit();
        String name = resolveRepository(entity);
        if (name == null) {
            throw new IOException(String.format("Unable to resolve file repository from file identifier '%s'", entity));
        }
        
        FileRepository<String> repo = this.repos.get(name);
        if (repo == null) {
            throw new IOException(String.format("Unable to resolve file repository '%s'", name));
        }
        return repo.read(maskRepository(entity, name));
    }
    
    @Override
    public boolean delete(String entity) throws IOException {
        checkInit();
        String name = resolveRepository(entity);
        if (name == null) {
            throw new IOException(String.format("Unable to resolve file repository from file identifier '%s'", entity));
        }
        
        FileRepository<String> repo = this.repos.get(name);
        if (repo == null) {
            throw new IOException(String.format("Unable to resolve file repository '%s'", name));
        }
        return repo.delete(maskRepository(entity, name));
    }
    
}
