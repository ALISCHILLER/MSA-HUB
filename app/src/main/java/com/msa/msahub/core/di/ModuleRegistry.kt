package com.msa.msahub.core.di

import org.koin.core.module.Module
import org.koin.dsl.module

object ModuleRegistry {
    val allModules: List<Module> = listOf(
        CoreModule.module,
        PlatformModule.module,
        NetworkModule.module,
        DatabaseModule.module,
        SecurityModule.module,
        BackgroundModule.module,
        AnalyticsModule.module,
        // Feature modules will be added here
    )
}

object CoreModule {
    val module = module {
        // TODO: Bind common singletons
    }
}

object PlatformModule {
    val module = module {
        // TODO: Bind platform impls
    }
}

object NetworkModule {
    val module = module {
        // TODO: Bind Ktor & Mqtt
    }
}

object DatabaseModule {
    val module = module {
        // TODO: Bind Room
    }
}

object SecurityModule {
    val module = module {
        // TODO: Bind Security
    }
}

object BackgroundModule {
    val module = module {
        // TODO: Bind Workers
    }
}

object AnalyticsModule {
    val module = module {
        // TODO: Bind Analytics
    }
}
