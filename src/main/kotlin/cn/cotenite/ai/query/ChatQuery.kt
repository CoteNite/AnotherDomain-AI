package cn.cotenite.ai.query

import cn.cotenite.ai.commons.constants.RedisKeyBuilder
import cn.cotenite.ai.commons.constants.TextConstants
import cn.cotenite.ai.commons.enums.Errors
import cn.cotenite.ai.commons.exception.BusinessException
import cn.cotenite.ai.repository.VectorStoreRepository
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.cassandra.CassandraChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.stereotype.Service

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 01:37
 */
interface ChatQuery{
    fun generate(sessionId:String,message:String): String

    fun ragGenerate(sessionId: String,message: String, ragTag: String): String
}

@Service
class ChatQueryImpl(
    private val chatModel: OllamaChatModel,
    private val chatMemory: CassandraChatMemory,
    private val vectorRepository: VectorStoreRepository
):ChatQuery{

    override fun generate(message:String, sessionId:String):String{
            val content = this.chat(sessionId, message)
            return content
    }

    override fun ragGenerate(sessionId: String,message: String, ragTag: String):String{

        val request = SearchRequest.builder()
            .query(message)
            .topK(5)
            .filterExpression("knowledge == '${ragTag}'")
            .build()

        val documents = vectorRepository.similaritySearch(request)
        val ragMessage = SystemPromptTemplate(TextConstants.RAG_CONTEXT_PROMPT).createMessage(mapOf("documents" to documents))

        val content = this.ragChat(sessionId, message,ragMessage)
        return content
    }




    private fun chat(sessionId: String,message: String):String{
        val advisor = MessageChatMemoryAdvisor(chatMemory, sessionId, 10)
        val content = ChatClient.create(chatModel)
            .prompt()
            .advisors(advisor)
            .user(message)
            .call()
            .content()?:throw BusinessException(Errors.CHAT_ERROR)
        return content
    }

    private fun ragChat(sessionId: String,userMessage:String, ragMessage: Message):String{
        val advisor = MessageChatMemoryAdvisor(chatMemory, sessionId, 10)
        val content = ChatClient.create(chatModel)
            .prompt(Prompt(ragMessage))
            .advisors(advisor)
            .user(userMessage)
            .call()
            .content()?:throw BusinessException(Errors.CHAT_ERROR)
        return content
    }

}