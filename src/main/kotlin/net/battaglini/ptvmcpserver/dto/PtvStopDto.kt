package net.battaglini.ptvmcpserver.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import net.battaglini.ptvmcpserver.enums.PtvRouteType

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PtvStopDto(
    @SerialName("stop_suburb")
    val stopSuburb: String? = null,
    @SerialName("route_type")
    val routeType: PtvRouteType? = null,
    @SerialName("stop_latitude")
    val stopLatitude: Double? = null,
    @SerialName("stop_longitude")
    val stopLongitude: Double? = null,
    @SerialName("stop_sequence")
    val stopSequence: Int? = null,
    @SerialName("stop_id")
    val stopId: Int? = null,
    @SerialName("stop_name")
    val stopName: String? = null,
    @SerialName("stop_landmark")
    val stopLandmark: String? = null,
)
