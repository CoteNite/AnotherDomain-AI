package cn.cotenite.ai.controller

import cn.cotenite.ai.commons.response.Response
import cn.cotenite.ai.query.ChatQuery
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
    private val chatQuery: ChatQuery
){

    @GetMapping("/generate")
    fun generate(@RequestParam message:String,@RequestParam sessionId:String):Response{
        val content = chatQuery.generate(message, sessionId)
        return Response.success(content)
    }

    @GetMapping("/ragGenerate")
    fun ragGenerate(@RequestParam model:String, @RequestParam ragTag:String, @RequestParam message:String): Response {

//        val request = SearchRequest.builder()
//            .query(message)
//            .topK(5)
//            .filterExpression("knowledge == '${ragTag}'")
//            .build()
//
//        val documents = vectorStore.similaritySearch(request)?:throw Exception("No documents found")
//
//        val documentCollectors  = documents.map { it.text }.joinToString("")
//
//        val ragMessage = SystemPromptTemplate(TextConstants.RAG_CONTEXT_PROMPT).createMessage(mapOf("documents" to documentCollectors))
//
//        val messages = listOf(UserMessage(message),ragMessage)

        return Response.success()
    }
}