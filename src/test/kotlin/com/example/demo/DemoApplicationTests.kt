package com.example.demo

import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@EmbeddedKafka(brokerProperties = ["auto.create.topics.enable=false"])
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DemoApplicationTests {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    @Order(1)
    fun `test await two times`() {
        webTestClient.post().uri { builder -> builder.path("/test/await-await").build() }
            .exchange()
            .expectStatus().is2xxSuccessful
    }

    @Test
    @Order(2)
    fun `test await two times on same topic`() {
        webTestClient.post().uri { builder -> builder.path("/test/await-await-same-topic").build() }
            .exchange()
            .expectStatus().is2xxSuccessful
    }

    @Test
    @Order(3)
    fun `test await and blocking get`() {
        webTestClient.post().uri { builder -> builder.path("/test/await-get").build() }
            .exchange()
            .expectStatus().is2xxSuccessful
    }

}
