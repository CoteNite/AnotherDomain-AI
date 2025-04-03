package cn.cotenite.ai.query

import cn.cotenite.ai.commons.constants.RedisKeyBuilder
import org.redisson.api.RList
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 00:42
 */
interface RagQuery {

    fun getRagTagList(): RList<String>?

}

@Service
class RagQueryImpl(
    private val redissonClient: RedissonClient
):RagQuery{

    override fun getRagTagList(): RList<String>?{
        val list = redissonClient.getList<String>(RedisKeyBuilder.buildRagTagListKey())
        return list
    }

}