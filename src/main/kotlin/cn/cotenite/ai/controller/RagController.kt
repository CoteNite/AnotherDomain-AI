package cn.cotenite.ai.controller

import cn.cotenite.ai.command.RagCommand
import cn.cotenite.ai.commons.aop.Slf4j
import cn.cotenite.ai.commons.aop.Slf4j.Companion.log
import cn.cotenite.ai.commons.response.Response
import cn.cotenite.ai.query.RagQuery
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.redisson.api.RList
import org.redisson.api.RedissonClient
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.PathResource
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes


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
    fun uploadRepository(@RequestParam repoUrl:String, @RequestParam userName:String, @RequestParam token:String): Response {
        taskExecutor.submit{
            ragCommand.uploadRepository(repoUrl,userName,token)
        }
        return Response.success()
    }



}