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
    fun generate(@RequestParam message:String,@RequestParam sessionId:String,@RequestParam(required = false) imageUrl: String?):Response{
        val content = chatQuery.generate(sessionId,message,imageUrl)
        return Response.success(content)
    }

    @GetMapping("/ragGenerate")
    fun ragGenerate(@RequestParam sessionId: String, @RequestParam ragTag:String, @RequestParam message:String,@RequestParam javaRepo:Boolean): Response {
        val content = chatQuery.ragGenerate(sessionId,message,ragTag,javaRepo)
        return Response.success(content)
    }


}