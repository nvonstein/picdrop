/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.common.base.Strings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author nvonstein
 */
public class ScopedRoundRobinFileRepository extends RoundRobinFileRepository {

    protected List<String> activeRepoNames = new ArrayList<>();

    public ScopedRoundRobinFileRepository() {
        super();
    }

    @Override
    public void init(boolean generate) throws IOException {
        if (activeRepoNames.isEmpty()) {
            throw new IOException("No active file repository registered");
        }
        super.init(generate);
    }

    @Override
    public ScopedRoundRobinFileRepository registerRepository(String name, FileRepository<String> repo) {
        super.registerRepository(name, repo);
        return this;
    }

    public ScopedRoundRobinFileRepository registerActiveRepository(String name, FileRepository<String> activeRepo) {
        if (Strings.isNullOrEmpty(name) || (activeRepo == null)) {
            return this;
        }
        this.activeRepoNames.add(name);
        return registerRepository(name, activeRepo);
    }

    @Override
    protected String chooseRepository() {
        if (this.activeRepoNames.size() > 1) {
            int i = DateTime.now().getMillisOfDay() % this.activeRepoNames.size();
            return this.activeRepoNames.get(i);
        }
        return this.activeRepoNames.get(0);
    }

}
