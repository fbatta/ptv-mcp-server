package net.battaglini.ptvMcpServer.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class ApplicationProperties(
    val baseUrl: String,
    val oauthAuthorizationServer: AuthorizationServerProperties
) {
    companion object {
        @ConfigurationProperties(prefix = "app.oauth-authorization-server")
        data class AuthorizationServerProperties(
            val authorizationServerUrl: String,
            val scopes: List<String>
        )
    }
}
