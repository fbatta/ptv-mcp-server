package net.battaglini.ptvmcpserver.client

import net.battaglini.ptvmcpserver.configuration.PtvClientProperties
import net.battaglini.ptvmcpserver.dto.PtvDisruptionsResponseDto
import net.battaglini.ptvmcpserver.dto.PtvRouteResponseDto
import net.battaglini.ptvmcpserver.dto.PtvRouteStopsResponseDto
import net.battaglini.ptvmcpserver.dto.PtvRoutesResponseDto
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.net.URI
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class PtvClient(
    private val ptvClientProperties: PtvClientProperties,
    private val ptvWebClient: WebClient,
) {
    @Cacheable("ptvRoutes")
    suspend fun getRoutes(): PtvRoutesResponseDto {
        LOGGER.debug("Getting all PTV routes...")
        return ptvWebClient.get()
            .uri { uriBuilder ->
                val uri = uriBuilder.path("/${ptvClientProperties.apiVersion}/routes")
                    .queryParam(DEVID_QUERY_PARAM_KEY, ptvClientProperties.username)
                    .build()
                val signature = calculateHMACSignature(uri)
                return@uri uriBuilder
                    .queryParam(SIGNATURE_QUERY_PARAM_KEY, signature)
                    .build()
            }
            .retrieve()
            .awaitBody<PtvRoutesResponseDto>()
    }

    @Cacheable("ptvRoute")
    suspend fun getRoute(routeId: Int): PtvRouteResponseDto {
        LOGGER.debug("Getting info for route $routeId...")
        return ptvWebClient.get()
            .uri { uriBuilder ->
                val uri = uriBuilder.path("/${ptvClientProperties.apiVersion}/routes/{routeId}")
                    .queryParam(DEVID_QUERY_PARAM_KEY, ptvClientProperties.username)
                    .build(routeId)
                val signature = calculateHMACSignature(uri)
                return@uri uriBuilder
                    .queryParam(SIGNATURE_QUERY_PARAM_KEY, signature)
                    .build(routeId)
            }
            .retrieve()
            .awaitBody<PtvRouteResponseDto>()
    }

    @Cacheable("ptvStops")
    suspend fun getStops(routeId: Int, routeType: Int): PtvRouteStopsResponseDto {
        LOGGER.debug("Getting all stops for route=$routeId, routeType=$routeType...")
        return ptvWebClient.get()
            .uri { uriBuilder ->
                val uri =
                    uriBuilder.path("/${ptvClientProperties.apiVersion}/stops/route/{routeId}/route_type/{routeType}")
                        .queryParam(DEVID_QUERY_PARAM_KEY, ptvClientProperties.username)
                        .build(routeId, routeType)
                val signature = calculateHMACSignature(uri)
                return@uri uriBuilder
                    .queryParam(SIGNATURE_QUERY_PARAM_KEY, signature)
                    .build(routeId, routeType)
            }
            .retrieve()
            .awaitBody<PtvRouteStopsResponseDto>()
    }

    @Cacheable("ptvRouteDisruptions")
    suspend fun getRouteDisruptions(routeId: Int, stopId: Int?): PtvDisruptionsResponseDto {
        LOGGER.debug("Getting disruptions for route=$routeId, stopId=$stopId...")
        return ptvWebClient.get()
            .uri { uriBuilder ->
                var uri: URI;
                if (stopId != null) {
                    uri =
                        uriBuilder.path("/${ptvClientProperties.apiVersion}/disruptions/route/{route_id}/stop/{stop_id}")
                            .queryParam(DEVID_QUERY_PARAM_KEY, ptvClientProperties.username)
                            .build(routeId, stopId)
                } else {
                    uri = uriBuilder.path("/${ptvClientProperties.apiVersion}/disruptions/route/{route_id}")
                        .queryParam(DEVID_QUERY_PARAM_KEY, ptvClientProperties.username)
                        .build(routeId)
                }
                val signature = calculateHMACSignature(uri)
                uriBuilder
                    .queryParam(SIGNATURE_QUERY_PARAM_KEY, signature)
                if (stopId != null) {
                    return@uri uriBuilder
                        .build(routeId, stopId)
                }
                return@uri uriBuilder
                    .build(routeId)
            }
            .retrieve()
            .awaitBody<PtvDisruptionsResponseDto>()
    }

    internal fun calculateHMACSignature(uri: URI): String {
        // Remove the baseUrl from the string
        val uriBytes = uri.toString().replace(ptvClientProperties.baseUrl, "/").byteInputStream().readBytes();
        val keyBytes = ptvClientProperties.password.byteInputStream().readBytes();

        val signingKey = SecretKeySpec(keyBytes, HMAC_SHA1_ALGORITHM)
        val mac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
        mac.init(signingKey)
        val signatureBytes = mac.doFinal(uriBytes)
        val signature = StringBuffer()
        for (signatureByte in signatureBytes) {
            val intVal = signatureByte.toInt().and(0xff)
            if (intVal < 0x10) {
                signature.append("0")
            }
            signature.append(Integer.toHexString(intVal))
        }
        return signature.toString().uppercase(Locale.getDefault())
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PtvClient::class.java)
        internal const val HMAC_SHA1_ALGORITHM = "HmacSHA1"
        internal const val DEVID_QUERY_PARAM_KEY = "devid"
        internal const val SIGNATURE_QUERY_PARAM_KEY = "signature"
    }
}