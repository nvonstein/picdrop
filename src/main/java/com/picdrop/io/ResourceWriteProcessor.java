/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io;

import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.guice.provider.InputStreamProvider;
import com.picdrop.io.writer.FileWriter;
import com.picdrop.model.resource.FileResource;
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
public class ResourceWriteProcessor implements FileProcessor<FileResource> {

    HashFunction hashf;
    File rootdir;

    @Inject
    public ResourceWriteProcessor(@Named("service.file.store") String rootdir) {
        this.hashf = Hashing.murmur3_128();
        this.rootdir = new File(rootdir);
        if (!this.rootdir.exists() || !this.rootdir.canWrite() || !this.rootdir.isDirectory()) {
            throw new IllegalArgumentException(String.format("the dir '%s' is not accessible", rootdir));
        }
    }
//    @Inject
//    protected FileWriter writer;
//
//    @Override
//    public FileResource write(FileResource entity, InputStreamProvider in) throws IOException {
//        String fileUri = this.writer.write(entity.getFileUri(), in.get());
//        entity.setFileUri(fileUri);
//
//        return entity;
//    }

    @Override
    public InputStream read(FileResource entity) throws IOException {
        if ((entity == null) || Strings.isNullOrEmpty(entity.getFileId())) {
            throw new IllegalArgumentException("No file entity provided!");
        }
        
        File file = new File(rootdir, entity.getFileId());
        return new FileInputStream(file);
    }

    @Override
    public boolean delete(FileResource entity) throws IOException {
        if ((entity == null) || Strings.isNullOrEmpty(entity.getFileId())) {
            return true;
        }
        
        File file = new File(rootdir,entity.getFileId());
        return file.delete();
    }

    @Override
    public FileResource write(FileResource entity, InputStreamProvider in) throws IOException {
        String uuid = UUID.randomUUID().toString();
        byte[] hash = hashf.hashUnencodedChars(uuid).asBytes();

        String fileId = DatatypeConverter.printHexBinary(hash);
        
        File f = new File(rootdir, fileId);
        
        Files.copy(in.get(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);

        entity.setFileId(fileId);
        return entity;
    }

}
