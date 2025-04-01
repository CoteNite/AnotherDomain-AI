package cn.cotenite.ai.controller

import org.apache.catalina.startup.Tool
import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/1 20:53
 */
@RestController
@RequestMapping("/mcp")
class McpController(
    private val chatClientBuilder: ChatClient.Builder,
    private val tools: List<Tool>
){
    @GetMapping("/list")
    fun list(){
        val userInput = "有哪些工具可以使用"
        val chatClient = chatClientBuilder
            .defaultTools(tools)
            .build()

        println("\n>>> QUESTION: $userInput")
        println(">>> ASSISTANT: ${chatClient.prompt(userInput).call().content()}".trimIndent())
    }

    @GetMapping("/creator")
    fun creator() {
        var userInput = "获取电脑配置"
        userInput = "Desktop下，创建 电脑.txt"

        val chatClient = chatClientBuilder
            .defaultTools(tools)
            .build()

        println("\n>>> QUESTION: $userInput")
        println(" >>> ASSISTANT: ${chatClient.prompt(userInput).call().content()}".trimIndent())
    }
}
