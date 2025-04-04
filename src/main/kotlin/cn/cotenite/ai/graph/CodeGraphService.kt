package cn.cotenite.ai.graph

import cn.cotenite.ai.model.entity.ClassNode
import cn.cotenite.ai.model.entity.MethodNode
import cn.cotenite.ai.repository.ClassNodeRepository
import cn.cotenite.ai.repository.MethodNodeRepository
import org.springframework.ai.document.Document
import org.springframework.stereotype.Service

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/4 23:20
 */
@Service
class CodeGraphService(
    private val classNodeRepository: ClassNodeRepository,
    private val methodNodeRepository: MethodNodeRepository,
){

    fun enhanceWithGraphInfo(documents: List<Document>): List<Document> {

        return documents.map { doc->
            val content=doc.text

            val classNameMatch = content?.let { Regex("class\\s+(\\w+)").find(it) }

            if (classNameMatch!=null){

                val className = classNameMatch.groupValues[1]
                val classNodes = classNodeRepository.findByName(className)

                if (!classNodes.isNullOrEmpty()) {

                    val mostRelevantClass = this.findMostRelevantClass(classNodes, content)
                    return@map this.enhanceClassNodeDocument(doc, mostRelevantClass.id)

                }
            }

            val methodNameMatch = content?.let { Regex("(fun|method|function)\\s+(\\w+)").find(it) }

            if (methodNameMatch != null) {

                val methodName = methodNameMatch.groupValues[2]
                val methodNodes = methodNodeRepository.findByName(methodName)

                if (!methodNodes.isNullOrEmpty()) {

                    val mostRelevantMethod = this.findMostRelevantMethod(methodNodes, content)
                    return@map this.enhanceMethodNodeDocument(doc, mostRelevantMethod.id)

                }
            }

            return@map doc
        }
    }

    private fun findMostRelevantMethod(methods: MutableList<MethodNode>, content: String): MethodNode {

        if (methods.size==1){
            return methods.first()
        }

        return methods.maxByOrNull { methodNode ->
            val contentWords = content.split(Regex("\\s+")).toSet()
            val methodWords = methodNode.content.split(Regex("\\s+")).toSet()
            contentWords.intersect(methodWords).size
        } ?: methods.first()
    }

    private fun findMostRelevantClass(classes: MutableList<ClassNode>, content: String): ClassNode {

        if (classes.size==1){
            return classes.first()
        }

        return classes.maxByOrNull { classNode ->
            val contentWords = content.split(Regex("\\s+")).toSet()
            val classWords = classNode.content.split(Regex("\\s+")).toSet()
            contentWords.intersect(classWords).size
        } ?: classes.first()
    }


    private fun enhanceClassNodeDocument(doc: Document, id:String): Document {
        val classNode = classNodeRepository.findById(id).orElse(null) ?: return doc
        val enhancedContent = buildClassNodeContent(classNode)

        return Document.builder()
            .id(doc.id)
            .text(enhancedContent)
            .metadata(doc.metadata)
            .build()
    }

    private fun enhanceMethodNodeDocument(doc: Document, id: String):Document{
        val methodNode = methodNodeRepository.findById(id).orElse(null) ?: return doc
        val enhancedContent = buildMethodNodeContent(methodNode)

        return Document.builder()
            .id(doc.id)
            .text(enhancedContent)
            .metadata(doc.metadata)
            .build()
    }

    private fun buildClassNodeContent(classNode: ClassNode): String {
        val sb = StringBuilder()
        sb.append("类名: ${classNode.name}\n")
        sb.append("内容: ${classNode.content}\n")

        // 添加方法信息
        if (!classNode.ownMethodNodes.isNullOrEmpty()) {
            sb.append("包含的方法:\n")
            classNode.ownMethodNodes!!.forEach { method ->
                sb.append("- 方法名: ${method.name}\n")
                if (method.comment.isNotBlank()) {
                    sb.append("  注释: ${method.comment}\n")
                }
                sb.append("  内容摘要: ${method.content.take(100)}...\n")
            }
        }

        // 添加导入的类
        if (!classNode.importNodes.isNullOrEmpty()) {
            sb.append("导入的类:\n")
            classNode.importNodes!!.forEach { importClass ->
                sb.append("- ${importClass.name}\n")
            }
        }

        return sb.toString()
    }

    /**
     * 构建方法节点的增强内容
     */
    private fun buildMethodNodeContent(methodNode: MethodNode): String {
        val sb = StringBuilder()
        sb.append("方法名: ${methodNode.name}\n")
        if (methodNode.comment.isNotBlank()) {
            sb.append("注释: ${methodNode.comment}\n")
        }
        sb.append("内容: ${methodNode.content}\n")

        // 添加调用的方法信息
        if (methodNode.usesMethodNodes.isNotEmpty()) {
            sb.append("调用的方法:\n")
            methodNode.usesMethodNodes.forEach { called ->
                sb.append("- ${called.name}\n")
                if (called.comment.isNotBlank()) {
                    sb.append("  注释: ${called.comment}\n")
                }
            }
        }

        return sb.toString()
    }


}