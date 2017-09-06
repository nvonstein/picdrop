/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security;

import com.google.common.base.Charsets;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.picdrop.guice.provider.PKIXProvider;

/**
 *
 * @author nvonstein
 */
public class SecureStore implements PKIXProvider {
    
    static final String KS_NAME = "Keystore.jks";
    
    private static final String STORE_PASS = "84nsMSUa8xZAWdLOwTuKGO0mS1GqHOtugnxxS5BFELcd8s0B1y37BE4278Wnhko"; // TODO !!! JUST FOR DEMO !!!
    private static final String ENTRY_PASS = "m2nl6L0cWjF6hgQoV73JDM2dO4KvrnjzcPDoXOcX1152qqeBgEg8huob2ZY1Beu"; // TODO !!! JUST FOR DEMO !!!
    private static final String CERT_ALIAS = "picdrop-cert";
    
    private final String dir;
    private KeyStore ks;
    
    public final static int MAXINDATALIMIT = 116;
    public final static int MAXOUTDATALIMIT = 128;
    public final static int DATA2HEXMULTIPLIER = 2;
    
    public SecureStore(String dir) {
        this.dir = dir;
    }
    
    public SecureStore(String dir, boolean load) throws IOException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        this(dir);
        if (load) {
            loadStore();
        }
    }
    
    public boolean exists() {
        return new File(dir, KS_NAME).exists();
    }
    
    protected final void loadStore() throws FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        if (this.ks == null) {
            FileInputStream fs = null;
            try {
                File f = new File(this.dir, KS_NAME);
                fs = new FileInputStream(f);
                
                ks = KeyStore.getInstance("JCEKS");
                ks.load(fs, STORE_PASS.toCharArray());
            } finally {
                if (fs != null) {
                    fs.close();
                }
            }
        }
    }
    
    protected final void writeStore() throws FileNotFoundException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        if (this.ks != null) {
            FileOutputStream fo = null;
            try {
                File f = new File(this.dir, KS_NAME);
                fo = new FileOutputStream(f);
                this.ks.store(fo, STORE_PASS.toCharArray());
                fo.close();
            } finally {
                if (fo != null) {
                    fo.close();
                }
            }
        }
    }
    
    protected boolean createKeyStore() throws IOException, InterruptedException {
        List<String> cmd = Arrays.asList(
                //        ProcessBuilder pb = new ProcessBuilder(
                "keytool",
                "-genkey",
                "-noprompt",
                "-keyalg", "RSA",
                "-validity", "3650",
                "-keystore", this.dir + "/" + KS_NAME,
                "-keysize", "1024",
                "-alias", CERT_ALIAS,
                "--storepass", STORE_PASS,
                "--keypass", STORE_PASS,
                "-dname", "CN=picdrop.com, OU=NRW, O=PICDROP, L=PICDROP, S=app, C=DE");
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        
        Process p = pb.start();
        int ret = p.waitFor();
        
        if (ret != 0) {
            Scanner sc = new Scanner(p.getInputStream());
            StringBuilder sb = new StringBuilder();
            
            while (sc.hasNext()) {
                sb.append(sc.nextLine());
            }
            sc.close();
            throw new RuntimeException(sb.toString());
        }
        
        return ret == 0;
    }
    
    protected boolean storeValue(String key, String value) throws KeyStoreException, IOException {
        try {
            loadStore();
        } catch (Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        SecretKeySpec valueKs = new SecretKeySpec(value.getBytes(Charsets.UTF_8), "AES");
        KeyStore.SecretKeyEntry valueKe = new KeyStore.SecretKeyEntry(valueKs);
        
        KeyStore.PasswordProtection passproto = new KeyStore.PasswordProtection(ENTRY_PASS.toCharArray());
        
        ks.setEntry(key, valueKe, passproto);
        
        return true;
    }
    
    public PublicKey getPublicKey() throws KeyStoreException, IOException {
        try {
            loadStore();
        } catch (Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        Certificate crt = ks.getCertificate(CERT_ALIAS);
        return crt.getPublicKey();
    }
    
    public PrivateKey getPrivateKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException {
        try {
            loadStore();
        } catch (Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        Key k = ks.getKey(CERT_ALIAS, STORE_PASS.toCharArray());
        
        return (PrivateKey) k;
    }
    
    public SecretKey readValueRaw(String key) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        try {
            loadStore();
        } catch (Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        KeyStore.PasswordProtection passproto = new KeyStore.PasswordProtection(ENTRY_PASS.toCharArray());
        
        KeyStore.SecretKeyEntry valueKe = (KeyStore.SecretKeyEntry) ks.getEntry(key, passproto);
        return valueKe.getSecretKey();
    }
    
    public byte[] readValueByte(String key) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        SecretKey sk = readValueRaw(key);
        return (sk == null)
                ? new byte[0]
                : sk.getEncoded();
    }
    
    public String readValue(String key) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException, IOException {
        return new String(readValueByte(key), Charsets.UTF_8);
    }
    
    public boolean hasValue(String key) throws KeyStoreException, IOException {
        try {
            loadStore();
        } catch (Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        return ks.containsAlias(key);
    }
    
    protected boolean deleteValue(String key) throws KeyStoreException, IOException {
        try {
            loadStore();
        } catch (Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        
        ks.deleteEntry(key);
        
        return true;
    }
    
    public byte[] toSecure(byte[] indata) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, KeyStoreException, IllegalBlockSizeException, BadPaddingException, IOException {
        try {
            loadStore();
        } catch (Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        Cipher ci = Cipher.getInstance("RSA");
        ci.init(Cipher.ENCRYPT_MODE, this.getPublicKey());
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        int inoff = 0;
        while (inoff < indata.length) {
            int size = ((inoff + MAXINDATALIMIT) < indata.length) ? MAXINDATALIMIT : indata.length - inoff;
            // size is fine
            byte[] b = ci.doFinal(indata, inoff, size);
            bo.write(b);
            inoff += size;
        }
        
        return bo.toByteArray();
    }
    
    public byte[] toInsecure(byte[] indata) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnrecoverableKeyException, KeyStoreException, IllegalBlockSizeException, BadPaddingException, IOException {
        try {
            loadStore();
        } catch (Exception ex) {
            throw new IOException(ex.getMessage(), ex);
        }
        Cipher ci = Cipher.getInstance("RSA");
        ci.init(Cipher.DECRYPT_MODE, ks.getKey(CERT_ALIAS, STORE_PASS.toCharArray()));
        int inoff = 0;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        
        while (inoff < indata.length) {
            int size = ((inoff + MAXOUTDATALIMIT) < indata.length) ? MAXOUTDATALIMIT : indata.length - inoff;
            // size is fine

            byte[] b = ci.doFinal(indata, inoff, size);
            bo.write(b);
            inoff += size;
        }
        return bo.toByteArray();
    }
    
    @Override
    public KeyPair get() throws IOException {
        try {
            return new KeyPair(getPublicKey(), getPrivateKey());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            throw new IOException(ex.getMessage(), ex);
        }
    }
}
