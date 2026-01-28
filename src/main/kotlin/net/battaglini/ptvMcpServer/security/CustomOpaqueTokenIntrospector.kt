package net.battaglini.ptvMcpServer.security

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import net.battaglini.ptvMcpServer.model.GoogleOauthUserInfoResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@Component
class CustomOpaqueTokenIntrospector : OpaqueTokenIntrospector {
    private lateinit var rest: WebClient

    private val tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo"

    private val audience = "174877382451-fa9pn0mc4k0s6obp8il55s269f47t320.apps.googleusercontent.com";

    @PostConstruct
    fun init() {
        rest = WebClient.create(tokenInfoUrl)
    }

    override fun introspect(token: String?): OAuth2AuthenticatedPrincipal? {
        val response = runBlocking {
            rest.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .queryParam("access_token", token!!)
                        .build()
                }
                .retrieve()
                .awaitBody<GoogleOauthUserInfoResponse>()
        }
        LOGGER.debug("Received response from tokeninfo endpoint={}", response)

        if (isTokenExpired(response) || !isRightAudience(response)) {
            return null
        }

        return DefaultOAuth2AuthenticatedPrincipal(
            mapOf(
                Pair("aud", response.aud),
                Pair("exp", Instant.fromEpochSeconds(response.exp.toLong()).toJavaInstant()),
                Pair("scope", response.scope),
                Pair("azp", response.azp),
                Pair("email", response.email),
                Pair("sub", response.sub)
            ),
            listOf(
                GrantedAuthority { "ROLE_USER" }
            )
        )
    }

    private fun isTokenExpired(response: GoogleOauthUserInfoResponse): Boolean {
        val tokenExpiration = Instant.fromEpochSeconds(response.exp.toLongOrNull() ?: 0)
        return tokenExpiration.minus(Clock.System.now()).isNegative()
    }

    private fun isRightAudience(response: GoogleOauthUserInfoResponse): Boolean {
        return response.aud == audience
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(CustomOpaqueTokenIntrospector::class.java)
    }
}