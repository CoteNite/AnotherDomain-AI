package cn.cotenite.ai.commons.enums

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/2 21:41
 */
enum class Errors(
    val code:Int,
    val message:String
){

    UNKNOWN(1000,"未知错误"),

    PARAM_ERROR(1001,"参数错误"),

    FILE_ERROR(1002,"文件错误"),

    FILE_UPLOAD_ERROR(1003,"文件上传错误"),

    FILE_DOWNLOAD_ERROR(1004,"文件下载错误"),

    FILE_DELETE_ERROR(1005,"文件删除错误"),

    FILE_NOT_FOUND(1006,"文件不存在"),

    FILE_EXISTS(1007,"文件已存在"),

    RAG_TAG_NOT_FOUND(1008,"rag标签不存在"),

    RAG_TAG_EXISTS(1009,"rag标签已存在"),

    RAG_TAG_ERROR(1010,"rag标签错误"),

    RAG_TAG_NOT_EXISTS(1011,"rag标签不存在"),

    RAG_TAG_NOT_EMPTY(1012,"rag标签不为空"),

    RAG_TAG_NOT_EXISTS_IN_REDIS(1013,"rag标签不存在于redis"),

    RAG_TAG_NOT_EXISTS_IN_VECTOR_STORE(1014,"rag标签不存在于向量库中"),

    CHAT_ERROR(1015,"聊天错误"),

    CHAT_MEMORY_ERROR(1016,"聊天记录错误"),

    CHAT_MEMORY_NOT_FOUND(1017,"聊天记录不存在"),

    CHAT_MEMORY_NOT_EMPTY(1018,"聊天记录不为空"),

    CHAT_MEMORY_NOT_EXISTS_IN_REDIS(1019,"聊天记录不存在于redis"),

    CHAT_MEMORY_NOT_EXISTS_IN_VECTOR_STORE(1020,"聊天记录不存在于向量库中"),

    CHAT_MEMORY_NOT_EXISTS_IN_CHAT_MEMORY(1021,"聊天记录不存在于聊天记录中"),

    CHAT_MEMORY_NOT_EXISTS_IN_CHAT_MEMORY_LIST(1022,"聊天记录不存在于聊天记录列表中"),

    CHAT_MEMORY_NOT_EXISTS_IN_CHAT_MEMORY_LIST_BY_SESSION_ID(1023,"聊天记录不存在于聊天记录列表中"),

    CHAT_MEMORY_NOT_EXISTS_IN_CHAT_MEMORY_LIST_BY_SESSION_ID_AND_MESSAGE(1024,"聊天记录不存在于聊天记录列表中")
}