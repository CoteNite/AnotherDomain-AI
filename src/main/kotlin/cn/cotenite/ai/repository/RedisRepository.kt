package cn.cotenite.ai.repository

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Repository

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 01:08
 */
@Repository
class RedisRepository(
    private val redissonClient: RedissonClient
){

    fun insert2ListIfNotExist(key:String,value:String) {
        val elements = redissonClient.getList<String>(key)

        if (!elements.contains(value)){
            elements.add(value)
        }
    }

}