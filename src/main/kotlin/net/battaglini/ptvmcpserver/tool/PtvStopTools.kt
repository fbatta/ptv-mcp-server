package net.battaglini.ptvmcpserver.tool

import kotlinx.coroutines.runBlocking
import net.battaglini.ptvmcpserver.client.PtvClient
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service

@Service
class PtvStopTools(
    val ptvClient: PtvClient,
) {
    @Tool(
        name = "ptv-stops-for-route",
        description = "Returns all the stops for a given PTV route"
    )
    fun getStops(
        @ToolParam(
            description = "The ID of the route",
            required = true
        ) routeId: Int,
        @ToolParam(
            description = "The type of the route",
            required = true,
        ) routeType: Int,
    ): String {
        val stops = runBlocking { ptvClient.getStops(routeId, routeType).stops }
        if (stops == null) return "Could not find stops for route: $routeId"

        var response = ""
        for (stop in stops) {
            response += """
                Stop ID: ${stop.stopId}
                Stop name: ${stop.stopName}
                Stop suburb: ${stop.stopSuburb}
                Stop sequence: ${stop.stopSequence}
                Stop coordinates: ${stop.stopLatitude} - ${stop.stopLongitude}
                \n
            """.trimIndent()
        }
        return response
    }
}