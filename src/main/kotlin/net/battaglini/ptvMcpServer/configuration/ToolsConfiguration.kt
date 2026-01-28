package net.battaglini.ptvMcpServer.configuration

import net.battaglini.ptvMcpServer.tool.PtvDeparturesTools
import net.battaglini.ptvMcpServer.tool.PtvDisruptionTools
import net.battaglini.ptvMcpServer.tool.PtvRouteTools
import net.battaglini.ptvMcpServer.tool.PtvStopTools
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ToolsConfiguration {
    @Bean
    fun toolCallbackProvider(
        ptvRouteTools: PtvRouteTools,
        ptvStopTools: PtvStopTools,
        ptvDisruptionTools: PtvDisruptionTools,
        ptvDeparturesTools: PtvDeparturesTools
    ): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder().toolObjects(
            ptvRouteTools,
            ptvStopTools,
            ptvDisruptionTools,
            ptvDeparturesTools,
        ).build()
    }
}