package cn.cotenite.ai.config

import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.autoconfigure.chat.client.ChatClientBuilderConfigurer
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.DefaultChatClientBuilder
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Scope


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/1 18:15
 */
@Configuration
class AiConfig(
    val ollamaEmbeddingModel: EmbeddingModel
){

    @Bean
    fun tokenTextSplitter(): TokenTextSplitter {
        return TokenTextSplitter()
    }

    @Bean
    @Primary
    fun primaryEmbeddingModel()=ollamaEmbeddingModel


    @Bean
    fun chatClientBuilder(chatModel: OpenAiChatModel): ChatClient.Builder {
        return DefaultChatClientBuilder(
            chatModel,
            ObservationRegistry.NOOP,
            null as ChatClientObservationConvention?
        )
    }

}