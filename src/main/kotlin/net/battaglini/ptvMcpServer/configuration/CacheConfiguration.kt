package net.battaglini.ptvMcpServer.configuration

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun cacheManager(): CacheManager {
        return CaffeineCacheManager()
    }

    @Bean
    fun ptvRoutesCache(): CaffeineCache {
        return CaffeineCache(
            PTV_ROUTES_CACHE_NAME,
            Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build()
        )
    }

    @Bean
    fun ptvRouteCache(): CaffeineCache {
        return CaffeineCache(
            PTV_ROUTE_CACHE_NAME,
            Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build()
        )
    }

    @Bean
    fun ptvStopsCache(): CaffeineCache {
        return CaffeineCache(
            PTV_STOPS_CACHE_NAME,
            Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build()
        )
    }

    @Bean
    fun ptvDisruptionsCache(): CaffeineCache {
        return CaffeineCache(
            PTV_DISRUPTIONS_CACHE_NAME,
            Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build()
        )
    }

    @Bean
    fun ptvDeparturesCache(): CaffeineCache {
        return CaffeineCache(
            PTV_DEPARTURES_CACHE_NAME,
            Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build()
        )
    }

    @Bean
    fun ptvDirectionsCache(): CaffeineCache {
        return CaffeineCache(
            PTV_DIRECTIONS_CACHE_NAME,
            Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build()
        )
    }

    companion object {
        const val PTV_ROUTES_CACHE_NAME = "ptvRoutes"
        const val PTV_ROUTE_CACHE_NAME = "ptvRoute"
        const val PTV_STOPS_CACHE_NAME = "ptvStops"
        const val PTV_DISRUPTIONS_CACHE_NAME = "ptvDisruptions"
        const val PTV_DEPARTURES_CACHE_NAME = "ptvDepartures"
        const val PTV_DIRECTIONS_CACHE_NAME = "ptvDirections"
    }
}