package cn.cotenite.ai.controller

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.tool.ToolCallbackProvider
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
    private val tool: ToolCallbackProvider
){
    @GetMapping("/list")
    fun list(){
        val userInput = "有哪些工具可以使用"
        val chatClient = chatClientBuilder
            .defaultTools(tool)
            .build()

        println("\n>>> QUESTION: $userInput")
        println(" >>> ASSISTANT: ${chatClient.prompt(userInput).call().content()}".trimIndent())
    }

    @GetMapping("/creator")
    fun creator() {
        val userInput = "获取电脑配置在 D:\\workspace\\study-demo\\Another-Domain-AI\\src\\main\\resources\\mcp\\index 文件夹下，创建 电脑.txt 把电脑配置写入 电脑.txt"
        val chatClient: ChatClient = chatClientBuilder
            .defaultTools(tool)
            .build()
        println("\n>>> QUESTION: $userInput")
        println(">>> ASSISTANT: ${chatClient.prompt(userInput).call().content()}".trimIndent())
    }
}
