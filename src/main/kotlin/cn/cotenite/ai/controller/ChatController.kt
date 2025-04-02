package cn.cotenite.ai.controller

import cn.cotenite.ai.commons.constants.TextConstants
import cn.cotenite.ai.commons.response.Response
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

    @GetMapping("/generate")
    fun generate(@RequestParam message:String):Response{
        val callResponse = chatModel.call(Prompt(message))
        return Response.success(callResponse)
    }

    @GetMapping("/ragGenerate")
    fun ragGenerate(@RequestParam model:String, @RequestParam ragTag:String, @RequestParam message:String): Response {

        val request = SearchRequest.builder()
            .query(message)
            .topK(5)
            .filterExpression("knowledge == '${ragTag}'")
            .build()

        val documents = vectorStore.similaritySearch(request)?:throw Exception("No documents found")

        val documentCollectors  = documents.map { it.text }.joinToString("")

        val ragMessage = SystemPromptTemplate(TextConstants.RAG_CONTEXT_PROMPT).createMessage(mapOf("documents" to documentCollectors))

        val messages = listOf(UserMessage(message),ragMessage)

        return Response.success(chatModel.call(Prompt(messages)))
    }
}