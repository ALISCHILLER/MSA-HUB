package com.msa.msahub.core.platform.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.msa.msahub.features.devices.data.local.dao.DeviceDao
import com.msa.msahub.features.devices.data.local.entity.DeviceEntity

@Database(
    entities = [
        DeviceEntity::class
    ],
    version = 1, // Reset version for this test
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}
