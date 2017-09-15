/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractFileRepository<T> implements FileRepository<T> {

    protected File rootDir;
    protected boolean isInit = false;

    public AbstractFileRepository(String rootDir) {
        this.rootDir = new File(rootDir);
    }

    protected boolean isInit() {
        return this.isInit;
    }

    protected void checkInit() {
        if (!isInit) {
            throw new IllegalStateException("File repository not initialized");
        }
    }

    @Override
    public void init(boolean generate) throws IOException {
        if (isInit()) {
            return;
        }

        if (generate) {
            this.rootDir.mkdirs();
        }
        if (!this.rootDir.exists() || !this.rootDir.canWrite() || !this.rootDir.isDirectory()) {
            throw new IOException(String.format("The dir '%s' is not accessible", rootDir.getAbsolutePath()));
        }

        this.isInit = true;
    }
}
