package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

@SpringBootApplication
class DemoApplication {

    @Bean
    fun createTopics() = KafkaAdmin.NewTopics(
        TopicBuilder
            .name("topic-1")
            .replicas(1)
            .partitions(3)
            .build(),
        TopicBuilder
            .name("topic-2")
            .replicas(1)
            .partitions(3)
            .build(),
    )

}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
