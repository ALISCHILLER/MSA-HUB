package com.msa.msahub.core.platform.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.msa.msahub.features.analytics.data.local.dao.AnalyticsDao
import com.msa.msahub.features.analytics.data.local.entity.SensorAnalyticsEntity
import com.msa.msahub.features.automation.data.local.dao.AutomationDao
import com.msa.msahub.features.automation.data.local.dao.AutomationLogDao
import com.msa.msahub.features.automation.data.local.entity.AutomationEntity
import com.msa.msahub.features.automation.data.local.entity.AutomationLogEntity
import com.msa.msahub.features.devices.data.local.dao.*
import com.msa.msahub.features.devices.data.local.entity.*
import com.msa.msahub.features.scenes.data.local.dao.SceneDao
import com.msa.msahub.features.scenes.data.local.entity.SceneEntity

@Database(
    entities = [
        DeviceEntity::class,
        DeviceStateEntity::class,
        DeviceHistoryEntity::class,
        OfflineCommandEntity::class,
        AutomationEntity::class,
        AutomationLogEntity::class,
        SceneEntity::class,
        SensorAnalyticsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceStateDao(): DeviceStateDao
    abstract fun deviceHistoryDao(): DeviceHistoryDao
    abstract fun offlineCommandDao(): OfflineCommandDao
    abstract fun automationDao(): AutomationDao
    abstract fun automationLogDao(): AutomationLogDao
    abstract fun sceneDao(): SceneDao
    abstract fun analyticsDao(): AnalyticsDao
}
