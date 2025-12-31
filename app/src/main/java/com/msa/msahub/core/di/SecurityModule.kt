package com.msa.msahub.core.di

import com.msa.msahub.core.security.auth.AuthTokenStore
import com.msa.msahub.core.security.auth.AuthTokenStoreImpl
import com.msa.msahub.core.security.crypto.AesCryptoBox
import com.msa.msahub.core.security.crypto.CryptoBox
import com.msa.msahub.core.security.keys.AndroidKeyManager
import com.msa.msahub.core.security.keys.KeyManager
import org.koin.dsl.module

object SecurityModule {
    val module = module {
        single { AndroidKeyManager() }
        single<KeyManager> { get<AndroidKeyManager>() }
        single<CryptoBox> { AesCryptoBox(get()) }
        single<AuthTokenStore> { AuthTokenStoreImpl(get(), get()) }
    }
}
