package com.msa.msahub.app

import android.app.Application
import com.msa.msahub.background.scheduler.WorkScheduler
import com.msa.msahub.core.common.Logger
import com.msa.msahub.core.di.AppScopeModule
import com.msa.msahub.core.di.KoinInitializer
import com.msa.msahub.core.platform.database.DatabaseInitializer
import com.msa.msahub.core.platform.network.mqtt.MqttConnectionManager
import com.msa.msahub.features.automation.domain.AutomationEngine
import com.msa.msahub.features.devices.data.remote.mqtt.MqttIngestor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import timber.log.Timber

object AppInitializer : KoinComponent {

    private val logger: Logger by inject()

    fun init(app: Application) {
        initLogging(app)
        
        // ۱. ساخت گراف DI (باید سریع و روی رشته اصلی باشد)
        KoinInitializer.init(app)

        // ۲. اجرای کارهای سنگین در پس‌زمینه برای جلوگیری از ANR
        val appScope = get<CoroutineScope>(AppScopeModule.APP_SCOPE)
        
        appScope.launch {
            // هر سرویس جداگانه مدیریت می‌شود تا خطای یکی باعث توقف بقیه نشود
            
            launch {
                runCatching {
                    get<DatabaseInitializer>().seedIfNeeded()
                }.onFailure { logger.e("Database seeding failed", it) }
            }

            launch {
                runCatching {
                    get<MqttConnectionManager>().start()
                }.onFailure { logger.e("MqttConnectionManager start failed", it) }
            }

            launch {
                runCatching {
                    get<MqttIngestor>().start()
                }.onFailure { logger.e("MqttIngestor start failed", it) }
            }

            launch {
                runCatching {
                    get<AutomationEngine>().start()
                }.onFailure { logger.e("AutomationEngine start failed", it) }
            }

            launch {
                runCatching {
                    get<WorkScheduler>().scheduleAll()
                }.onFailure { logger.e("WorkScheduler failed", it) }
            }
            
            logger.i("AppInitializer: Background services initialization triggered.")
        }
    }

    private fun initLogging(app: Application) {
        if (com.msa.msahub.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority < android.util.Log.WARN) return
                    val safe = message
                        .replace(Regex("Bearer\\s+[A-Za-z0-9\\-\\._]+"), "Bearer ***")
                        .replace(Regex("password\\s*=\\s*\\S+", RegexOption.IGNORE_CASE), "password=***")
                    android.util.Log.println(priority, tag ?: "MSA-HUB", safe)
                }
            })
        }
    }
}
