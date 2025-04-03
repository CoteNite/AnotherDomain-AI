package cn.cotenite.ai

import org.springframework.ai.autoconfigure.vectorstore.cassandra.CassandraVectorStoreAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(
    exclude = [CassandraVectorStoreAutoConfiguration::class]
)
class AiApplication

fun main(args: Array<String>) {
    runApplication<AiApplication>(*args)
}
