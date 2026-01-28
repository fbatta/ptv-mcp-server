package net.battaglini.ptvMcpServer.tool

import kotlinx.coroutines.runBlocking
import net.battaglini.ptvMcpServer.client.PtvClient
import net.battaglini.ptvMcpServer.enums.PtvRouteType
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("isAuthenticated()")
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
        val routes = runBlocking { ptvClient.getRoutes()?.routes }
        val route = routes?.find { it.routeNumber == routeNumber }
        if (route == null) {
            return "Could not find route $routeNumber"
        }
        return """
            Route ID: ${route.routeId}
            Route type: ${PtvRouteType[route.routeType ?: PtvRouteType.Unknown.id]?.name} (${PtvRouteType[route.routeType ?: PtvRouteType.Unknown.id]?.id})
            Route number: ${route.routeNumber}
            Route name: ${route.routeName}
            ${route.routeServiceStatus?.let { "Service status: ${it.description}" }}
        """.trimIndent()
    }
}