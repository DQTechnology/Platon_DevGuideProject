package com.digquant.util

import java.io.*
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.StringBuilder

object FileUtil {
    fun CreateFile(dir: File?, filePath: String?): Boolean {
        val file = File(dir, filePath)
        return CreateFile(file)
    }

    fun CreateFile(filePath: String?): Boolean {
        val file = File(filePath)
        return CreateFile(file)
    }

    fun CreateFile(file: File): Boolean {
        return try {
            if (file.exists()) {
                return true
            }
            val dirPath = GetFileDir(file.absolutePath)
            val dir = File(dirPath)
            if (!dir.isDirectory) {
                val bSucceed = dir.mkdirs()
                if (!bSucceed) {
                    return false
                }
            }
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun IsFileExist(dir: File?, filePath: String?): Boolean {
        val file = File(dir, filePath)
        return file.exists()
    }

    fun IsFileExist(filePath: String?): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    fun ReadFileAsString(filePath: String?): String {
        val file = File(filePath)
        return ReadFileAsString(file)
    }

    fun ReadFileAsString(file: File): String {
        if (!file.exists()) {
            return ""
        }
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = FileInputStream(file)
            val fileSize = file.length()
            if (fileSize > Int.MAX_VALUE) {
                throw RuntimeException("文件过大: 最多只能读取4G文本文件!")
            }
            val buffer = ByteArray(fileSize.toInt())
            val readSize = fileInputStream.read(buffer)
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return ""
    }

    fun WriteStringToFile(dir: File?, filePath: String?, content: String?): Boolean {
        val bSucceed = CreateFile(dir, filePath)
        if (!bSucceed) {
            return false
        }
        val file = File(dir, filePath)
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file)
            fileWriter.write(content)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    fun WriteByteToNewFile(filePath: String?, bytes: ByteArray?): Boolean {
        val bSucceed = CreateFile(filePath)
        if (!bSucceed) {
            return false
        }
        val file = File(filePath)
        var bos: BufferedOutputStream? = null
        return try {
            bos = BufferedOutputStream(FileOutputStream(file))
            bos.write(bytes)
            bos.flush()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun GetFileDir(filePath: String): String {
        val pathArr = filePath.split("[/\\\\]".toRegex()).toTypedArray()
        if (pathArr.size == 1) {
            return "." + File.separator
        }
        val stringBuilder = StringBuilder()
        val len = pathArr.size - 1
        for (i in 0 until len) {
            stringBuilder.append(pathArr[i])
            stringBuilder.append(File.separator)
        }
        stringBuilder.setLength(stringBuilder.length - File.separator.length)
        return stringBuilder.toString()
    }

    fun DeleteFile(filePath: String?): Boolean {
        val file = File(filePath)
        if (file.isFile && file.exists()) {
            file.delete()
            return true
        }
        return false
    }

    fun DeleteFile(dir: File?, filePath: String?): Boolean {
        val file = File(dir, filePath)
        if (file.isFile && file.exists()) {
            file.delete()
            return true
        }
        return false
    }
}
