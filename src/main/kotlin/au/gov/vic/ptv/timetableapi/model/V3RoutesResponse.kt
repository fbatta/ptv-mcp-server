package au.gov.vic.ptv.timetableapi.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class V3RoutesResponse(
    @SerialName(value = "routes")
    val routes: List<V3RouteWithStatus>? = null,

    @SerialName(value = "status")
    val status: V3Status? = null
)
