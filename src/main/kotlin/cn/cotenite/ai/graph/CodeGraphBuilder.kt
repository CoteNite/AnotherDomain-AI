package cn.cotenite.ai.graph

import cn.cotenite.ai.commons.aop.Slf4j
import cn.cotenite.ai.commons.aop.Slf4j.Companion.log
import cn.cotenite.ai.commons.exception.BusinessException
import cn.cotenite.ai.model.entity.ClassNode
import cn.cotenite.ai.model.entity.MethodNode
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.MethodCallExpr
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import java.io.File
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/4 02:34
 */
@Slf4j
class CodeGraphBuilder(
    private val projectPath: Path,
    private val ragTag: String,
    private val javaParser:JavaParser
){

    companion object{
        private val mapperSqlMap = mutableMapOf<String, String>()
        private val classDeclarationMap = mutableMapOf<String, ClassOrInterfaceDeclaration>()
        private val classNodeMap =  mutableMapOf<String, ClassNode>()
        private val methodNodeMap =  mutableMapOf<String, MethodNode>()
    }

    data class BuildContext(val classNodes: MutableCollection<ClassNode>, val methodNodes: MutableCollection<MethodNode>)


    fun buildGraph(): BuildContext {
        this.buildMapperSqlMap()
        Files.walk(this.getJavaSourcePath()).use { stream ->
            stream
                .filter{ it.toFile().isFile }
                .flatMap { this.getClassDeclarations(it).stream() }
                .filter{ it.fullyQualifiedName.isPresent }
                .forEach { classDeclarationMap[it.fullyQualifiedName.get()] = it }

            classDeclarationMap.values.forEach(this::buildClassNode)

            classNodeMap.values
                .forEach { classNode ->
                    val classOrInterfaceDeclaration = classDeclarationMap[classNode.id]?:throw BusinessException("找不到类节点")
                    val methodDeclarations = classOrInterfaceDeclaration.findAll(MethodDeclaration::class.java)
                    val ownsMethodNodes = methodDeclarations
                        .stream()
                        .map { this.buildMethodNode(it.nameAsString, classNode.id, methodDeclarations)}
                        .filter { it?.isPresent?:throw BusinessException("找不到方法节点") }
                        .map{ it?.get()}
                        .toList()
                    classNode.ownMethodNodes= ownsMethodNodes as MutableList<MethodNode>?
                }
        }
        return BuildContext(classNodeMap.values, methodNodeMap.values)
    }

    private fun buildMethodNode(methodName: String?, className: String, declarations: List<MethodDeclaration>): Optional<MethodNode>? {
        val methodId = "$className#$methodName"

        if (methodNodeMap.containsKey(methodId)) {
            return methodNodeMap[methodId]?.let { Optional.of(it) }
        }
        return declarations.stream()
            .filter { methodDeclaration -> methodDeclaration.nameAsString.equals(methodName) }
            .findFirst()
            .map { methodDeclaration ->
                val content: String? = methodDeclaration.findAll(AnnotationExpr::class.java)
                    .stream()
                    .filter { a ->
                        a.metaModel.qualifiedClassName.equals("org.apache.ibatis.annotations.Mapper")
                    }
                    .findAny()
                    .map { mapperSqlMap[methodId] }
                    .orElse(methodDeclaration.toString())
                val methodNode = MethodNode(
                    id = methodId,
                    name = methodDeclaration.nameAsString,
                    content = content?:"",
                    ragTag = ragTag,
                    comment = methodDeclaration.comment.map(Comment::getContent).orElse("")
                )
                methodNodeMap[methodNode.id] = methodNode
                val usesMethodNodes = methodDeclaration
                    .findAll(MethodCallExpr::class.java)
                    .stream()
                    .map(this::buildMethodNodeFromMethodCall)
                    .filter { it?.isPresent?:throw BusinessException("找不到方法节点") }
                    .map { it?.get() ?: throw BusinessException("找不到方法节点") }
                    .toList()
                methodNode.usesMethodNodes= usesMethodNodes as MutableList<MethodNode>
                methodNode
            }
    }

    private fun buildMethodNodeFromMethodCall(methodCallExpr: MethodCallExpr): Optional<MethodNode>{
        return methodCallExpr
            .scope
            .filter(this::checkScopeExist)
            .flatMap{ scope: Expression ->
                buildMethodNode(
                    methodCallExpr.nameAsString,
                    scope.calculateResolvedType().asReferenceType().qualifiedName,
                    this.getMethodDeclarationsFromScope(scope)
                )
            }
    }

    private fun getMethodDeclarationsFromScope(scope: Expression): List<MethodDeclaration> {
        return Optional.ofNullable(
            classDeclarationMap[scope.calculateResolvedType()
                .asReferenceType().qualifiedName]
        )
            .map { declaration -> declaration.findAll(MethodDeclaration::class.java) }
            .orElse(listOf())
    }

    private fun checkScopeExist(expression: Expression): Boolean {
        try {
            expression.calculateResolvedType().asReferenceType()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun buildClassNode(declaration: ClassOrInterfaceDeclaration): Optional<ClassNode>? {
        if (classNodeMap.contains(declaration.nameAsString)){
            return classNodeMap[declaration.nameAsString]?.let { Optional.of(it) }
        }
        return declaration.fullyQualifiedName
            .map { qualifiedClasName  ->
                val classNode=ClassNode(
                    id = qualifiedClasName ,
                    name = declaration.nameAsString,
                    ragTag = ragTag,
                    content = declaration.toString()
                )
                classNodeMap[qualifiedClasName ] = classNode
                val importClassNodes = declaration
                    .findAll(ImportDeclaration::class.java)
                    .stream()
                    .map { Optional.ofNullable(classDeclarationMap[it.nameAsString]).flatMap(this::buildClassNode) }
                    .filter { it.isPresent }
                    .map { it.get() }
                    .toList()

                classNode.importNodes=importClassNodes
                return@map classNode
            }

    }

    private fun getClassDeclarations(path: Path?): List<ClassOrInterfaceDeclaration> {
        return javaParser.parse(path)
            .result
            .map { it.findAll(ClassOrInterfaceDeclaration::class.java) }
            .filter { it.isNotEmpty() }
            .orElse(listOf())
    }

    private fun getJavaSourcePath(): Path {
        return projectPath.resolve(Path.of("src", "main", "java"))
    }


    private fun buildMapperSqlMap() {
        try {
            Files.walk(getFileInResource("mapper")).use {  stream ->
                stream
                    .filter{ it.toString().endsWith(".xml") }
                    .forEach{
                        val document = this.parseXMLFileAsDocument(it.toFile())
                        val selectNodes = document.documentElement.getElementsByTagName("select")
                        val namespace = document.documentElement.getAttribute("namespace")
                        this.extractSqlFromStatement(selectNodes, namespace)
                        val deleteNodes = document.documentElement.getElementsByTagName("delete")
                        this.extractSqlFromStatement(deleteNodes, namespace)
                        val updateNodes = document.documentElement.getElementsByTagName("update")
                        this.extractSqlFromStatement(updateNodes, namespace)
                    }
            }
        } catch (ignored: Exception) {
            log.warn("不存在mapper")
        }
    }

    private fun extractSqlFromStatement(nodeList: NodeList, namespace: String) {
        val tf = TransformerFactory.newInstance()
        val transformer = tf.newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        for (i in 0..<nodeList.length) {
            val writer = StringWriter()
            transformer.transform(DOMSource(nodeList.item(i)), StreamResult(writer))
            val output = writer.buffer.toString().replace("[\n\r]".toRegex(), "")
            val key=namespace + "#" + nodeList.item(i).attributes.getNamedItem("id").nodeValue
            mapperSqlMap[key] = output
        }
    }

    private fun parseXMLFileAsDocument(file: File): Document {
        val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        return documentBuilder.parse(file)
    }


    private fun getFileInResource(fileName: String): Path {
        return projectPath.resolve(Path.of("src","main","resources",fileName))
    }


}