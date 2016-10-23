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
import com.picdrop.model.resource.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;
import com.picdrop.io.FileProcessor;

/**
 *
 * @author i330120
 */
public class MurmurFileWriter implements FileWriter {

    HashFunction hashf;
    File rootdir;

    @Inject
    public MurmurFileWriter(@Named("service.file.store") String rootdir) {
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
            lpath = sb.toString();
        }
        
        File f = new File(rootdir, lpath);
        if (!f.mkdirs()) {
            throw new IOException("unable to create dirs: " + f.getAbsolutePath());
        }
        
        Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        return lpath;
    }

}
