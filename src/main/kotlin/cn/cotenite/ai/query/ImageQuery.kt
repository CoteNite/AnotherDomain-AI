package cn.cotenite.ai.query

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.cassandra.CassandraChatMemory
import org.springframework.ai.image.ImagePrompt
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiImageModel
import org.springframework.ai.openai.OpenAiImageOptions
import org.springframework.stereotype.Service

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/5 03:33
 */
interface ImageQuery{
    fun create(sessionId: String,message:String): String

}

@Service
class ImageQueryImpl(
    private val imageModel: OpenAiImageModel
):ImageQuery{


    override fun create(sessionId:String,message:String): String {
        val prompt = ImagePrompt(message)

        val response = imageModel.call(prompt)

        return response.result.output.url
    }

}