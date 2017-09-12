/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.picdrop.guice.provider.JWETokenCryptoProvider;
import com.picdrop.guice.provider.JWETokenDirectEncrypterDecrypterProvider;
import com.picdrop.guice.provider.JWSTokenMACSignerVerifierProvider;
import com.picdrop.guice.provider.JWSTokenSignatureProvider;
import com.picdrop.guice.provider.PKIXProvider;
import com.picdrop.guice.provider.SecureStorePKIXProvider;
import com.picdrop.guice.provider.SecureStoreProvider;
import com.picdrop.guice.provider.SecureStoreProviderImpl;
import com.picdrop.guice.provider.StaticSymmetricKeyProvider;
import com.picdrop.guice.provider.SymmetricKeyProvider;
import com.picdrop.guice.provider.TokenCipherProvider;
import com.picdrop.guice.provider.TokenSignerProvider;
import com.picdrop.security.SecureStore;
import com.picdrop.security.token.cipher.TokenCipher;
import com.picdrop.security.token.cipher.TokenCipherImpl;
import com.picdrop.security.token.signer.TokenSigner;
import com.picdrop.security.token.signer.TokenSignerImpl;
import java.io.IOException;
import java.security.KeyPair;
import javax.crypto.SecretKey;

/**
 *
 * @author nvonstein
 */
public class CryptoModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(ThrowingProviderBinder.forModule(this));

        bindSecureStore(binder);

        bindSignatureProviders(binder);

        bindCryptoProviders(binder);

        bindSymmeticKeyProviders(binder);

        bindPKIXProvider(binder);
    }

    protected void bindSecureStore(Binder binder) {
        ThrowingProviderBinder.create(binder)
                .bind(SecureStoreProvider.class, SecureStore.class)
                .to(SecureStoreProviderImpl.class)
                .in(Singleton.class);
    }

    protected void bindSignatureProviders(Binder binder) {
        ThrowingProviderBinder.create(binder)
                .bind(JWSTokenSignatureProvider.SignerCheckedProvider.class, JWSSigner.class)
                .to(JWSTokenMACSignerVerifierProvider.JWSTokenMACSignerProvider.class)
                .asEagerSingleton();

        ThrowingProviderBinder.create(binder)
                .bind(JWSTokenSignatureProvider.VerifierCheckedProvider.class, JWSVerifier.class)
                .to(JWSTokenMACSignerVerifierProvider.JWSTokenMACVerifierProvider.class)
                .asEagerSingleton();
    }

    protected void bindCryptoProviders(Binder binder) {
        ThrowingProviderBinder.create(binder)
                .bind(JWETokenCryptoProvider.EncrypterCheckedProvider.class, JWEEncrypter.class)
                .to(JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectEncrypterProvider.class)
                .asEagerSingleton();

        ThrowingProviderBinder.create(binder)
                .bind(JWETokenCryptoProvider.DecrypterCheckedProvider.class, JWEDecrypter.class)
                .to(JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectDecrypterProvider.class)
                .asEagerSingleton();
    }

    protected void bindSymmeticKeyProviders(Binder binder) {
        ThrowingProviderBinder.create(binder)
                .bind(SymmetricKeyProvider.class, SecretKey.class)
                .annotatedWith(Names.named("security.signature.key.provider"))
                .to(StaticSymmetricKeyProvider.class)
                .asEagerSingleton();

        ThrowingProviderBinder.create(binder)
                .bind(SymmetricKeyProvider.class, SecretKey.class)
                .annotatedWith(Names.named("security.crypto.sym.key.provider"))
                .to(StaticSymmetricKeyProvider.class)
                .asEagerSingleton();
    }

    protected void bindPKIXProvider(Binder binder) {
        ThrowingProviderBinder.create(binder)
                .bind(PKIXProvider.class, KeyPair.class)
                .to(SecureStorePKIXProvider.class)
                .in(Singleton.class);
    }

    @Singleton
    @CheckedProvides(TokenCipherProvider.class)
    protected TokenCipher provideTokenCipher(
            @Named("token.cipher.alg") String alg,
            @Named("token.cipher.meth") String meth,
            JWETokenCryptoProvider.EncrypterCheckedProvider encProv,
            JWETokenCryptoProvider.DecrypterCheckedProvider decProv) throws IOException {
        JWEEncrypter enc = encProv.get();
        JWEDecrypter dec = decProv.get();
        JWEAlgorithm algParsed = JWEAlgorithm.parse(alg);
        EncryptionMethod methParsed = EncryptionMethod.parse(meth);

        if (!enc.supportedJWEAlgorithms().contains(algParsed) || dec.supportedJWEAlgorithms().contains(algParsed)) {
            throw new IOException(String.format("Unsupported encryption algorithm provided. Must be one of the following: %s", enc.supportedJWEAlgorithms().toString()));
        }

        if (!enc.supportedEncryptionMethods().contains(methParsed) || dec.supportedEncryptionMethods().contains(methParsed)) {
            throw new IOException(String.format("Unsupported encryption method provided. Must be one of the following: %s", enc.supportedEncryptionMethods().toString()));
        }
        return new TokenCipherImpl(algParsed, methParsed, enc, dec);
    }

    @Singleton
    @CheckedProvides(TokenSignerProvider.class)
    protected TokenSigner provideTokenSigner(
            @Named("token.signer.alg") String alg,
            JWSTokenSignatureProvider.SignerCheckedProvider signProv,
            JWSTokenSignatureProvider.VerifierCheckedProvider verifProv) throws IOException {
        JWSSigner sig = signProv.get();
        JWSVerifier verf = verifProv.get();
        JWSAlgorithm algParsed = JWSAlgorithm.parse(alg);

        if (!sig.supportedJWSAlgorithms().contains(algParsed) || verf.supportedJWSAlgorithms().contains(algParsed)) {
            throw new IOException(String.format("Unsupported signature algorithm provided. Must be one of the following: %s", sig.supportedJWSAlgorithms().toString()));
        }

        return new TokenSignerImpl(algParsed, sig, verf);
    }
}
