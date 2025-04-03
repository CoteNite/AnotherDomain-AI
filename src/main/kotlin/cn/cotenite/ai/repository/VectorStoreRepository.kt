package cn.cotenite.ai.repository

import cn.cotenite.ai.commons.enums.Errors
import cn.cotenite.ai.commons.exception.BusinessException
import org.springframework.ai.document.Document
import org.springframework.ai.reader.tika.TikaDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.PathResource
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 01:01
 */
@Repository
class VectorStoreRepository(
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
        val documentsSplitterList = this.buildFileList(reader,ragTag)?:throw BusinessException(Errors.FILE_ERROR)
        milvusVectorStore.accept(documentsSplitterList)
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

    fun similaritySearch(request: SearchRequest): String {
        val documents = milvusVectorStore.similaritySearch(request)?:throw BusinessException(Errors.FILE_ERROR)
        return documents.map { it.text }.joinToString("")
    }

}