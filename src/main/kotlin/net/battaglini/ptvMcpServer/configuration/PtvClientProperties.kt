package net.battaglini.ptvMcpServer.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "clients.ptv")
class PtvClientProperties(
    val baseUrl: String,
    val apiVersion: String,
    val username: String,
    val password: String,
)