package net.battaglini.ptvMcpServer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("net.battaglini.ptvMcpServer.configuration")

class PtvMcpServerApplication

fun main(args: Array<String>) {
    runApplication<PtvMcpServerApplication>(*args)
}
