/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.io.writer;

import com.google.common.base.Strings;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.picdrop.model.resource.FileResource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;
import com.picdrop.io.FileProcessor;
import java.io.FileInputStream;

/**
 *
 * @author i330120
 */
public class MurmurFileReaderWriter implements FileWriter, FileReader {

    HashFunction hashf;
    File rootdir;

    @Inject
    public MurmurFileReaderWriter(@Named("service.file.store") String rootdir) {
        this.hashf = Hashing.murmur3_128();
        this.rootdir = new File(rootdir);
        if (!this.rootdir.exists() || !this.rootdir.canWrite() || !this.rootdir.isDirectory()) {
            throw new IllegalArgumentException(String.format("the dir '%s' is not accessible", rootdir));
        }
    }

    @Override
    public String write(String path, InputStream in) throws IOException {
        String lpath = path;
        if (Strings.isNullOrEmpty(lpath)) {
            String uuid = UUID.randomUUID().toString();
            byte[] hash = hashf.hashUnencodedChars(uuid).asBytes();

            String rawpath = DatatypeConverter.printHexBinary(hash);

            StringBuilder sb = new StringBuilder();
            sb.append(File.separatorChar)
                    .append(rawpath.substring(0, 8))
                    .append(File.separatorChar)
                    .append(rawpath.substring(8));
            lpath = sb.toString().toLowerCase();
        }
        
        File f = new File(rootdir, lpath);
        if (!f.mkdirs()) {
            throw new IOException("unable to create dirs: " + f.getAbsolutePath());
        }
        
        Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        return lpath;
    }

    @Override
    public InputStream read(String path) throws IOException {
        File f = new File(rootdir,path);
        if (!f.exists() || !f.canRead()) {
            throw new IOException(String.format("'%s%s' does not exists or is not readable", rootdir.getAbsolutePath(), path));
        }
        
        return new FileInputStream(f);
    }

}
