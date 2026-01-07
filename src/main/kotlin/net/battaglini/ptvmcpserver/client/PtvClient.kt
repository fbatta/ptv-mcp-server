package net.battaglini.ptvmcpserver.client

import jakarta.annotation.PostConstruct
import net.battaglini.ptvmcpserver.configuration.CacheConfiguration.Companion.PTV_DISRUPTIONS_CACHE_NAME
import net.battaglini.ptvmcpserver.configuration.CacheConfiguration.Companion.PTV_ROUTES_CACHE_NAME
import net.battaglini.ptvmcpserver.configuration.CacheConfiguration.Companion.PTV_ROUTE_CACHE_NAME
import net.battaglini.ptvmcpserver.configuration.CacheConfiguration.Companion.PTV_STOPS_CACHE_NAME
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
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilderFactory
import java.net.URI
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class PtvClient(
    private val ptvClientProperties: PtvClientProperties,
    private val ptvWebClient: WebClient,
) {
    private lateinit var uriBuilderFactory: UriBuilderFactory

    @PostConstruct
    fun init() {
        uriBuilderFactory = DefaultUriBuilderFactory(ptvClientProperties.baseUrl)
    }

    @Cacheable(PTV_ROUTES_CACHE_NAME)
    suspend fun getRoutes(): PtvRoutesResponseDto {
        LOGGER.debug("Getting all PTV routes...")

        val uri = buildRequestURI("/{apiVersion}/routes", ptvClientProperties.apiVersion)

        return ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBody<PtvRoutesResponseDto>()
    }

    @Cacheable(PTV_ROUTE_CACHE_NAME)
    suspend fun getRoute(routeId: Int): PtvRouteResponseDto {
        LOGGER.debug("Getting info for route $routeId...")

        val uri = buildRequestURI("/{apiVersion}/routes/{routeId}", ptvClientProperties.apiVersion, routeId)

        return ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBody<PtvRouteResponseDto>()
    }

    @Cacheable(PTV_STOPS_CACHE_NAME)
    suspend fun getStops(routeId: Int, routeType: Int): PtvRouteStopsResponseDto {
        LOGGER.debug("Getting all stops for route=$routeId, routeType=$routeType...")

        val uri = buildRequestURI(
            "/{apiVersion}/stops/route/{routeId}/route_type/{routeType}",
            ptvClientProperties.apiVersion,
            routeId,
            routeType
        )

        return ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBody<PtvRouteStopsResponseDto>()
    }

    @Cacheable(PTV_DISRUPTIONS_CACHE_NAME)
    suspend fun getRouteDisruptions(routeId: Int, stopId: Int?): PtvDisruptionsResponseDto {
        LOGGER.debug("Getting disruptions for route=$routeId, stopId=$stopId...")

        val uri = stopId?.let {
            buildRequestURI(
                "/{apiVersion}/disruptions/route/{route_id}/stop/{stop_id}",
                ptvClientProperties.apiVersion,
                routeId,
                stopId
            )
        } ?: buildRequestURI("/{apiVersion}/disruptions/route/{route_id}", ptvClientProperties.apiVersion, routeId)

        return ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBody<PtvDisruptionsResponseDto>()
    }

    internal fun buildRequestURI(path: String, vararg uriVariables: Any): URI {
        val builder = uriBuilderFactory.builder()

        val tempUri = builder.path(path)
            .queryParam(DEVID_QUERY_PARAM_KEY, ptvClientProperties.username)
            .build(uriVariables)

        val signature = calculateHMACSignature(tempUri)

        return builder
            .queryParam(SIGNATURE_QUERY_PARAM_KEY, signature)
            .build(uriVariables)
    }

    internal fun calculateHMACSignature(uri: URI): String {
        // Remove the baseUrl from the string
        val uriBytes = uri.toString().replace(ptvClientProperties.baseUrl, "/").byteInputStream().readBytes()
        val keyBytes = ptvClientProperties.password.byteInputStream().readBytes()

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