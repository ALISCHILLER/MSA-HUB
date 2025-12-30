package com.msa.msahub.core.di

import androidx.room.Room
import com.msa.msahub.core.platform.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single<AppDatabase> {
        Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = "msa_hub.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs موجود واقعی در AppDatabase
    single { get<AppDatabase>().deviceDao() }
    single { get<AppDatabase>().deviceStateDao() }
    single { get<AppDatabase>().deviceHistoryDao() }
    single { get<AppDatabase>().offlineCommandDao() }

    // Scenes (بعد از آپدیت AppDatabase که پایین می‌دم)
    single { get<AppDatabase>().sceneDao() }
}
