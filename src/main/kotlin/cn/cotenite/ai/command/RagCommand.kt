package cn.cotenite.ai.command

import cn.cotenite.ai.commons.aop.Slf4j
import cn.cotenite.ai.commons.aop.Slf4j.Companion.log
import cn.cotenite.ai.commons.constants.RedisKeyBuilder
import cn.cotenite.ai.commons.utils.GitUtil
import cn.cotenite.ai.commons.utils.JavaParserUtil
import cn.cotenite.ai.graph.CodeGraphBuilder
import cn.cotenite.ai.repository.ClassNodeRepository
import cn.cotenite.ai.repository.KnowledgeStoreRepository
import cn.cotenite.ai.repository.MethodNodeRepository
import cn.cotenite.ai.repository.RedisRepository
import lombok.AllArgsConstructor
import lombok.Data
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Flux
import java.io.File
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.pathString


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 00:42
 */
interface RagCommand {
    fun upload(ragTag:String,files: List<MultipartFile>)
    fun uploadRepository(repoUrl:String,userName:String,token:String)
}

@Slf4j
@Service
class RagCommandImpl(
    private val redisRepository: RedisRepository,
    private val knowledgeStoreRepository: KnowledgeStoreRepository,
    private val methodNodeRepository: MethodNodeRepository,
    private val classNodeRepository: ClassNodeRepository,
    @Qualifier("taskExecutor") private val taskExecutor: ThreadPoolTaskExecutor
):RagCommand{

    override fun upload(ragTag: String, files: List<MultipartFile>) {
        files.forEach {
            try {
                knowledgeStoreRepository.insertFileWithTag(ragTag,it)
            }catch (e:Exception){
                log.error("遍历解析路径，上传知识库失败:${it.name}",e)
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

//        buildGraph(localPath)

        Files.walkFileTree(Paths.get(localPath), object : SimpleFileVisitor<Path>(){
            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                log.info("$repoProjectName 遍历解析路径，上传知识库:${file.fileName}")
                try {

                    knowledgeStoreRepository.insertFileWithTag(repoProjectName, file)
                } catch (e: Exception) {
                    log.error("遍历解析路径，上传知识库失败:${file.fileName}",e)
                }

                return FileVisitResult.CONTINUE
            }

            override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
                log.info("Failed to access file: $file - ${exc.message}")
                return FileVisitResult.CONTINUE
            }
        })

        git.repository.close()
        git.close()

        FileUtils.deleteDirectory(File(localPath))

        redisRepository.insert2ListIfNotExist(RedisKeyBuilder.buildRagTagListKey(),repoProjectName)

        log.info("遍历解析路径，上传完成:${repoUrl}")
    }

    private fun buildGraph(projectPath: String) {
        methodNodeRepository.deleteAll()
        classNodeRepository.deleteAll()

        val buildContext = CodeGraphBuilder(Path.of(projectPath), JavaParserUtil.getJavaParser(projectPath)).buildGraph()

        classNodeRepository.saveAll(buildContext.classNodes)
        log.info("类节点保存完毕:${projectPath}")
        methodNodeRepository.saveAll(buildContext.methodNodes)
        log.info("方法节点保存完毕:${projectPath}")

    }





}
