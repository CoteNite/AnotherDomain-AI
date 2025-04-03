package cn.cotenite.ai.commons.exception

import cn.cotenite.ai.commons.enums.Errors

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/2 21:37
 */
class BusinessException(
    val code:Int,
    override val message:String
):Exception(message){

    constructor(message: String) : this(400,message)

    constructor(error: Errors) : this(error.code,error.message)



}