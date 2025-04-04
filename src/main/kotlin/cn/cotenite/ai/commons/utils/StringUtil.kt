package cn.cotenite.ai.commons.utils

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/4 22:44
 */
object StringUtil {

    fun extractKeyWords(message:String):List<String>{
        val commonWords = setOf("the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "with", "by", "how", "what", "when", "where", "why")
        return message.split(" ", ".", ",", "?", "!", "(", ")")
            .map { it.lowercase().trim() }
            .filter { it.length > 2 && it !in commonWords }
            .distinct()
    }



}