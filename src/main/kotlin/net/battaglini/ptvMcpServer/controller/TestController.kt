package net.battaglini.ptvMcpServer.controller

import au.gov.vic.ptv.timetableapi.model.V3DisruptionsResponse
import au.gov.vic.ptv.timetableapi.model.V3RoutesResponse
import au.gov.vic.ptv.timetableapi.model.V3StopsOnRouteResponse
import net.battaglini.ptvMcpServer.client.PtvClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestController(
    val ptvClient: PtvClient
) {
    @GetMapping("/routes")
    suspend fun getRoutes(): V3RoutesResponse {
        return ptvClient.getRoutes() ?: V3RoutesResponse()
    }

    @GetMapping("/disruptions/{routeId}")
    suspend fun getDisruptionsForRoute(@PathVariable("routeId") routeId: Int): V3DisruptionsResponse {
        return ptvClient.getRouteDisruptions(routeId, null)
    }

    @GetMapping("/stops/{routeId}/{routeType}")
    suspend fun getStopsForRoute(
        @PathVariable("routeId") routeId: Int,
        @PathVariable("routeType") routeType: Int
    ): V3StopsOnRouteResponse {
        return ptvClient.getStops(routeId, routeType)
    }
}