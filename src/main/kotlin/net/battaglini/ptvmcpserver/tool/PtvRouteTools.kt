package net.battaglini.ptvmcpserver.tool

import kotlinx.coroutines.runBlocking
import net.battaglini.ptvmcpserver.client.PtvClient
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.stereotype.Service

@Service
class PtvRouteTools(
    val ptvClient: PtvClient
) {
    @Tool(
        name = "ptv-route-information",
        description = "Provides information about a PTV train/tram/bus/V Line/night bus route"
    )
    fun getRouteInformation(
        @ToolParam(description = "Route number", required = true) routeNumber: String
    ): String {
        val routes = runBlocking { ptvClient.getRoutes().routes }
        val route = routes.find { it.routeNumber == routeNumber }
        if (route == null) {
            return "Could not find route $routeNumber"
        }
        return """
            Route ID: ${route.routeId}
            Route type: ${route.routeType?.name} (${route.routeType?.id})
            Route number: ${route.routeNumber}
            Route name: ${route.routeName}
            Service status: ${route.routeServiceStatus.description}
        """.trimIndent()
    }
}