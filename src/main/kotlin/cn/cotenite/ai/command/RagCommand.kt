package cn.cotenite.ai.command

import cn.cotenite.ai.commons.aop.Slf4j.Companion.log
import cn.cotenite.ai.commons.constants.RedisKeyBuilder
import cn.cotenite.ai.commons.utils.GitUtil
import cn.cotenite.ai.repository.RedisRepository
import cn.cotenite.ai.repository.VectorStoreRepository
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.ai.document.Document
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.core.io.PathResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.Consumer


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 00:42
 */
interface RagCommand {
    fun upload(ragTag:String,files: List<MultipartFile>)
    fun uploadRepository(repoUrl:String,userName:String,token:String)
}

@Service
class RagCommandImpl(
    private val redisRepository: RedisRepository,
    private val vectorStoreRepository: VectorStoreRepository
):RagCommand{

    override fun upload(ragTag: String, files: List<MultipartFile>) {

        files.forEach {
            try {
                vectorStoreRepository.insertFileWithTag(ragTag,it)
            }catch (e:Exception){
                log.error("遍历解析路径，上传知识库失败:${it.name}")
            }
            redisRepository.insert2ListIfNotExist(RedisKeyBuilder.buildRagTagListKey(),ragTag)
        }

    }

    override fun uploadRepository(repoUrl: String, userName: String, token: String) {
        val localPath = "./git-cloned-repo"
        val repoProjectName = GitUtil.extractProjectName(repoUrl)

        log.info("克隆路径：${File(localPath).absolutePath}")

        FileUtils.deleteDirectory(File(localPath))

        val git = Git.cloneRepository()
            .setURI(repoUrl)
            .setDirectory(File(localPath))
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(userName, token))
            .call()



        Files.walkFileTree(Paths.get(localPath), object : SimpleFileVisitor<Path>(){
            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                log.info("{} 遍历解析路径，上传知识库:{}", repoProjectName, file.fileName)
                try {
                    vectorStoreRepository.insertFileWithTag(repoProjectName, file)
                } catch (e: Exception) {
                    log.error("遍历解析路径，上传知识库失败:{}", file.fileName)
                }

                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
                log.info("Failed to access file: {} - {}", file.toString(), exc.message)
                return FileVisitResult.CONTINUE
            }
        })

        git.repository.close()
        git.close()

        FileUtils.deleteDirectory(File(localPath))

        redisRepository.insert2ListIfNotExist(RedisKeyBuilder.buildRagTagListKey(),repoProjectName)

        log.info("遍历解析路径，上传完成:${repoUrl}")
    }




}