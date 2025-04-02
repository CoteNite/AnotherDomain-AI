package cn.cotenite.ai.commons.response

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/2 21:35
 */
data class Response(
    val code:Int,
    val message:String,
    val data:Any?
){
    companion object{

        fun success(data:Any?):Response{
            return Response(200,"success",data)
        }

        fun error(message:String):Response{
            return Response(500,message,null)
        }

        fun error(code:Int,message:String):Response{
            return Response(code,message,null)
        }


    }
}
