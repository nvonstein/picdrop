/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.throwingproviders.CheckedProvides;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.picdrop.guice.provider.JWECryptoProvider;
import com.picdrop.guice.provider.JWSSignatureProvider;
import com.picdrop.guice.provider.TokenCipherProvider;
import com.picdrop.guice.provider.TokenSignerProvider;
import com.picdrop.security.token.cipher.TokenCipher;
import com.picdrop.security.token.cipher.TokenCipherImpl;
import com.picdrop.security.token.signer.TokenSigner;
import com.picdrop.security.token.signer.TokenSignerImpl;
import java.io.IOException;

/**
 *
 * @author nvonstein
 */
public class CryptoModule extends AbstractCryptoModule {


    @Singleton
    @CheckedProvides(TokenCipherProvider.class)
    @Override
    protected TokenCipher provideTokenCipher(
            @Named("token.cipher.alg") String alg,
            @Named("token.cipher.meth") String meth,
            JWECryptoProvider.EncrypterCheckedProvider encProv,
            JWECryptoProvider.DecrypterCheckedProvider decProv) throws IOException {
        JWEEncrypter enc = encProv.get();
        JWEDecrypter dec = decProv.get();
        JWEAlgorithm algParsed = JWEAlgorithm.parse(alg);
        EncryptionMethod methParsed = EncryptionMethod.parse(meth);

        if (!enc.supportedJWEAlgorithms().contains(algParsed) || !dec.supportedJWEAlgorithms().contains(algParsed)) {
            throw new IOException(String.format("Unsupported encryption algorithm provided. Must be one of the following: %s", enc.supportedJWEAlgorithms().toString()));
        }

        if (!enc.supportedEncryptionMethods().contains(methParsed) || !dec.supportedEncryptionMethods().contains(methParsed)) {
            throw new IOException(String.format("Unsupported encryption method provided. Must be one of the following: %s", enc.supportedEncryptionMethods().toString()));
        }
        return new TokenCipherImpl(algParsed, methParsed, enc, dec);
    }

    @Singleton
    @CheckedProvides(TokenSignerProvider.class)
    @Override
    protected TokenSigner provideTokenSigner(
            @Named("token.signer.alg") String alg,
            JWSSignatureProvider.SignerCheckedProvider signProv,
            JWSSignatureProvider.VerifierCheckedProvider verifProv) throws IOException {
        JWSSigner sig = signProv.get();
        JWSVerifier verf = verifProv.get();
        JWSAlgorithm algParsed = JWSAlgorithm.parse(alg);

        if (!sig.supportedJWSAlgorithms().contains(algParsed) || !verf.supportedJWSAlgorithms().contains(algParsed)) {
            throw new IOException(String.format("Unsupported signature algorithm provided. Must be one of the following: %s", sig.supportedJWSAlgorithms().toString()));
        }

        return new TokenSignerImpl(algParsed, sig, verf);
    }
}
