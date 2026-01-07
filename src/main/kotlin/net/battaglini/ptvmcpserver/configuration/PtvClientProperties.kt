package net.battaglini.ptvmcpserver.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "clients.ptv")
class PtvClientProperties(
    val baseUrl: String,
    val apiVersion: String,
    val username: String,
    val password: String,
)