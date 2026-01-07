package net.battaglini.ptvmcpserver.configuration

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class AppConfiguration {
    @Bean
    fun cacheManager(): CacheManager {
        return CaffeineCacheManager()
    }
}