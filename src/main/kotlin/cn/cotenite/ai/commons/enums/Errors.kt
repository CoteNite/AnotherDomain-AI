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

}