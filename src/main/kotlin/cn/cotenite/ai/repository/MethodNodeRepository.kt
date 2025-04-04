package cn.cotenite.ai.repository

import cn.cotenite.ai.model.entity.MethodNode
import org.springframework.data.neo4j.repository.Neo4jRepository


/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/4 19:23
 */
interface MethodNodeRepository :Neo4jRepository<MethodNode,String> {

}