package cn.cotenite.ai.commons.constants

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/2 21:45
 */
object RedisKeyCreator {

    val PREFIX="AnotherDomain:Ai:"

    fun getRagTagKey():String{
        return "${PREFIX}ragTag"
    }

}