package cn.cotenite.ai.query

import cn.cotenite.ai.commons.enums.Errors
import cn.cotenite.ai.commons.exception.BusinessException
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.cassandra.CassandraChatMemory
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestParam

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 01:37
 */
interface ChatQuery{
    fun generate(message:String, sessionId:String): String
}

@Service
class ChatQueryImpl(
    private val chatModel: OpenAiChatModel,
    private val chatMemory: CassandraChatMemory
):ChatQuery{

        override fun generate(message:String, sessionId:String):String{
        val advisor = MessageChatMemoryAdvisor(chatMemory, sessionId, 10)
        val request = ChatClient.create(chatModel)
            .prompt()
            .advisors(advisor)
            .user(message)
            .call()
            .content()?:throw BusinessException(Errors.CHAT_ERROR)
        return request
    }

}