package cn.cotenite.ai.config

import com.datastax.oss.driver.api.core.CqlSession
import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.autoconfigure.chat.memory.cassandra.CassandraChatMemoryProperties
import org.springframework.ai.chat.client.DefaultChatClientBuilder
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention
import org.springframework.ai.chat.memory.cassandra.CassandraChatMemory
import org.springframework.ai.chat.memory.cassandra.CassandraChatMemoryConfig
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/1 18:15
 */
@Configuration
class AiConfig(
    private val ollamaEmbeddingModel: EmbeddingModel
){

    @Bean
    fun tokenTextSplitter()= TokenTextSplitter()

    @Bean
    @Primary
    fun embeddingModel()=ollamaEmbeddingModel


    @Bean
    fun chatClientBuilder(chatModel: OpenAiChatModel): DefaultChatClientBuilder {
        return DefaultChatClientBuilder(
            chatModel,
            ObservationRegistry.NOOP,
            null as ChatClientObservationConvention?
        )
    }

}