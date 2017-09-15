/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.picdrop.guice.provider.ResourceContainer;
import java.io.IOException;
import java.io.InputStream;
import org.joda.time.DateTime;

/**
 *
 * @author nvonstein
 */
public class RoundRobinFileRepository extends AbstractFileMultiRepository {

    public RoundRobinFileRepository() {
        super();
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

    protected String chooseRepository() {
        if (this.names.size() > 1) {
            int i = DateTime.now().getMillisOfDay() % this.names.size();
            return this.names.get(i);
        }
        return this.names.get(0);
    }

}
