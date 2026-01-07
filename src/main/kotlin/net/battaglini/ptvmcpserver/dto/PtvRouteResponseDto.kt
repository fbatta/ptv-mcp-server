package net.battaglini.ptvmcpserver.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PtvRouteResponseDto(
    val route: PtvRoutesResponseDto.Companion.PtvRouteDto? = null,
)
