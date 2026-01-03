package com.msa.msahub.core.di

import androidx.room.Room
import com.msa.msahub.core.platform.database.AppDatabase
import org.koin.android.ext.koin.androidContext
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

        // Only provide the single DAO from our minimal database
        single { get<AppDatabase>().deviceDao() }
    }
}
