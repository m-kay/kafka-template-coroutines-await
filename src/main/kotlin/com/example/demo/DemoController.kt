package com.example.demo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/test")
class DemoController(private val kafkaTemplate: KafkaTemplate<String, String>) {

    private val log = LoggerFactory.getLogger(DemoController::class.java)

    @PostMapping("/await-await")
    suspend fun awaitTwoTimes() {
        log.info("sending to topic 1")
        kafkaTemplate.send("topic-1", "some key", "some value").completable().await()
        log.info("sent to topic 1")
        log.info("sending to topic 2")
        kafkaTemplate.send("topic-2", "some key", "some value").completable().await()
        log.info("sent to topic 2")
    }

    @PostMapping("/await-await-same-topic")
    suspend fun awaitTwoTimesSameTopic() {
        log.info("sending first message to topic 1")
        kafkaTemplate.send("topic-1", "some key", "some value").completable().await()
        log.info("sent first message to topic 1")
        log.info("sending second message to topic 1")
        kafkaTemplate.send("topic-1", "some key", "some value").completable().await()
        log.info("sent second message to topic 1")
    }

    @PostMapping("/mono")
    fun mono() : Mono<Void> {
        log.info("sending message to topic 1")
        val sent = Mono.fromFuture(kafkaTemplate.send("topic-1", "some key", "some value").completable())
            .flatMap{ first ->
                log.info("sent message to topic 1 to partitition ${first.recordMetadata.partition()}")
                log.info("sending message to topic 2")
                Mono.fromFuture(kafkaTemplate.send("topic-2", "some key", "some value").completable())
                    .map { second -> log.info("sent message to topic 2 to partition ${second.recordMetadata.partition()}") }
            }
        return Mono.`when`(sent)
    }

    @PostMapping("/await-get")
    suspend fun awaitGet() {
        log.info("sending to topic 1")
        val first = kafkaTemplate.send("topic-1", "some key", "some value").completable().await()
        log.info("sent to topic 1 to partitition ${first.recordMetadata.partition()}")
        log.info("sending to topic 2")
        val second = withContext(Dispatchers.Default) {
            kafkaTemplate.send("topic-2", "some key", "some value").get()
        }
        log.info("sent to topic 2 to partition ${second.recordMetadata.partition()}")
    }

}