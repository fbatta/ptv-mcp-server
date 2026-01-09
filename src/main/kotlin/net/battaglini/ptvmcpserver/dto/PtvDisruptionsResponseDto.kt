package net.battaglini.ptvmcpserver.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class PtvDisruptionsResponseDto(
    val disruptions: Disruptions? = null,
) {
    companion object {
        @Serializable
        @JsonIgnoreUnknownKeys
        data class Disruptions(
            val general: List<Disruption>? = null,
            @SerialName("metro_train")
            val metroTrain: List<Disruption>? = null,
            @SerialName("metro_tram")
            val metroTram: List<Disruption>? = null,
            @SerialName("metro_bus")
            val metroBus: List<Disruption>? = null,
            @SerialName("regional_train")
            val regionalTrain: List<Disruption>? = null,
            @SerialName("regional_coach")
            val regionalCoach: List<Disruption>? = null,
            @SerialName("regional_bus")
            val regionalBus: List<Disruption>? = null,
            @SerialName("school_bus")
            val schoolBus: List<Disruption>? = null,
            val telebus: List<Disruption>? = null,
            @SerialName("night_bus")
            val nightBus: List<Disruption>? = null,
            val ferry: List<Disruption>? = null,
            @SerialName("interstate_train")
            val interstateTrain: List<Disruption>? = null,
            val skybus: List<Disruption>? = null,
            val taxi: List<Disruption>? = null
        )

        @Serializable
        @JsonIgnoreUnknownKeys
        data class Disruption(
            @SerialName("disruption_id")
            val disruptionId: Int? = null,
            val title: String? = null,
            val url: String? = null,
            val description: String? = null,
            @SerialName("disruption_status")
            val disruptionStatus: String? = null,
            @SerialName("disruption_type")
            val disruptionType: String? = null,
            @SerialName("from_date")
            val fromDate: String? = null,
            @SerialName("to_date")
            val toDate: String? = null,
            val routes: List<DisruptionRoute>? = null,
            val stops: List<DisruptionStop>? = null,
        )

        @Serializable
        @JsonIgnoreUnknownKeys
        data class DisruptionRoute(
            @SerialName("route_type")
            val routeType: Int? = null,
            @SerialName("route_id")
            val routeId: Int? = null,
            @SerialName("route_name")
            val routeName: String? = null,
            @SerialName("route_number")
            val routeNumber: String? = null,
        )

        @Serializable
        @JsonIgnoreUnknownKeys
        data class DisruptionDirection(
            @SerialName("route_direction_id")
            val routeDirectionId: Int? = null,
            @SerialName("direction_id")
            val directionId: Int? = null,
            @SerialName("direction_name")
            val directionName: String? = null,
            @SerialName("service_name")
            val serviceName: String? = null,
        )

        @Serializable
        @JsonIgnoreUnknownKeys
        data class DisruptionStop(
            @SerialName("stop_id")
            val stopId: Int? = null,
            @SerialName("stop_name")
            val stopName: String? = null,
        )
    }
}
