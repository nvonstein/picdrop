/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security.token.cipher;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author i330120
 */
public class TokenCipherImpl implements TokenCipher {

    JWEAlgorithm alg;
    EncryptionMethod meth;
    @Inject
    Provider<JWEEncrypter> encryptorProv;
    @Inject
    Provider<JWEDecrypter> decryptorProv;

    @Inject
    public TokenCipherImpl(
            @Named("token.cipher.alg") String alg,
            @Named("token.cipher.meth") String meth) {
        this.alg = JWEAlgorithm.parse(alg);
        this.meth = EncryptionMethod.parse(meth);
    }

    @Override
    public JWEObject encrypt(Payload pl) throws IOException {
        return encrypt(pl, null);
    }

    @Override
    public JWEObject encrypt(Payload pl, String contenttype) throws IOException {
        JWEObject jwe = new JWEObject(
                new JWEHeader.Builder(alg, meth)
                .contentType(contenttype) // required to signal nested JWT
                .build(),
                pl);

        try {
            jwe.encrypt(encryptorProv.get());
        } catch (JOSEException ex) {
            throw new IOException("Unable to encrypt token: " + ex.getMessage(), ex);
        }
        return jwe;
    }

    @Override
    public Payload decrypt(String raw) throws IOException, ParseException {
        return decrypt(JWEObject.parse(raw));
    }

    @Override
    public Payload decrypt(JWEObject jwe) throws IOException {
        try {
            jwe.decrypt(decryptorProv.get());
        } catch (JOSEException ex) {
            throw new IOException("Unable to decrypt token: " + ex.getMessage(), ex);
        }

        return jwe.getPayload();
    }

}
