package cn.cotenite.ai.model.entity

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/4 02:27
 */
@Node
class MethodNode(
    @Id
    val id:String,
    val name:String,
    val comment:String,
    val content:String,

    @Relationship(direction = Relationship.Direction.OUTGOING, type = "USERS")
    var usesMethodNodes:MutableList<MethodNode>
){
    constructor(id:String,name:String,comment:String,content:String):this(id,name,comment,content, mutableListOf())
}