package com.msa.msahub.features.devices.di

import com.msa.msahub.core.platform.network.mqtt.MqttClient
import com.msa.msahub.features.devices.data.mapper.DeviceCommandMapper
import com.msa.msahub.features.devices.data.mapper.DeviceMapper
import com.msa.msahub.features.devices.data.mapper.DeviceStateMapper
import com.msa.msahub.features.devices.data.remote.api.DeviceApiService
import com.msa.msahub.features.devices.data.remote.api.DeviceApiServiceImpl
import com.msa.msahub.features.devices.data.remote.mqtt.DeviceMqttHandler
import com.msa.msahub.features.devices.data.repository.DeviceRepositoryImpl
import com.msa.msahub.features.devices.data.sync.OfflineCommandOutbox
import com.msa.msahub.features.devices.domain.repository.DeviceRepository
import com.msa.msahub.features.devices.domain.usecase.FlushOfflineCommandsUseCase
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

        // --- Mappers ---
        single { DeviceMapper() }
        single { DeviceStateMapper() }
        single { DeviceCommandMapper() }

        // --- Remote ---
        single<DeviceApiService> { 
            DeviceApiServiceImpl(
                httpClient = get(),
                networkConfig = get(),
                mapper = get()
            )
        }
        single { DeviceMqttHandler(mqttClient = get<MqttClient>()) }

        // --- Outbox ---
        single { OfflineCommandOutbox(dao = get(), mqttHandler = get()) }

        // --- Repository ---
        single<DeviceRepository> {
            DeviceRepositoryImpl(
                deviceDao = get(),
                deviceStateDao = get(),
                offlineCommandDao = get(),
                deviceHistoryDao = get(),
                api = get(),
                mqttHandler = get(),
                outbox = get(),
                deviceMapper = get(),
                deviceStateMapper = get(),
                commandMapper = get()
            )
        }

        // --- UseCases ---
        factory { FlushOfflineCommandsUseCase(get()) }
        factory { GetDevicesUseCase(get()) }
        factory { GetDeviceDetailUseCase(get()) }
        factory { ObserveDeviceStateUseCase(get()) }
        factory { SendDeviceCommandUseCase(get()) }
        factory { GetDeviceHistoryUseCase(get()) }

        // --- ViewModels ---
        viewModel { DeviceListViewModel(get()) }
        viewModel { DeviceDetailViewModel(get(), get(), get()) }
        viewModel { DeviceHistoryViewModel(get()) }
        viewModel { DeviceSettingsViewModel(deviceDao = get()) }
    }
}
