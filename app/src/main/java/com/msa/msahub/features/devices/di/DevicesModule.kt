package com.msa.msahub.features.devices.di

import com.msa.msahub.core.platform.database.AppDatabase
import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.features.devices.data.local.dao.DeviceDao
import com.msa.msahub.features.devices.data.local.dao.DeviceHistoryDao
import com.msa.msahub.features.devices.data.local.dao.DeviceStateDao
import com.msa.msahub.features.devices.data.local.dao.OfflineCommandDao
import com.msa.msahub.features.devices.data.mapper.DeviceCommandMapper
import com.msa.msahub.features.devices.data.mapper.DeviceMapper
import com.msa.msahub.features.devices.data.mapper.DeviceStateMapper
import com.msa.msahub.features.devices.data.remote.api.DeviceApiService
import com.msa.msahub.features.devices.data.remote.api.FakeDeviceApiService
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import com.msa.msahub.features.devices.data.repository.DeviceRepositoryImpl
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import com.msa.msahub.features.devices.domain.usecase.GetDeviceDetailUseCase
import com.msa.msahub.features.devices.domain.usecase.GetDeviceHistoryUseCase
import com.msa.msahub.features.devices.domain.usecase.GetDevicesUseCase
import com.msa.msahub.features.devices.domain.usecase.ObserveDeviceStateUseCase
import com.msa.msahub.features.devices.domain.usecase.SendDeviceCommandUseCase
import com.msa.msahub.features.devices.presentation.viewmodel.DeviceDetailViewModel
import com.msa.msahub.features.devices.presentation.viewmodel.DeviceHistoryViewModel
import com.msa.msahub.features.devices.presentation.viewmodel.DeviceListViewModel
import com.msa.msahub.features.devices.presentation.viewmodel.DeviceSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object DevicesModule {
    val module = module {

        // --- DAO bindings (from AppDatabase) ---
        single<DeviceDao> { get<AppDatabase>().deviceDao() }
        single<DeviceStateDao> { get<AppDatabase>().deviceStateDao() }
        single<OfflineCommandDao> { get<AppDatabase>().offlineCommandDao() }
        single<DeviceHistoryDao> { get<AppDatabase>().deviceHistoryDao() }

        // --- Mappers ---
        single { DeviceMapper() }
        single { DeviceStateMapper() }
        single { DeviceCommandMapper() }

        // --- Remote ---
        single<DeviceApiService> { FakeDeviceApiService() }
        single { DeviceMqttHandler(mqttClient = get<MqttClient>()) }

        // --- Repository ---
        single<DeviceRepository> {
            DeviceRepositoryImpl(
                deviceDao = get(),
                deviceStateDao = get(),
                offlineCommandDao = get(),
                deviceHistoryDao = get(),
                api = get(),
                mqttHandler = get(),
                deviceMapper = get(),
                deviceStateMapper = get(),
                commandMapper = get()
            )
        }

        // --- UseCases ---
        factory { GetDevicesUseCase(get()) }
        factory { GetDeviceDetailUseCase(get()) }
        factory { ObserveDeviceStateUseCase(get()) }
        factory { SendDeviceCommandUseCase(get()) }
        factory { GetDeviceHistoryUseCase(get()) }

        // --- ViewModels ---
        viewModel { DeviceListViewModel(get()) }
        viewModel { DeviceDetailViewModel(get(), get(), get()) }
        viewModel { DeviceHistoryViewModel(get()) }
        viewModel { DeviceSettingsViewModel() }
    }
}
