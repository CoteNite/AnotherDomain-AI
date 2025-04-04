package cn.cotenite.ai.query

import cn.cotenite.ai.commons.constants.TextConstants
import cn.cotenite.ai.commons.enums.Errors
import cn.cotenite.ai.commons.exception.BusinessException
import cn.cotenite.ai.graph.CodeGraphService
import cn.cotenite.ai.repository.KnowledgeStoreRepository
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.cassandra.CassandraChatMemory
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.model.Media
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import java.net.URL

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 01:37
 */
interface ChatQuery{
    fun generate(sessionId:String,message:String,imageUrl: String?): String

    fun ragGenerate(sessionId: String,message: String, ragTag: String,javaRepo: Boolean): String
}

@Service
class ChatQueryImpl(
    private val chatModel: OpenAiChatModel,
    private val chatMemory: CassandraChatMemory,
    private val vectorRepository: KnowledgeStoreRepository,
    private val codeGraphService: CodeGraphService
):ChatQuery{

    override fun generate(sessionId:String,message:String,imageUrl: String?):String{
        val content:String =
            if (imageUrl!=null)
                this.chat(sessionId, message,imageUrl)
            else
                this.chat(sessionId, message)

        return content
    }

    override fun ragGenerate(sessionId: String,message: String, ragTag: String,javaRepo: Boolean):String{
        val request = SearchRequest.builder()
            .query(message)
            .topK(20)
            .filterExpression("knowledge == '${ragTag}'")
            .build()

        var documents = vectorRepository.similaritySearch(request)

        if (javaRepo){
            documents = codeGraphService.enhanceWithGraphInfo(documents)
        }

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

    private fun chat(sessionId: String,message: String,imageUrl:String?):String{
        val advisor = MessageChatMemoryAdvisor(chatMemory, sessionId, 10)
        val userMessage = UserMessage(message, Media(MimeTypeUtils.IMAGE_PNG, URL(imageUrl)))
        val content = ChatClient.create(chatModel)
            .prompt()
            .advisors(advisor)
            .messages(userMessage)
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