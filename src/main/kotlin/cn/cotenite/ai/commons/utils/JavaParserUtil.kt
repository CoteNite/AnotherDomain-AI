package cn.cotenite.ai.commons.utils

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseResult
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import java.nio.file.Path

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/4 19:30
 */
object JavaParserUtil {

    fun getJavaParser(projectPath: String): JavaParser {
        val combinedTypeSolver= CombinedTypeSolver()
        combinedTypeSolver.add(JavaParserTypeSolver(Path.of(projectPath).resolve(Path.of("src","main","java"))))
        val parserConfiguration = ParserConfiguration().setSymbolResolver(JavaSymbolSolver(combinedTypeSolver))
        return JavaParser(parserConfiguration)
    }

}