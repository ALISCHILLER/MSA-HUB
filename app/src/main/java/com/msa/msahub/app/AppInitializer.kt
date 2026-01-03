package com.msa.msahub.app

import android.app.Application
import com.msa.msahub.background.scheduler.WorkScheduler
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.di.KoinInitializer
import com.msa.msahub.core.platform.database.DatabaseInitializer
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionManager
import com.msa.msahub.features.automation.domain.AutomationEngine
import com.msa.msahub.features.devices.data.remote.mqtt.MqttIngestor
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import timber.log.Timber

object AppInitializer : KoinComponent {

    private val logger: Logger by inject()

    fun init(app: Application) {
        initLogging(app)
        KoinInitializer.init(app)

        runCatching {
            // ۱. مقداردهی اولیه دیتابیس (Seed)
            get<DatabaseInitializer>().seedIfNeeded()

            // ۲. مدیریت اتصال MQTT
            get<MqttConnectionManager>().start()

            // ۳. شروع Ingestor برای دریافت وضعیت دستگاه‌ها
            get<MqttIngestor>().start()

            // ۳.۵ ✅ شروع موتور اتوماسیون (مبتنی بر incomingMessages)
            get<AutomationEngine>().start()

            // ۴. زمان‌بندی کارهای پس‌زمینه (WorkManager)
            get<WorkScheduler>().scheduleAll()
        }.onFailure { e ->
            // ❗ در Release: بهتره به Crashlytics/Sentry گزارش بدی (اگر داری)
            logger.e("AppInitializer failed. Running in degraded mode.", e)
            // در حالت degraded حداقل WorkScheduler یا MQTT رو استارت نکن که loop کرش نشه
        }
    }

    private fun initLogging(app: Application) {
        if (com.msa.msahub.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // فقط WARN/ERROR
                    if (priority < android.util.Log.WARN) return
                    // پیام‌های حساس را sanitize کن
                    val safe = message
                        .replace(Regex("Bearer\\s+[A-Za-z0-9\\-\\._]+"), "Bearer ***")
                        .replace(Regex("password\\s*=\\s*\\S+", RegexOption.IGNORE_CASE), "password=***")
                    android.util.Log.println(priority, tag ?: "MSA-HUB", safe)
                }
            })
        }
    }
}
