package net.battaglini.ptvMcpServer.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class GoogleOauthUserInfoResponse(
    val aud: String,
    val azp: String,
    val email: String,
    val exp: String,
    val scope: String,
    val sub: String
)