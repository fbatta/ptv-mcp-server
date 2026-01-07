package net.battaglini.ptvmcpserver.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import net.battaglini.ptvmcpserver.enums.PtvRouteType

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PtvRoutesResponseDto(
    val routes: List<PtvRouteDto>
) {
    companion object {
        @Serializable
        @JsonIgnoreUnknownKeys
        data class PtvRouteDto(
            @SerialName("route_service_status")
            val routeServiceStatus: RouteServiceStatus,
            @SerialName("route_type")
            val routeType: PtvRouteType? = null,
            @SerialName("route_id")
            val routeId: Int? = null,
            @SerialName("route_name")
            val routeName: String? = null,
            @SerialName("route_number")
            val routeNumber: String? = null,
        ) {
            companion object {
                @OptIn(ExperimentalSerializationApi::class)
                @Serializable
                @JsonIgnoreUnknownKeys
                data class RouteServiceStatus(
                    val description: String? = null,
                    val timestamp: String? = null,
                )
            }
        }
    }
}
