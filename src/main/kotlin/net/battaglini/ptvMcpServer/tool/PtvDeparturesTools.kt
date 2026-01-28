package net.battaglini.ptvMcpServer.tool

import au.gov.vic.ptv.timetableapi.model.V3DirectionWithDescription
import kotlinx.coroutines.runBlocking
import net.battaglini.ptvMcpServer.client.PtvClient
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("isAuthenticated()")
class PtvDeparturesTools(
    val ptvClient: PtvClient
) {
    @Tool(
        name = "ptv-departures-for-stop",
        description = "Returns a list of public transport departures for a specific stop id and route type"
    )
    fun getDeparturesForStop(
        @ToolParam(description = "The stop id", required = true) stopId: Int,
        @ToolParam(description = "The route type", required = true) routeType: String,
    ): String {
        val departures = runBlocking { ptvClient.getDeparturesForStopAndRouteType(stopId, routeType) }.departures

        if (departures.isNullOrEmpty()) {
            return "No departures found for stop $stopId and route type $routeType"
        }

        val routeIds = departures.map { departure -> departure.routeId!! }.toSet()
        val directions = mutableMapOf<String, V3DirectionWithDescription>()
        for (routeId in routeIds) {
            val routeDirections = runBlocking { ptvClient.getDirectionsForRoute(routeId) }.directions
            routeDirections?.forEach { direction ->
                directions[getDirectionMapKey(routeId, direction.directionId!!)] = direction
            }
        }

        var response = ""
        for (departure in departures) {
            response += """
                Route ID: ${departure.routeId}
                Direction ID: ${departure.directionId}
                Direction name: ${
                directions[getDirectionMapKey(
                    departure.routeId!!,
                    departure.directionId!!
                )]?.directionName
            }
                Scheduled time: ${departure.scheduledDepartureUtc}
                ${departure.platformNumber?.let { platformNumber -> "Platform: $platformNumber" }}
                \n
            """.trimIndent()
        }
        return response
    }

    private fun getDirectionMapKey(routeId: Int, directionId: Int) = "$routeId-$directionId"
}