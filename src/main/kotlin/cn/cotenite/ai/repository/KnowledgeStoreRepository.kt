package cn.cotenite.ai.repository

import cn.cotenite.ai.commons.aop.Slf4j
import cn.cotenite.ai.commons.aop.Slf4j.Companion.log
import cn.cotenite.ai.commons.enums.Errors
import cn.cotenite.ai.commons.exception.BusinessException
import org.springframework.ai.document.Document
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore
import org.springframework.core.io.PathResource
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 01:01
 */
@Slf4j
@Repository
class KnowledgeStoreRepository(
    private val milvusVectorStore: MilvusVectorStore,
    private val tokenTextSplitter: TokenTextSplitter
){

    fun insertFileWithTag(ragTag:String,file: MultipartFile){
        val reader = TikaDocumentReader(file.resource)
        val documentsSplitterList = this.buildFileList(reader,ragTag)?:throw BusinessException(Errors.FILE_ERROR)
        milvusVectorStore.accept(documentsSplitterList)

    }

    fun insertFileWithTag(ragTag:String,file: Path){
        val reader = TikaDocumentReader(PathResource(file))
        val documentsSplitterList = this.buildFileList(reader, ragTag) ?: throw BusinessException(Errors.FILE_ERROR)

        val chunked = documentsSplitterList.chunked(10)

        chunked.forEach {
            it.filter { doc ->
                !doc.text.isNullOrBlank() && doc.metadata.isNotEmpty()
            }
            if (it.isNotEmpty()) {
                milvusVectorStore.accept(it)
            } else {
                log.warn("No valid documents found for file: ${file.fileName}")
            }
        }

    }

    private fun buildFileList(reader:TikaDocumentReader, ragTag:String): MutableList<Document>? {
        val documents = reader.get()
        val documentsSplitterList = tokenTextSplitter.apply(documents)
        documents.forEach{ doc ->
            doc.metadata["knowledge"] = ragTag
        }

        documentsSplitterList.forEach { doc->
            doc.metadata["knowledge"] = ragTag
        }
        return documentsSplitterList
    }

    fun similaritySearch(request: SearchRequest): List<Document> {
        val documents = milvusVectorStore.similaritySearch(request)?:throw BusinessException(Errors.FILE_ERROR)
        return documents
    }

}