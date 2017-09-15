/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.picdrop.guice.provider.ResourceContainer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author i330120
 */
public class MurmurFileRepository extends AbstractFileRepository<String> {

    HashFunction hashf;

    public MurmurFileRepository(String rootdir) {
        super(rootdir);
        this.hashf = Hashing.murmur3_128();
    }

    @Override
    public InputStream read(String entity) throws IOException {
        checkInit();
        if ((entity == null) || Strings.isNullOrEmpty(entity)) {
            throw new IllegalArgumentException("No file entity provided!");
        }

        File file = new File(rootDir, entity);
        return new FileInputStream(file);
    }

    @Override
    public boolean delete(String entity) throws IOException {
        checkInit();
        if ((entity == null) || Strings.isNullOrEmpty(entity)) {
            return true;
        }

        File file = new File(rootDir, entity);
        return file.delete();
    }

    @Override
    public String write(String entity, ResourceContainer cnt) throws IOException {
        checkInit();
        String fileId = entity;

        if (Strings.isNullOrEmpty(fileId)) {
            String uuid = UUID.randomUUID().toString();
            byte[] hash = hashf.hashUnencodedChars(uuid).asBytes();
            fileId = String.format("/%s/%s",
                    DatatypeConverter.printHexBinary(hash).toLowerCase(),
                    cnt.getName());
        }

        File f = new File(rootDir, fileId);
        f.mkdirs();
        
        Files.copy(cnt.get(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return fileId;
    }

}
