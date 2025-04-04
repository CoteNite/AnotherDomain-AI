package cn.cotenite.ai.controller

import cn.cotenite.ai.command.RagCommand
import cn.cotenite.ai.commons.aop.Slf4j
import cn.cotenite.ai.commons.response.Response
import cn.cotenite.ai.query.RagQuery
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/1 18:12
 */
@Slf4j
@RestController
@RequestMapping("/rag")
class RagController(
    private val ragQuery: RagQuery,
    private val ragCommand: RagCommand,
    @Qualifier("taskExecutor") private val taskExecutor: ThreadPoolTaskExecutor
){

    @GetMapping("ragList")
    fun getRagList(): Response {
        val list = ragQuery.getRagTagList()
        return Response.success(list)
    }

    @PostMapping("/uploadFile")
    fun upload(@RequestParam ragTag:String,@RequestParam files: List<MultipartFile>): Response {
        ragCommand.upload(ragTag,files)
        return Response.success()
    }

    @PostMapping("/uploadRepository")
    fun uploadRepository(@RequestParam repoUrl:String, @RequestParam userName:String, @RequestParam token:String,@RequestParam javaRepo:Boolean): Response {

        ragCommand.uploadRepository(repoUrl,userName,token,javaRepo)

        return Response.success()
    }



}