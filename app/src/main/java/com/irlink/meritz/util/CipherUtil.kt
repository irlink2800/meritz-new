package com.irlink.meritz.util

import android.util.Base64
import com.irlink.meritz.manager.DirectoryManager
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import org.apache.commons.text.StringEscapeUtils
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder
import java.net.URLEncoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

open class CipherUtil(

    var algorithm: String = "",

    var cipherKey: String = "",

    var transformation: String = "",

    private val fileUtil: FileUtil,

    private val directoryManager: DirectoryManager

) {

    companion object {
        const val TAG: String = "CipherUtil"
    }

    protected val encryptCipher: Cipher
        get() = createCipher(Cipher.ENCRYPT_MODE)

    protected val decryptCipher: Cipher
        get() = createCipher(Cipher.DECRYPT_MODE)

    fun createCipher(mode: Int): Cipher {
        val ivSpec = IvParameterSpec(cipherKey.substring(0, 16).toByteArray())
        val secretKey = SecretKeySpec(cipherKey.toByteArray(), algorithm)
        return Cipher.getInstance(transformation).apply {
            init(mode, secretKey, ivSpec)
        }
    }

    /**
     * 파일 암호화.
     */
    open fun encrypt(
        decFile: File,
        encFile: File = File(directoryManager.encDir, decFile.name),
        isDeleteDecFile: Boolean = true
    ): Flowable<File> = encryptCipher.doFinal(
        fromFile = decFile,
        toFile = encFile,
        isDeleteFromFile = isDeleteDecFile
    )

    /**
     * 암호화.
     */
    open fun encrypt(byteArray: ByteArray): ByteArray = encryptCipher.doFinal(byteArray)

    /**
     * 복호화.
     */
    open fun decrypt(byteArray: ByteArray): ByteArray = decryptCipher.doFinal(byteArray)

    /**
     * 파일 복호화.
     */
    open fun decrypt(
        encFile: File,
        decFile: File = File(directoryManager.decDir, encFile.name),
        isDeleteEncFile: Boolean = true
    ): Flowable<File> = decryptCipher.doFinal(
        fromFile = encFile,
        toFile = decFile,
        isDeleteFromFile = isDeleteEncFile
    )

    /**
     * 암, 복호화 작업
     */
    protected open fun Cipher.doFinal(
        fromFile: File,
        toFile: File,
        isDeleteFromFile: Boolean
    ): Flowable<File> = Flowable.create({
        var fileOutputStream: FileOutputStream? = null
        try {
            val doFinalData: ByteArray = doFinal(
                fromFile.readBytes()
            )
            if (toFile.exists()) {
                fileUtil.renameFile(
                    toFile, "temp_${System.currentTimeMillis()}_${toFile.name}"
                )
            }
            fileOutputStream = FileOutputStream(toFile).apply {
                write(doFinalData)
            }
            if (isDeleteFromFile) {
                fromFile.delete()
            }
            LogUtil.d(TAG, "doFinal: toFile: ${toFile.path} [${toFile.exists()}]")
            it.onNext(toFile)

        } catch (e: Exception) {
            it.onError(e)

        } finally {
            fileOutputStream?.close()
            it.onComplete()
        }
    }, BackpressureStrategy.BUFFER)

}

/**
 * Base64 인코딩.
 */
fun String.encodeBase64(flags: Int = Base64.NO_WRAP): String = try {
    Base64.encodeToString(toByteArray(), flags)
} catch (e: Exception) {
    LogUtil.exception(CipherUtil.TAG, e)
    ""
}

/**
 * Base64 디코딩.
 */
fun String.decodeBase64(flags: Int = Base64.NO_WRAP): String = try {
    String(Base64.decode(toByteArray(), flags))
} catch (e: Exception) {
    LogUtil.exception(CipherUtil.TAG, e)
    ""
}

/**
 * URL 인코딩.
 */
fun String?.toUrlEncode(enc: String = "UTF-8"): String = when (this.isNullOrEmpty()) {
    true -> ""
    else -> try {
        URLEncoder.encode(this, enc)
    } catch (e: Exception) {
        LogUtil.exception(CipherUtil.TAG, e)
        ""
    }
}

/**
 * URL 디코딩 (UTF-8)
 */
fun String?.toUrlDecode(enc: String = "UTF-8"): String = when (this.isNullOrEmpty()) {
    true -> ""
    else -> try {
        URLDecoder.decode(this, enc)
    } catch (e: Exception) {
        LogUtil.exception(CipherUtil.TAG, e)
        ""
    }
}

/**
 * Escape to String.
 */
fun String?.toUnescape(): String = when (this.isNullOrEmpty()) {
    true -> ""
    else -> try {
        StringEscapeUtils.unescapeHtml4(this)
    } catch (e: Exception) {
        LogUtil.exception(CipherUtil.TAG, e)
        ""
    }
}