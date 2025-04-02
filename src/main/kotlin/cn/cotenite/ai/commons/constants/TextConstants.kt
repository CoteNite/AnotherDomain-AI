package cn.cotenite.ai.commons.constants

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/2 21:47
 */
object TextConstants {

    val RAG_CONTEXT_PROMPT = """
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
            Another thing you need to note is that your reply must be in Chinese!
            DOCUMENTS:
                {documents}
            """.trimIndent()

}