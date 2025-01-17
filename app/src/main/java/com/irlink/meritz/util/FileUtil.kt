package com.irlink.meritz.util

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.net.URLConnection
import java.util.*

open class FileUtil {

    companion object {
        const val TAG: String = "FileUtil"
    }

    /**
     * 파일 생성.
     */
    open fun createFile(file: File): Boolean = try {
        if (!file.exists()) {
            file.createNewFile().also { isCreated ->
                LogUtil.d(TAG, "createdFile: ${file.path} [$isCreated]")
            }
        } else {
            true
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        false
    }

    /**
     * 폴더 생성.
     */
    open fun createDirectory(directory: File): Boolean = try {
        if (!directory.exists()) {
            directory.mkdirs().also { isCreated ->
                LogUtil.d(TAG, "createdDirectory: ${directory.path} [$isCreated]")
            }
        } else {
            true
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        false
    }

    /**
     * 파일 삭제.
     */
    open fun deleteFile(file: File, isForceDelete: Boolean = false): Boolean = try {
        if (file.exists()) {
            when {
                file.isFile -> {
                    file.delete().also { isDeleted ->
                        LogUtil.d(TAG, "deletedFile: ${file.path} [$isDeleted]")
                    }
                }
                file.isDirectory -> {
                    if (isForceDelete) file.listFiles()?.forEach { child ->
                        deleteFile(child, true)
                    }
                    file.delete().also { isDeleted ->
                        LogUtil.d(TAG, "deletedDirectory: ${file.path} [$isDeleted]")
                    }
                }
                else -> false
            }
        } else {
            false
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        false
    }

    /**
     * 파일 이동.
     */
    open fun moveFile(targetFile: File, moveFile: File): Boolean = try {
        targetFile.renameTo(moveFile).also { isRenameTo ->
            LogUtil.d(TAG, "movedFile: ${targetFile.path} to ${moveFile.path} [$isRenameTo]")
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        false
    }

    /**
     * 폴더내 파일 리스트 이동.
     */
    open fun moveFiles(targetDir: File, moveDir: File): Boolean = try {
        if (targetDir.exists()) {
            if (!moveDir.exists()) {
                createDirectory(moveDir)
            }
            targetDir.listFiles()?.forEach { file ->
                moveFile(file, File(moveDir, file.name))
            }
            true
        } else {
            false
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        false
    }

    /**
     * 파일명 변경.
     */
    open fun renameFile(fromFile: File, rename: String): Boolean = renameFile(
        fromFile = fromFile,
        toFile = File(fromFile.parentFile, rename)
    )

    /**
     * 파일명 변경.
     */
    open fun renameFile(fromFile: File, toFile: File): Boolean = try {
        fromFile.renameTo(toFile).also { isRenamed ->
            LogUtil.d(TAG, "renamedFile: ${fromFile.name} to ${toFile.name} [$isRenamed]")
        }
    } catch (e: Exception) {
        LogUtil.exception(TAG, e)
        false
    }
}

/**
 * File to Uri.
 */
fun File.toUri(): Uri = Uri.fromFile(this)

/**
 * File to Uri.
 * 단 외부에 공개 가능한 Uri 형태로 리턴된다.
 */
fun File.toProvideUri(context: Context): Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", this)
} else {
    toUri()
}

/**
 * 파일의 확장자 리턴.
 */
val String.extension: String
    get() = substringAfterLast('.', "")

/**
 * 파일의 확장자를 제외한 파일명 리턴.
 */
val String.nameWithoutExtension: String
    get() = substringBeforeLast(".")

/**
 * 파일의 생성일자를 리턴.
 */
val File.lastModifiedDate: Date
    get() = Date(lastModified())

/**
 * 파일의 ByteArray MimeType 리턴.
 */
fun ByteArray.getMimeType(): String? {
    val inputStream = BufferedInputStream(ByteArrayInputStream(this))
    return try {
        URLConnection.guessContentTypeFromStream(inputStream)
    } catch (ignored: IOException) {
        null
    } finally {
        inputStream.close()
    }
}