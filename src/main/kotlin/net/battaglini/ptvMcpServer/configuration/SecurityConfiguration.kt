package net.battaglini.ptvMcpServer.configuration

import net.battaglini.ptvMcpServer.security.CustomOpaqueTokenIntrospector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    val customOpaqueTokenIntrospector: CustomOpaqueTokenIntrospector,
    val applicationProperties: ApplicationProperties
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { configurer ->
                configurer.requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/test/**").permitAll()
                    .requestMatchers("/mcp").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { resourceServer ->
                resourceServer
                    .opaqueToken { opaqueToken ->
                        opaqueToken.introspector { token ->
                            customOpaqueTokenIntrospector.introspect(token)
                        }
                    }
                    .protectedResourceMetadata {
                        it.protectedResourceMetadataCustomizer { customizer ->
                            customizer
                                .resource("${applicationProperties.baseUrl}/mcp")
                                .authorizationServer(applicationProperties.oauthAuthorizationServer.authorizationServerUrl)
                                .scopes { scopes ->
                                    applicationProperties.oauthAuthorizationServer.scopes.forEach { scope ->
                                        scopes.add(
                                            scope
                                        )
                                    }
                                }
                                .build()
                        }
                    }
            }
            .csrf { it.disable() }
            .build()
    }
}