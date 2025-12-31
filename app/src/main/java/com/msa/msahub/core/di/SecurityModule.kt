package com.msa.msahub.core.di

import com.msa.msahub.core.security.auth.AuthTokenStore
import com.msa.msahub.core.security.auth.AuthTokenStoreImpl
import com.msa.msahub.core.security.crypto.AesCryptoBox
import com.msa.msahub.core.security.crypto.CryptoBox
import com.msa.msahub.core.security.keys.AndroidKeyManager
import com.msa.msahub.core.security.keys.KeyManager
import com.msa.msahub.core.security.storage.SecurePrefs
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object SecurityModule {
    val module = module {
        // استفاده از EncryptedSharedPreferences در قالب SecurePrefs
        single { SecurePrefs(androidContext()) }
        
        // مدیریت کلیدها با استفاده از Android KeyStore
        single<KeyManager> { AndroidKeyManager() }
        
        // جعبه ابزار رمزنگاری متقارن
        single<CryptoBox> { AesCryptoBox(get()) }
        
        // انبار امن توکن‌های احراز هویت
        single<AuthTokenStore> { AuthTokenStoreImpl(get()) }
    }
}
