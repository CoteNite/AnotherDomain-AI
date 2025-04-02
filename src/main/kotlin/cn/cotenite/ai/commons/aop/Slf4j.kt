package cn.cotenite.ai.commons.aop

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/1 19:42
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Slf4j{
    companion object{
        val <reified T> T.log: Logger
            inline get() = LoggerFactory.getLogger(T::class.java)
    }
}