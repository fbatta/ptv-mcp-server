package net.battaglini.ptvmcpserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("net.battaglini.ptvmcpserver.configuration")
class PtvMcpServerApplication

fun main(args: Array<String>) {
    runApplication<PtvMcpServerApplication>(*args)
}
