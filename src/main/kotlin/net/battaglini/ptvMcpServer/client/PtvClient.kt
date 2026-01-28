package net.battaglini.ptvMcpServer.client

import au.gov.vic.ptv.timetableapi.model.*
import jakarta.annotation.PostConstruct
import net.battaglini.ptvMcpServer.configuration.CacheConfiguration.Companion.PTV_DEPARTURES_CACHE_NAME
import net.battaglini.ptvMcpServer.configuration.CacheConfiguration.Companion.PTV_DIRECTIONS_CACHE_NAME
import net.battaglini.ptvMcpServer.configuration.CacheConfiguration.Companion.PTV_DISRUPTIONS_CACHE_NAME
import net.battaglini.ptvMcpServer.configuration.CacheConfiguration.Companion.PTV_ROUTES_CACHE_NAME
import net.battaglini.ptvMcpServer.configuration.CacheConfiguration.Companion.PTV_ROUTE_CACHE_NAME
import net.battaglini.ptvMcpServer.configuration.CacheConfiguration.Companion.PTV_STOPS_CACHE_NAME
import net.battaglini.ptvMcpServer.configuration.PtvClientProperties
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilderFactory
import java.net.URI
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class PtvClient(
    private val ptvClientProperties: PtvClientProperties,
) {
    private val ptvWebClient = WebClient.create()
    private lateinit var uriBuilderFactory: UriBuilderFactory

    @PostConstruct
    fun init() {
        uriBuilderFactory = DefaultUriBuilderFactory(ptvClientProperties.baseUrl)
    }

    @Cacheable(PTV_ROUTES_CACHE_NAME)
    suspend fun getRoutes(): V3RoutesResponse? {
        LOGGER.debug("Getting all PTV routes...")

        val uri = buildRequestURI("/{apiVersion}/routes", emptyMap(), ptvClientProperties.apiVersion)
        val response = ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBodyOrNull<V3RoutesResponse>()
        LOGGER.debug("Received response from PTV routes endpoint. response={}", response)
        return response
    }

    @Cacheable(PTV_ROUTE_CACHE_NAME)
    suspend fun getRoute(routeId: Int): V3RouteResponse? {
        LOGGER.debug("Getting info for route $routeId...")

        val uri = buildRequestURI("/{apiVersion}/routes/{routeId}", emptyMap(), ptvClientProperties.apiVersion, routeId)

        val response = ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBodyOrNull<V3RouteResponse>()

        LOGGER.debug("Received response from PTV route endpoint. response={}", response)
        return response
    }

    @Cacheable(PTV_STOPS_CACHE_NAME)
    suspend fun getStops(routeId: Int, routeType: Int): V3StopsOnRouteResponse {
        LOGGER.debug("Getting all stops for route=$routeId, routeType=$routeType...")

        val uri = buildRequestURI(
            "/{apiVersion}/stops/route/{routeId}/route_type/{routeType}",
            emptyMap(),
            ptvClientProperties.apiVersion,
            routeId,
            routeType
        )

        val response = ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBody<V3StopsOnRouteResponse>()

        LOGGER.debug("Received response from PTV stops on route endpoint. response={}", response)
        return response
    }

    @Cacheable(PTV_DISRUPTIONS_CACHE_NAME)
    suspend fun getRouteDisruptions(routeId: Int, stopId: Int?): V3DisruptionsResponse {
        LOGGER.debug("Getting disruptions for route=$routeId, stopId=$stopId...")

        val uri = stopId?.let {
            buildRequestURI(
                "/{apiVersion}/disruptions/route/{route_id}/stop/{stop_id}",
                emptyMap(),
                ptvClientProperties.apiVersion,
                routeId,
                stopId
            )
        } ?: buildRequestURI(
            "/{apiVersion}/disruptions/route/{route_id}",
            emptyMap(),
            ptvClientProperties.apiVersion,
            routeId
        )

        val response = ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBody<V3DisruptionsResponse>()

        LOGGER.debug("Received response from PTV disruptions on route endpoint. response={}", response)
        return response
    }

    @Cacheable(PTV_DEPARTURES_CACHE_NAME)
    suspend fun getDeparturesForStopAndRouteType(
        stopId: Int,
        routeType: String,
        maxResults: Int = 6
    ): V3DeparturesResponse {
        LOGGER.debug("Getting departures for stop=$stopId, routeType=$routeType")
        val uri = buildRequestURI(
            "/{apiVersion}/departures/route_type/{routeType}/stop/{stopId}",
            mapOf("max_results" to maxResults.toString()),
            ptvClientProperties.apiVersion,
            routeType,
            stopId
        )
        val response = ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBody<V3DeparturesResponse>()

        LOGGER.debug("Received response from PTV departures on route endpoint. response={}", response)
        return response
    }

    @Cacheable(PTV_DIRECTIONS_CACHE_NAME)
    suspend fun getDirectionsForRoute(routeId: Int): V3DirectionsResponse {
        LOGGER.debug("Getting directions for route=$routeId")
        val uri = buildRequestURI(
            "/{apiVersion}/directions/route/{routeId}",
            emptyMap(),
            ptvClientProperties.apiVersion,
            routeId
        )
        val response = ptvWebClient.get()
            .uri(uri)
            .retrieve()
            .awaitBody<V3DirectionsResponse>()

        LOGGER.debug("Received response from PTV directions for route. response={}", routeId)
        return response
    }

    internal fun buildRequestURI(path: String, extraQueryParams: Map<String, String>, vararg uriVariables: Any): URI {
        val builder = uriBuilderFactory.builder()

        val tempUri = builder.path(path)
            .queryParam(DEVID_QUERY_PARAM_KEY, ptvClientProperties.username)
            .build(*uriVariables)

        val signature = calculateHMACSignature(tempUri)

        return builder
            .queryParam(SIGNATURE_QUERY_PARAM_KEY, signature)
            .build(*uriVariables)
    }

    internal fun calculateHMACSignature(uri: URI): String {
        // Remove the baseUrl from the string
        val uriBytes = ptvClientProperties.baseUrl.last().let { lastChar ->
            if (lastChar == '/') {
                return@let uri.toString().replace(ptvClientProperties.baseUrl, "/").byteInputStream().readBytes()
            }
            return@let uri.toString().replace(ptvClientProperties.baseUrl, "").byteInputStream().readBytes()
        }
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