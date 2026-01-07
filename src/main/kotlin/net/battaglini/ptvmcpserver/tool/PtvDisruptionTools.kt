package net.battaglini.ptvmcpserver.tool

import kotlinx.coroutines.runBlocking
import net.battaglini.ptvmcpserver.client.PtvClient
import net.battaglini.ptvmcpserver.dto.PtvDisruptionsResponseDto
import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Service

@Service
class PtvDisruptionTools(
    private val ptvClient: PtvClient,
) {
    @Tool(
        name = "ptv-disruptions-on-route",
        description = "Get all the disruptions affecting a specific route"
    )
    fun getDisruptionsOnRoute(routeId: Int): String {
        val disruptions: List<PtvDisruptionsResponseDto.Companion.Disruption> = runBlocking {
            val disruptions = ptvClient.getRouteDisruptions(routeId, null).disruptions ?: return@runBlocking emptyList()
            return@runBlocking disruptions.general
                ?.plus(disruptions.metroTrain)
                ?.plus(disruptions.metroTram)
                ?.plus(disruptions.metroBus)
                ?.plus(disruptions.regionalTrain)
                ?.plus(disruptions.regionalCoach)
                ?.plus(disruptions.regionalBus)
                ?.plus(disruptions.schoolBus)
                ?.plus(disruptions.telebus)
                ?.plus(disruptions.nightBus)
                ?.plus(disruptions.ferry)
                ?.plus(disruptions.interstateTrain)
                ?.plus(disruptions.skybus)
                ?.plus(disruptions.taxi) as List<PtvDisruptionsResponseDto.Companion.Disruption>
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
    }
}