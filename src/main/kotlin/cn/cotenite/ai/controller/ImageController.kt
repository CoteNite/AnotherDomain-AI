package cn.cotenite.ai.controller

import cn.cotenite.ai.commons.response.Response
import cn.cotenite.ai.query.ImageQuery
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/5 02:34
 */
@RestController
@RequestMapping("/image")
class ImageController(
    private val imageQuery: ImageQuery
){

    @GetMapping("/create")
    fun create(@RequestParam message:String):Response{
        val url = imageQuery.create("sessionId", "message")
        return Response.success(url)
    }

}