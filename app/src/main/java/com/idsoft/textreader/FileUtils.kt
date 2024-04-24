package com.idsoft.textreader

import android.content.Context
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipInputStream


object FileUtils {
    // 从assets复制文件到内部存储
    fun copyFileFromAssets(
        context: Context,
        assetFileName: String?,
        targetFileName: String?
    ): Boolean {
        try {
            context.assets.open(assetFileName!!).use { inputStream ->
                context.openFileOutput(targetFileName, Context.MODE_PRIVATE)
                    .use { outputStream ->
                        val buffer = ByteArray(1024)
                        var length: Int
                        while (inputStream.read(buffer).also { length = it } > 0) {
                            outputStream.write(buffer, 0, length)
                        }
                        //inputStream.close()
                        //outputStream.close()
                        return true
                    }
            }
        } catch (e: IOException) {
            Log.e("FileUtils", "Error copying file from assets", e)
            return false
        }
    }

    // 解压ZIP文件到内部存储目录
    fun unzipFile(context: Context?, zipFilePath: String?, destPath: String): Boolean {
        val destDir = File(destPath)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        try {
            ZipInputStream(FileInputStream(zipFilePath)).use { zipIn ->
                var zipEntry = zipIn.nextEntry
                while (zipEntry != null) {
                    val filePath =
                        destPath + File.separator + zipEntry.name
                    if (!zipEntry.isDirectory) {
                        // 如果是文件，则解压
                        extractFile(zipIn, filePath)
                    } else {
                        // 如果是目录，则创建目录
                        val dir = File(filePath)
                        dir.mkdir()
                    }
                    zipIn.closeEntry()
                    zipEntry = zipIn.nextEntry
                }
                return true
            }
        } catch (e: IOException) {
            Log.e("FileUtils", "Error unzipping file", e)
            return false
        }
    }

    // 辅助方法：从ZipInputStream中提取文件
    @Throws(IOException::class)
    private fun extractFile(zipIn: ZipInputStream, filePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(filePath))
        val bytesIn = ByteArray(4096)
        var read = 0
        while (zipIn.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }
}