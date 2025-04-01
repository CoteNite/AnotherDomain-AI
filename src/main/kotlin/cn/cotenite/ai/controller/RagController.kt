package cn.cotenite.ai.controller

import cn.cotenite.ai.utils.Slf4j
import cn.cotenite.ai.utils.Slf4j.Companion.log
import cn.hutool.core.io.FileUtil
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.redisson.api.RList
import org.redisson.api.RedissonClient
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore
import org.springframework.core.io.PathResource
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
    private val chatModel: OllamaChatModel,
    private val vectorStore: MilvusVectorStore,
    private val tokenTextSplitter: TokenTextSplitter,
    private val redissonClient: RedissonClient
){

    @GetMapping("ragList")
    fun getRagList(): RList<String>? {
        val list = redissonClient.getList<String>("ragTag")
        return list
    }

    @PostMapping("/upload")
    fun upload(@RequestParam ragTag:String,@RequestParam files: List<MultipartFile>):String{

        files.forEach {
            val documentReader = TikaDocumentReader(it.resource)
            val documents = documentReader.get()
            val documentsSplitterList = tokenTextSplitter.apply(documents)

            documents.forEach{ doc ->
                doc.metadata["knowledge"] = ragTag
            }

            documentsSplitterList.forEach { doc->
                doc.metadata["knowledge"] = ragTag
            }

            vectorStore.accept(documentsSplitterList)

            val elements = redissonClient.getList<String>("ragTag")

            if (!elements.contains(ragTag)){
                elements.add(ragTag)
            }

        }
        return "success"
    }

    @PostMapping("/git")
    fun chat(@RequestParam repoUrl:String, @RequestParam userName:String, @RequestParam token:String): String {
        val localPath = "./git-cloned-repo"
        val repoProjectName = this.extractProjectName(repoUrl)
        log.info("克隆路径：{}", File(localPath).absolutePath)

        FileUtils.deleteDirectory(File(localPath))


        val git = Git.cloneRepository()
            .setURI(repoUrl)
            .setDirectory(File(localPath))
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(userName, token))
            .call()


        Files.walkFileTree(Paths.get(localPath), object : SimpleFileVisitor<Path?>() {

            override fun visitFile(file: Path?, attrs: BasicFileAttributes): FileVisitResult {

                if (file==null){
                    throw Exception("file is null")
                }

                log.info("{} 遍历解析路径，上传知识库:{}", repoProjectName, file.fileName)

                try {
                    val documentReader = TikaDocumentReader(PathResource(file))
                    val documents = documentReader.get()
                    val documentsSplitterList = tokenTextSplitter.apply(documents)

                    documents.forEach { doc ->
                        doc.metadata["knowledge"] = repoProjectName
                    }

                    documentsSplitterList.forEach { doc ->
                        doc.metadata["knowledge"] = repoProjectName
                    }

                    vectorStore.accept(documentsSplitterList)
                }catch (e:Exception){
                    log.error("遍历解析路径，上传知识库失败:{}", file.fileName)
                }

                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(file: Path?, exc: IOException): FileVisitResult {
                log.info("Failed to access file: {} - {}", file.toString(), exc.message)
                return FileVisitResult.CONTINUE
            }

        })


        git.repository.close()
        git.close()

        FileUtils.deleteDirectory(File(localPath))

        val elements = redissonClient.getList<String>("ragTag")
        if (!elements.contains(repoProjectName)) {
            elements.add(repoProjectName)
        }

        log.info("遍历解析路径，上传完成:{}", repoUrl)
        return "Success"
    }

    private fun extractProjectName(repoUrl: String): String {
        val parts = repoUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val projectNameWithGit = parts[parts.size - 1]
        return projectNameWithGit.replace(".git", "")
    }

}