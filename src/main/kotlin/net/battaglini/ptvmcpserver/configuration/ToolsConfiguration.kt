package net.battaglini.ptvmcpserver.configuration

import net.battaglini.ptvmcpserver.tool.PtvRouteTools
import net.battaglini.ptvmcpserver.tool.PtvStopTools
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ToolsConfiguration {
    @Bean
    fun toolCallbackProvider(
        ptvRouteTools: PtvRouteTools,
        ptvStopTools: PtvStopTools
    ): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder().toolObjects(
            ptvRouteTools,
            ptvStopTools,
        ).build()
    }
}