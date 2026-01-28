package net.battaglini.ptvMcpServer.tool

import au.gov.vic.ptv.timetableapi.model.V3Disruption
import kotlinx.coroutines.runBlocking
import net.battaglini.ptvMcpServer.client.PtvClient
import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("isAuthenticated()")
class PtvDisruptionTools(
    private val ptvClient: PtvClient,
) {
    @Tool(
        name = "ptv-disruptions-on-route",
        description = "Get all the disruptions affecting a specific route"
    )
    fun getDisruptionsOnRoute(routeId: Int): String {
        return getDisruptions(routeId, null)
    }

    @Tool(
        name = "ptv-disruptions-on-route-for-stop",
        description = "Get all the disruptions affecting a specific route and stop"
    )
    fun getDisruptionsOnRouteForStop(routeId: Int, stopId: Int): String {
        return getDisruptions(routeId, stopId)
    }

    private fun getDisruptions(routeId: Int, stopId: Int?): String {
        try {
            val disruptions: List<V3Disruption> = runBlocking {
                val disruptions =
                    ptvClient.getRouteDisruptions(routeId, stopId).disruptions ?: return@runBlocking emptyList()
                val result = mutableListOf<V3Disruption>()
                disruptions.general?.also { result.addAll(it.asSequence()) }
                disruptions.metroTrain?.also { result.addAll(it.asSequence()) }
                disruptions.metroTram?.also { result.addAll(it.asSequence()) }
                disruptions.metroBus?.also { result.addAll(it.asSequence()) }
                disruptions.regionalTrain?.also { result.addAll(it.asSequence()) }
                disruptions.regionalCoach?.also { result.addAll(it.asSequence()) }
                disruptions.regionalBus?.also { result.addAll(it.asSequence()) }
                disruptions.schoolBus?.also { result.addAll(it.asSequence()) }
                disruptions.telebus?.also { result.addAll(it.asSequence()) }
                disruptions.nightBus?.also { result.addAll(it.asSequence()) }
                disruptions.ferry?.also { result.addAll(it.asSequence()) }
                disruptions.interstateTrain?.also { result.addAll(it.asSequence()) }
                disruptions.skybus?.also { result.addAll(it.asSequence()) }
                disruptions.taxi?.also { result.addAll(it.asSequence()) }

                return@runBlocking result.toList()
            }
            if (disruptions.isEmpty()) {
                return "No disruptions found on this route"
            }

            var result = ""
            for (disruption in disruptions) {
                result += """
                Disruption ID: ${disruption.disruptionId}
                Title: ${disruption.title}
                Description: ${disruption.description}
                Status: ${disruption.disruptionStatus}
                Type: ${disruption.disruptionType}
                Starts on: ${disruption.fromDate}
                Ends on: ${disruption.toDate}
                Affected routes: ${disruption.routes?.map { it.routeNumber }.orEmpty()}
                Affected stops: ${disruption.stops?.map { it.stopName }.orEmpty()}
                \n
            """.trimIndent()
            }
            return result
        } catch (ex: Exception) {
            LOGGER.error("Error occurred", ex)
            return "An error occurred, and I cannot retrieve the list of disruptions"
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PtvDisruptionTools::class.java)
    }
}