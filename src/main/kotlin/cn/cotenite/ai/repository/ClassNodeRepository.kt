package cn.cotenite.ai.repository

import cn.cotenite.ai.model.entity.ClassNode
import org.springframework.data.neo4j.repository.Neo4jRepository

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/4 19:24
 */
interface ClassNodeRepository : Neo4jRepository<ClassNode, String>