package com.msa.msahub.core.di

import com.msa.msahub.core.security.auth.AuthTokenStore
import com.msa.msahub.core.security.auth.AuthTokenStoreImpl
import com.msa.msahub.core.security.crypto.AesCryptoBox
import com.msa.msahub.core.security.crypto.CryptoBox
import com.msa.msahub.core.security.keys.AndroidKeyManager
import com.msa.msahub.core.security.keys.KeyManager
import com.msa.msahub.core.security.storage.SecurePrefs
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

object SecurityModule {
    val module = module {
        single { SecurePrefs(androidContext()) }
        single<KeyManager> { AndroidKeyManager() }
        single<CryptoBox> { AesCryptoBox(get()) }
        single<AuthTokenStore> { AuthTokenStoreImpl(get(), get(named("app_scope"))) }
    }
}
