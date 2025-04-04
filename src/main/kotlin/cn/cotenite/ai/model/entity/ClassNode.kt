package cn.cotenite.ai.model.entity

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.Relationship

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/4 02:26
 */
@Node
class ClassNode(
    @Id
    val id:String,
    val name:String,
    val content:String,

    @Relationship(direction = Relationship.Direction.OUTGOING, type = "OWNS")
    var ownMethodNodes:MutableList<MethodNode>?,

    @Relationship(direction = Relationship.Direction.OUTGOING, type = "IMPORTS")
    var importNodes:MutableList<ClassNode>?
){



    constructor(id:String,content:String,name:String):this(id,name,content, mutableListOf(), mutableListOf())
}