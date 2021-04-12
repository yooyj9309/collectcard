package com.rainist.collectcard.common.util

import org.apache.commons.io.FileUtils
import org.springframework.util.ResourceUtils
import java.io.IOException
import java.nio.charset.StandardCharsets

class FileUtil {

    companion object {
        fun readText(fileInClassPath: String): String {
            try {
                val file = ResourceUtils.getFile(fileInClassPath)
                return FileUtils.readFileToString(file, StandardCharsets.UTF_8.toString())
            } catch (e: IOException) {
                throw RuntimeException("Fail to read file", e)
            }
        }
    }
}
