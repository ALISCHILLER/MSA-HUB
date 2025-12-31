package com.msa.msahub.app

import android.app.Application
import com.msa.msahub.background.scheduler.WorkScheduler
import com.msa.msahub.core.di.KoinInitializer
import com.msa.msahub.core.platform.database.DatabaseInitializer
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionManager
import com.msa.msahub.features.devices.data.remote.mqtt.MqttIngestor
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import timber.log.Timber

object AppInitializer : KoinComponent {

    fun init(app: Application) {
        initLogging()
        KoinInitializer.init(app)

        // ۱. مقداردهی اولیه دیتابیس (Seed)
        get<DatabaseInitializer>().seedIfNeeded()

        // ۲. مدیریت اتصال MQTT
        get<MqttConnectionManager>().start()

        // ۳. شروع Ingestor برای دریافت وضعیت دستگاه‌ها
        get<MqttIngestor>().start()

        // ۴. زمان‌بندی کارهای پس‌زمینه (WorkManager)
        get<WorkScheduler>().scheduleAll()
    }

    private fun initLogging() {
        if (Timber.forest().isEmpty()) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
