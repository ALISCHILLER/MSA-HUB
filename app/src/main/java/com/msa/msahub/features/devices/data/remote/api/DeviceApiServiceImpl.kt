package com.msa.msahub.features.devices.data.remote.api

import com.msa.msahub.core.platform.network.http.NetworkConfig
import com.msa.msahub.features.devices.data.mapper.DeviceMapper
import com.msa.msahub.features.devices.data.remote.model.DeviceRemoteModel
import com.msa.msahub.features.devices.domain.model.Device
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class DeviceApiServiceImpl(
    private val httpClient: HttpClient,
    private val networkConfig: NetworkConfig,
    private val mapper: DeviceMapper
) : DeviceApiService {

    override suspend fun fetchDevices(): List<Device> {
        val remoteModels: List<DeviceRemoteModel> = httpClient.get {
            url("${networkConfig.baseUrl}/devices")
        }.body()
        
        return remoteModels.map { mapper.fromRemote(it) }
    }

    override suspend fun fetchDeviceDetail(deviceId: String): Device? {
        val remoteModel: DeviceRemoteModel = httpClient.get {
            url("${networkConfig.baseUrl}/devices/$deviceId")
        }.body()
        
        return mapper.fromRemote(remoteModel)
    }

    override suspend fun registerDevice(device: Device): Boolean {
        return runCatching {
            httpClient.post {
                url("${networkConfig.baseUrl}/devices/register")
                contentType(ContentType.Application.Json)
                setBody(mapper.toRemote(device))
            }.status.value in 200..299
        }.getOrDefault(false)
    }
}
