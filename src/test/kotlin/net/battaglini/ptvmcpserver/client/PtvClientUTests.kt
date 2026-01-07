package net.battaglini.ptvmcpserver.client

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import net.battaglini.ptvmcpserver.client.PtvClient.Companion.DEVID_QUERY_PARAM_KEY
import net.battaglini.ptvmcpserver.configuration.PtvClientProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory
import kotlin.test.expect

@ExtendWith(MockKExtension::class)
class PtvClientUTests {
    @MockK
    private lateinit var ptvClientProperties: PtvClientProperties

    @MockK
    private lateinit var ptvWebClient: WebClient

    @InjectMockKs
    private lateinit var ptvClient: PtvClient

    @BeforeEach
    fun init() {
        every { ptvClientProperties.baseUrl } returns "https://ptv.local"
        every { ptvClientProperties.username } returns "username"
        // Random UUID generated just for these tests
        every { ptvClientProperties.password } returns "ca7fd9ef-0cc6-4454-a7b1-fbd971d10a6e"
        every { ptvClientProperties.apiVersion } returns "v3"
    }

    @Test
    fun `should calculate HMAC signature based on PTV spec`() {
        val uri = DefaultUriBuilderFactory().builder()
            .path("/${ptvClientProperties.apiVersion}/routes")
            .queryParam(DEVID_QUERY_PARAM_KEY, ptvClientProperties.username)
            .build()

        // Calculated directly on the PTV API spec page for the above values of devid and secret key, and the URI provided
        val expected = "F3FCC71528DD74772DEA49C55650BAE5EAB8EBD3"
        expect(expected, {
            ptvClient.calculateHMACSignature(uri)
        })
    }
}