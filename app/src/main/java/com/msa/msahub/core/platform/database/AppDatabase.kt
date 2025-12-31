package com.msa.msahub.core.platform.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.msa.msahub.features.devices.data.local.dao.DeviceDao
import com.msa.msahub.features.devices.data.local.dao.DeviceHistoryDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.local.entity.DeviceEntity
import com.msa.msahub.features.devices.data.local.entity.DeviceHistoryEntity
import com.msa.msahub.features.devices.data.local.entity.DeviceStateEntity
import com.msa.msahub.features.devices.data.local.entity.OfflineCommandEntity
import com.msa.msahub.features.scenes.data.local.dao.SceneDao
import com.msa.msahub.features.scenes.data.local.entity.SceneEntity
import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.data.local.entity.AutomationEntity

@Database(
    entities = [
        DeviceEntity::class,
        DeviceStateEntity::class,
        DeviceHistoryEntity::class,
        OfflineCommandEntity::class,
        SceneEntity::class,
        AutomationEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceStateDao(): DeviceStateDao
    abstract fun deviceHistoryDao(): DeviceHistoryDao
    abstract fun offlineCommandDao(): OfflineCommandDao
    abstract fun sceneDao(): SceneDao
    abstract fun automationDao(): AutomationDao
}
