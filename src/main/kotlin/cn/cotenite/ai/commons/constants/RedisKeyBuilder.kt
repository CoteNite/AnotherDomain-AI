package cn.cotenite.ai.commons.constants

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/2 21:45
 */
object RedisKeyBuilder {

    private const val PREFIX="AnotherDomain:Ai:"

    fun buildRagTagListKey():String{
        return "${PREFIX}ragTag"
    }

}