package cn.cotenite.ai.commons.utils

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/3 00:54
 */
object GitUtil {

    fun extractProjectName(repoUrl: String): String {
        val parts = repoUrl.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val projectNameWithGit = parts[parts.size - 1]
        return projectNameWithGit.replace(".git", "")
    }


}