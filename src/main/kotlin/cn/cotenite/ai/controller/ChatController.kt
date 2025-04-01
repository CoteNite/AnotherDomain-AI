package cn.cotenite.ai.controller

import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.chat.prompt.SystemPromptTemplate
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/1 17:38
 */
@RestController
@RequestMapping("/chat")
class ChatController(
    private val chatModel: OllamaChatModel,
    private val vectorStore: MilvusVectorStore
){

    companion object{
        val SYSTEM_PROMPT = """
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
            Another thing you need to note is that your reply must be in Chinese!
            DOCUMENTS:
                {documents}
            """.trimIndent()
    }

    @GetMapping("/generate")
    fun generate(@RequestParam message:String): ChatResponse? = chatModel.call(Prompt(message))

    @GetMapping("/ragGenerate")
    fun ragGenerate(@RequestParam model:String, @RequestParam ragTag:String, @RequestParam message:String): ChatResponse? {

        val request = SearchRequest.builder()
            .query(message)
            .topK(5)
            .filterExpression("knowledge == '${ragTag}'")
            .build()

        val documents = vectorStore.similaritySearch(request)?:throw Exception("No documents found")

        val documentCollectors  = documents.map { it.text }.joinToString("")

        val ragMessage = SystemPromptTemplate(SYSTEM_PROMPT).createMessage(mapOf("documents" to documentCollectors))

        val messages = listOf(UserMessage(message),ragMessage)

        return chatModel.call(Prompt(messages))
    }
}