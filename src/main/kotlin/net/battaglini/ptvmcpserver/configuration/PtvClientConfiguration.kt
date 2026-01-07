package net.battaglini.ptvmcpserver.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class PtvClientConfiguration {
    private val ptvClientProperties: PtvClientProperties

    constructor(ptvClientProperties: PtvClientProperties) {
        this.ptvClientProperties = ptvClientProperties
    }

    @Bean
    fun ptvWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(ptvClientProperties.baseUrl)
            .build()
    }
}