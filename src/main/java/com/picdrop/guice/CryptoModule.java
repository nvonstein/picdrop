/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.picdrop.guice.provider.JWETokenCryptoProvider;
import com.picdrop.guice.provider.JWETokenDirectEncrypterDecrypterProvider;
import com.picdrop.guice.provider.JWETokenRSAEncrypterDecrypterProvider;
import com.picdrop.guice.provider.JWSTokenMACSignerVerifierProvider;
import com.picdrop.guice.provider.JWSTokenSignatureProvider;
import com.picdrop.guice.provider.SecureStoreProvider;
import com.picdrop.guice.provider.SecureStoreProviderImpl;
import com.picdrop.guice.provider.SecureStoreSymmetricKeyProvider;
import com.picdrop.guice.provider.StaticSymmetricKeyProvider;
import com.picdrop.guice.provider.SymmetricKeyProvider;
import com.picdrop.security.SecureStore;
import java.io.IOException;
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
                .in(Singleton.class);

        ThrowingProviderBinder.create(binder)
                .bind(JWSTokenSignatureProvider.VerifierCheckedProvider.class, JWSVerifier.class)
                .to(JWSTokenMACSignerVerifierProvider.JWSTokenMACVerifierProvider.class)
                .in(Singleton.class);
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
                .annotatedWith(Names.named("security.signature.key"))
                .to(StaticSymmetricKeyProvider.class)
                .asEagerSingleton();

        ThrowingProviderBinder.create(binder)
                .bind(SymmetricKeyProvider.class, SecretKey.class)
                .annotatedWith(Names.named("security.crypto.sym.key"))
                .to(StaticSymmetricKeyProvider.class)
                .asEagerSingleton();
    }

    @Provides
    protected StaticSymmetricKeyProvider provideStaticSymKeyProvider() {
        StaticSymmetricKeyProvider skProv;
        skProv = new StaticSymmetricKeyProvider();

        return skProv;
    }

//    @Provides
//    @Singleton
//    protected SecureStoreSymmetricKeyProvider provideSecureStoreSymKeyProvider() {
//        SecureStoreSymmetricKeyProvider skProv;
//        skProv = new SecureStoreSymmetricKeyProvider(ssProv, keyName)
//
//        return skProv;
//    }

    @Provides
    @Singleton
    protected JWSTokenMACSignerVerifierProvider.JWSTokenMACVerifierProvider provideTokenMACVerifierProvider(@Named("security.signature.key") SymmetricKeyProvider symKProv) {
        JWSTokenMACSignerVerifierProvider.JWSTokenMACVerifierProvider verf;
        try {
            verf = new JWSTokenMACSignerVerifierProvider.JWSTokenMACVerifierProvider(symKProv.get());
        } catch (IOException ex) {
            return null;
        }

        return verf;
    }

    @Provides
    @Singleton
    protected JWSTokenMACSignerVerifierProvider.JWSTokenMACSignerProvider provideTokenMACSignerProvider(@Named("security.signature.key") SymmetricKeyProvider symKProv) {
        JWSTokenMACSignerVerifierProvider.JWSTokenMACSignerProvider sig;
        try {
            sig = new JWSTokenMACSignerVerifierProvider.JWSTokenMACSignerProvider(symKProv.get());
        } catch (IOException ex) {
            return null;
        }

        return sig;
    }

//    @Provides
//    @Singleton
//    protected JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectEncrypterProvider provideTokenDirectEncrypterProvider(@Named("security.crypto.key.provider") SymmetricKeyProvider symKProv) {
//        JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectEncrypterProvider obj;
//        try {
//            obj = new JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectEncrypterProvider(symKProv.get());
//        } catch (IOException ex) {
//            return null;
//        }
//
//        return obj;
//    }
//
//    @Provides
//    @Singleton
//    protected JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectDecrypterProvider provideTokenDirectDecrypterProvider(@Named("security.crypto.key.provider") SymmetricKeyProvider symKProv) {
//        JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectDecrypterProvider obj;
//        try {
//            obj = new JWETokenDirectEncrypterDecrypterProvider.JWETokenDirectDecrypterProvider(symKProv.get());
//        } catch (IOException ex) {
//            return null;
//        }
//
//        return obj;
//    }
//
//    @CheckedProvides(JWETokenCryptoProvider.EncrypterCheckedProvider.class)
//    @Singleton
//    protected JWETokenRSAEncrypterDecrypterProvider.JWETokenRSAEncrypterProvider provideTokenRSAEncrypterProvider(SecureStoreProvider ssprov) throws IOException {
//        JWETokenRSAEncrypterDecrypterProvider.JWETokenRSAEncrypterProvider enc;
////        try {
//            enc = new JWETokenRSAEncrypterDecrypterProvider.JWETokenRSAEncrypterProvider(ssprov.get());
////        } catch (IOException ex) {
////            return null;
////        }
//        return enc;
//    }
//
//    @Provides
//    @Singleton
//    protected JWETokenRSAEncrypterDecrypterProvider.JWETokenRSADecrypterProvider provideTokenRSADecrypterProvider(SecureStoreProvider ssprov) {
//        JWETokenRSAEncrypterDecrypterProvider.JWETokenRSADecrypterProvider dec;
//        try {
//            dec = new JWETokenRSAEncrypterDecrypterProvider.JWETokenRSADecrypterProvider(ssprov.get());
//        } catch (IOException ex) {
//            return null;
//        }
//        return dec;
//    }

}
