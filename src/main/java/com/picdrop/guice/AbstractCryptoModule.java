/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.guice;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.picdrop.guice.names.Encryption;
import com.picdrop.guice.names.Signature;
import com.picdrop.guice.provider.JWECryptoProvider;
import com.picdrop.guice.provider.JWSSignatureProvider;
import com.picdrop.guice.provider.SymmetricKeyProvider;
import com.picdrop.guice.provider.implementation.JWEDirectCryptoProvider;
import com.picdrop.guice.provider.implementation.JWSMACSignatureProvider;
import com.picdrop.guice.provider.implementation.StaticSymmetricKeyProvider;
import com.picdrop.security.token.cipher.TokenCipher;
import com.picdrop.security.token.signer.TokenSigner;
import java.io.IOException;
import javax.crypto.SecretKey;

/**
 *
 * @author nvonstein
 */
public abstract class AbstractCryptoModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(ThrowingProviderBinder.forModule(this));
        bindSignatureProviders(binder);
        bindCryptoProviders(binder);
    }

    protected void bindSecureStore(Binder binder) {
    }

    protected void bindSignatureProviders(Binder binder) {
        ThrowingProviderBinder.create(binder).bind(JWSSignatureProvider.SignerCheckedProvider.class, JWSSigner.class).to(JWSMACSignatureProvider.SignerProvider.class).asEagerSingleton();
        ThrowingProviderBinder.create(binder).bind(JWSSignatureProvider.VerifierCheckedProvider.class, JWSVerifier.class).to(JWSMACSignatureProvider.VerifierProvider.class).asEagerSingleton();
        ThrowingProviderBinder.create(binder).bind(SymmetricKeyProvider.class, SecretKey.class).annotatedWith(Signature.class).to(StaticSymmetricKeyProvider.class).asEagerSingleton();
    }

    protected void bindCryptoProviders(Binder binder) {
        ThrowingProviderBinder.create(binder).bind(JWECryptoProvider.EncrypterCheckedProvider.class, JWEEncrypter.class).to(JWEDirectCryptoProvider.EncrypterProvider.class).asEagerSingleton();
        ThrowingProviderBinder.create(binder).bind(JWECryptoProvider.DecrypterCheckedProvider.class, JWEDecrypter.class).to(JWEDirectCryptoProvider.DecrypterProvider.class).asEagerSingleton();
        //        Required for symm. encryption --------------------
        ThrowingProviderBinder.create(binder).bind(SymmetricKeyProvider.class, SecretKey.class).annotatedWith(Encryption.class).to(StaticSymmetricKeyProvider.class).asEagerSingleton();
        //        Required for asymm. encryption --------------------
        //        ThrowingProviderBinder.create(binder)
        //                .bind(SecureStoreProvider.class, SecureStore.class)
        //                .to(SecureStoreProviderImpl.class)
        //                .in(Singleton.class);
        //
        //        ThrowingProviderBinder.create(binder)
        //                .bind(PKIXProvider.class, KeyPair.class)
        //                .annotatedWith(Encryption.class)
        //                .to(SecureStorePKIXProvider.class)
        //                .in(Singleton.class);
    }

    protected abstract TokenCipher provideTokenCipher(String alg, String meth, JWECryptoProvider.EncrypterCheckedProvider encProv, JWECryptoProvider.DecrypterCheckedProvider decProv) throws IOException;

    protected abstract TokenSigner provideTokenSigner(String alg, JWSSignatureProvider.SignerCheckedProvider signProv, JWSSignatureProvider.VerifierCheckedProvider verifProv) throws IOException;

}
