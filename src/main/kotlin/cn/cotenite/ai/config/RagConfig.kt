package cn.cotenite.ai.config

import io.micrometer.observation.ObservationRegistry
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.DefaultChatClientBuilder
import org.springframework.ai.chat.client.observation.ChatClientObservationConvention
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/1 18:15
 */
@Configuration
class RagConfig {


    @Bean
    fun tokenTextSplitter(): TokenTextSplitter {
        return TokenTextSplitter()
    }

    @Bean
    fun chatClientBuilder(ollamaChatModel: OllamaChatModel): ChatClient.Builder {
        return DefaultChatClientBuilder(
            ollamaChatModel,
            ObservationRegistry.NOOP,
            null as ChatClientObservationConvention?
        )
    }

}