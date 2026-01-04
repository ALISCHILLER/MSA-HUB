package com.msa.msahub.core.di

import androidx.room.Room
import com.msa.msahub.core.platform.database.AppDatabase
import com.msa.msahub.core.platform.database.DatabaseInitializer
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

object DatabaseModule {
    val module = module {

        single<AppDatabase> {
            Room.databaseBuilder(
                context = androidContext(),
                klass = AppDatabase::class.java,
                name = "msa_hub.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        single { get<AppDatabase>().deviceDao() }
        single { get<AppDatabase>().deviceStateDao() }
        single { get<AppDatabase>().deviceHistoryDao() }
        single { get<AppDatabase>().offlineCommandDao() }
        single { get<AppDatabase>().automationDao() }
        single { get<AppDatabase>().automationLogDao() }
        single { get<AppDatabase>().sceneDao() }
        single { get<AppDatabase>().analyticsDao() }

        single {
            DatabaseInitializer(
                deviceDao = get(),
                deviceStateDao = get(),
                logger = get(),
                scope = get(named("app_scope"))
            )
        }
    }
}
