package com.irlink.meritz.util.message

import android.annotation.SuppressLint
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.*
import android.os.Build
import android.os.Bundle
import android.provider.Telephony.Mms
import android.provider.Telephony.Sms
import android.telephony.SmsMessage
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.google.android.mms.pdu_alt.GenericPdu
import com.google.android.mms.pdu_alt.PduParser
import com.irlink.meritz.util.LogUtil
import com.irlink.meritz.util.extension.emptyDisposable
import com.irlink.meritz.util.extension.format
import com.irlink.meritz.util.extension.runOnBackgroundThread
import com.irlink.meritz.util.extension.toV3
import com.tedpark.tedonactivityresult.rx2.TedRxOnActivityResult
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread

@SuppressLint("Recycle")
open class MessageUtil(

    private val applicationContext: Context,

    ) {

    companion object {
        const val TAG: String = "MessageUtil"
    }

    protected open val roleManager: RoleManager
        @RequiresApi(Build.VERSION_CODES.Q)
        get() = applicationContext.getSystemService(RoleManager::class.java)!!

    /**
     * 현재 앱이 문자 기본앱인지 체크.
     */
    open val isDefaultMessageApp: Boolean
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> roleManager.isRoleHeld(RoleManager.ROLE_SMS)
            else -> Sms.getDefaultSmsPackage(applicationContext) == applicationContext.packageName
        }

    /**
     * 문자 전송.
     */
    fun sendMessage(
        content: String,
        receiveNumber: String,
        imageUrls: List<String>? = null,
    ) {

    }

    /**
     * 이미지 다운로드.
     */
    open fun downloadImages(
        imageUrls: List<String>,
        onDownloaded: (bitmaps: Array<Bitmap>) -> Unit
    ): Disposable = runOnBackgroundThread {
        val bitmaps = mutableListOf<Bitmap>().apply {
            for (imageUrl in imageUrls) {
                var connection: HttpURLConnection?
                var inputStream: InputStream? = null
                try {
                    val url = URL(imageUrl).openConnection().apply {
                        doInput = true
                    }

                    connection = if (url is HttpsURLConnection) {
                        url
                    } else {
                        url as HttpURLConnection
                    }

                    connection.connect()

                    inputStream = connection.inputStream

                    val resize = 720
                    var bitmap = BitmapFactory.decodeStream(inputStream)

                    if (bitmap.width > 720 || bitmap.height > 720) {
                        val tempBitmap = bitmap
                        bitmap = when (bitmap.width > bitmap.height) {
                            true -> Bitmap.createScaledBitmap(
                                bitmap,
                                resize,
                                (bitmap.height * resize) / bitmap.width,
                                true
                            )

                            false -> Bitmap.createScaledBitmap(
                                bitmap,
                                (bitmap.width * resize) / bitmap.height,
                                resize,
                                true
                            )
                        }
                        tempBitmap.recycle()
                    }
                    this += bitmap

                } catch (e: Exception) {
                    LogUtil.exception(TAG, e)

                } finally {
                    inputStream?.close()
                }
            }
        }.toTypedArray()

        onDownloaded(bitmaps)
    }


    /**
     * 문자열이 140바이트보다 크면 MMS로 판단하여 true를 리턴.
     */
    protected open fun String.isMms(): Boolean =
        toByteArray(Charset.forName("euc-kr")).size >= 140

    /**
     * 문자 메시지 개수 리턴.
     */
    open fun getMessageCount(date: String? = null): Int = getSmsCount(date) + getMmsCount(date)

    /**
     * SMS 문자 개수 리턴
     */
    open fun getSmsCount(date: String? = null): Int = searchMessageCount(
        MessageHistory.Type.SMS, date
    )

    /**
     *  MMS 문자 개수 리턴
     */
    open fun getMmsCount(date: String? = null): Int = searchMessageCount(
        MessageHistory.Type.MMS, date
    )

    /**
     * 타입에 맞는 문자를 검색하는 메소드.
     */
    open fun searchMessages(
        messageType: MessageHistory.Type,
        date: String? = null
    ): List<MessageHistory> = mutableListOf<MessageHistory>().apply {
        val compareDate = date
            ?.replace(".", "")
            ?.replace("-", "")
            ?.replace("/", "")

        val cursor: Cursor = applicationContext.contentResolver.query(
            when (messageType) {
                MessageHistory.Type.SMS -> Sms.CONTENT_URI
                MessageHistory.Type.MMS -> Mms.CONTENT_URI
            },
            arrayOf(
                "_id",
                "date"
            ),
            null,
            null,
            null
        ) ?: return@apply

        val idColumnIndex = cursor.getColumnIndexOrThrow("_id")
        val dateColumnIndex = cursor.getColumnIndexOrThrow("date")

        while (cursor.moveToNext()) {
            val id = cursor.getLongOrNull(idColumnIndex)

            val timeStamp = when (messageType) {
                MessageHistory.Type.SMS -> cursor.getStringOrNull(dateColumnIndex)?.toLongOrNull()
                MessageHistory.Type.MMS -> (cursor.getLongOrNull(dateColumnIndex) ?: 0) * 1000
            }?.let {
                Date(it) format "yyyyMMdd"
            }
            if (compareDate == null || compareDate == timeStamp) {
                add(MessageHistory(id = id, type = messageType, timestamp = timeStamp?.toLong()))
            }
        }
        if (!cursor.isClosed) {
            cursor.close()
        }

    }

    /**
     * 타입에 맞는 문자 메시지 개수 리턴.
     */
    protected open fun searchMessageCount(
        messageType: MessageHistory.Type,
        date: String? = null
    ): Int =
        searchMessages(messageType, date).size

    /**
     * 발신 문자 찾기.
     */
    open fun searchSentMessage(messageType: MessageHistory.Type, uri: Uri): MessageHistory? {
        try {
            val cursor: Cursor = applicationContext.contentResolver?.query(
                uri,
                null,
                null,
                null,
                null
            ) ?: return null

            if (cursor.count == 1) {
                cursor.moveToFirst()
                return when (messageType) {
                    MessageHistory.Type.SMS -> {
                        val threadIdColumnIndex = cursor.getColumnIndex(Sms.Outbox.THREAD_ID)
                        val idColumnIndex = cursor.getColumnIndex(Sms.Outbox._ID)
                        val bodyColumnIndex = cursor.getColumnIndex(Sms.Outbox.BODY)
                        val dateColumnIndex = cursor.getColumnIndex(Sms.Outbox.DATE)
                        val readColumnIndex = cursor.getColumnIndex(Sms.Outbox.READ)
                        val addressColumnIndex = cursor.getColumnIndex(Sms.Outbox.ADDRESS)

                        Mms.Outbox.CONTENT_CLASS
                        MessageHistory(
                            id = cursor.getLongOrNull(idColumnIndex),
                            threadId = cursor.getLongOrNull(threadIdColumnIndex),
                            type = messageType,
                            body = cursor.getStringOrNull(bodyColumnIndex),
                            senderNumber = cursor.getString(addressColumnIndex),
                            readState = cursor.getIntOrNull(readColumnIndex),
                            timestamp = cursor.getStringOrNull(dateColumnIndex)?.toLongOrNull()
                        )
                    }

                    MessageHistory.Type.MMS -> {
                        val threadIdColumnIndex = cursor.getColumnIndex(Mms.Outbox.THREAD_ID)
                        val idColumnIndex = cursor.getColumnIndex(Mms.Outbox._ID)
                        val dateColumnIndex = cursor.getColumnIndex(Mms.Outbox.DATE)
                        val readColumnIndex = cursor.getColumnIndex(Mms.Outbox.READ)
                        val mmsId = cursor.getLongOrNull(idColumnIndex)
                        MessageHistory(
                            id = cursor.getLongOrNull(idColumnIndex),
                            threadId = cursor.getLongOrNull(threadIdColumnIndex),
                            type = messageType,
                            body = getMmsMessage(applicationContext, mmsId.toString()),
                            senderNumber = getMmsRemoteNumber(applicationContext, mmsId.toString()),
                            readState = cursor.getIntOrNull(readColumnIndex),
                            timestamp = cursor.getStringOrNull(dateColumnIndex)?.toLongOrNull()
                        )
                    }
                }
            } else {
                return null
            }
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 문자 메시지 리스트 리턴.
     */
    open fun getMessages(isUnReadOnly: Boolean = false): List<MessageHistory> =
        mutableListOf<MessageHistory>().apply {
            this += getSmsMessages(isUnReadOnly)
            this += getMmsMessages(isUnReadOnly)
        }

    open fun getSmsMessages(isUnReadOnly: Boolean = false): List<MessageHistory> =
        mutableListOf<MessageHistory>().apply {
            val cursor = applicationContext.contentResolver.query(
                Sms.CONTENT_URI,
                arrayOf(
                    Sms._ID,
                    Sms.BODY,
                    Sms.DATE,
                    Sms.READ,
                    Sms.ADDRESS
                ),
                null,
                null,
                "${Sms.DATE} DESC;"
            ) ?: return@apply

            val idColumnIndex = cursor.getColumnIndex(Sms._ID)
            val bodyColumnIndex = cursor.getColumnIndex(Sms.BODY)
            val dateColumnIndex = cursor.getColumnIndex(Sms.DATE)
            val readColumnIndex = cursor.getColumnIndex(Sms.READ)
            val addressColumnIndex = cursor.getColumnIndex(Sms.ADDRESS)

            while (cursor.moveToNext()) {
                val message = MessageHistory(
                    id = cursor.getLongOrNull(idColumnIndex),
                    type = MessageHistory.Type.SMS,
                    body = cursor.getStringOrNull(bodyColumnIndex),
                    senderNumber = cursor.getString(addressColumnIndex),
                    readState = cursor.getIntOrNull(readColumnIndex),
                    timestamp = cursor.getLongOrNull(dateColumnIndex)
                )
                if (!isUnReadOnly || !message.isRead) {
                    this += message
                }
                continue
            }
            if (!cursor.isClosed) {
                cursor.close()
            }
        }

    open fun getMmsMessages(isUnReadOnly: Boolean = false): List<MessageHistory> =
        mutableListOf<MessageHistory>().apply {
            val cursor = applicationContext.contentResolver.query(
                Mms.CONTENT_URI,
                arrayOf(
                    Mms._ID,
                    Mms.DATE,
                    Mms.READ
                ),
                null,
                null,
                "${Mms.DATE} DESC;"
            ) ?: return@apply

            val idColumnIndex = cursor.getColumnIndex(Mms._ID)
            val dateColumnIndex = cursor.getColumnIndex(Mms.DATE)
            val readStateColumnIndex = cursor.getColumnIndex(Mms.READ)

            while (cursor.moveToNext()) {
                val mmsId = cursor.getLongOrNull(idColumnIndex)
                val message = MessageHistory(
                    id = mmsId,
                    type = MessageHistory.Type.MMS,
                    body = getMmsMessage(applicationContext, mmsId.toString()),
                    senderNumber = getMmsRemoteNumber(applicationContext, mmsId.toString()),
                    readState = cursor.getIntOrNull(readStateColumnIndex),
                    timestamp = cursor.getLongOrNull(dateColumnIndex)
                )
                if (!isUnReadOnly || !message.isRead) {
                    this += message
                }
                continue
            }
            if (!cursor.isClosed) {
                cursor.close()
            }
        }

    /**
     * SMS 수신 정보를 리턴.
     */
    open fun parseReceiveSmsMessage(intent: Intent?): SmsMessage? {
        val messageBundle: Bundle? = intent?.extras
        val messageObjects: Array<*> = messageBundle?.get("pdus") as Array<*>
        val messages: Array<SmsMessage?> = arrayOfNulls(messageObjects.size)
        for (index: Int in messageObjects.indices) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val format: String? = messageBundle.getString("format")
                messages[index] =
                    SmsMessage.createFromPdu(messageObjects[index] as ByteArray, format)
            } else {
                messages[index] = SmsMessage.createFromPdu(messageObjects[index] as ByteArray)
            }
        }
        return messages[0]
    }

    /**
     * MMS 수신 정보를 리턴.
     * 코드 수정 필요.
     */
    open fun parseReceiveMmsMessage(
        context: Context?,
        response: (remoteNumber: String?, message: String?) -> Unit
    ): Disposable = runOnBackgroundThread {

        Thread.sleep(5000)

        val cursor: Cursor? = context?.contentResolver?.query(
            Mms.CONTENT_URI,
            arrayOf(Mms._ID),
            null,
            null,
            "${Mms._ID} desc limit 1"
        )
        if (cursor != null && cursor.count == 0) {
            cursor.close()
            response(null, null)

        } else {
            cursor?.moveToFirst()

            val mmsId: String = cursor!!.getString(cursor.getColumnIndex(Mms._ID))
            val remoteNumber: String? = getMmsRemoteNumber(context, mmsId)
            val message: String? = getMmsMessage(context, mmsId)

            cursor.close()
            response(remoteNumber, message)
        }
    }

    /**
     * MMS 상대방 번호 리턴.
     */
    protected open fun getMmsRemoteNumber(context: Context?, id: String?): String? {
        val selection = "msg_id=$id"
        val phoneNumberUri = Uri.parse("content://mms/$id/addr")
        val cursor = context?.contentResolver?.query(
            phoneNumberUri,
            null,
            selection,
            null,
            null
        )
        var remoteNumber = ""
        if (cursor != null && cursor.count == 0) {
            cursor.close()
            return null
        }
        if (cursor?.moveToFirst() == true) {
            do {
                val address = cursor.getString(cursor.getColumnIndexOrThrow("address"))
                if (address != null) {
                    remoteNumber = address.replace("-", "")
                }
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return remoteNumber
    }

    /**
     *  MMS 문자 메세지 리턴.
     *  코드 수정 필요.
     */
    protected open fun getMmsMessage(context: Context?, id: String?): String? {
        var result: String? = null

        // 조회에 조건을 넣게되면 가장 마지막 한두개의 mms를 가져오지 않는다.
        val cursor = context?.contentResolver?.query(
            Uri.parse("content://mms/part"),
            arrayOf("mid", "_id", "ct", "_data", "text"),
            null,
            null,
            null
        )
        if (cursor != null && cursor.count == 0) {
            cursor.close()
            return null
        }
        cursor?.moveToFirst()

        if (cursor != null) {
            while (!cursor.isAfterLast) {
                val mid = cursor.getString(cursor.getColumnIndex("mid"))
                if (id == mid) {
                    val partId = cursor.getString(cursor.getColumnIndex("_id"))
                    val type = cursor.getString(cursor.getColumnIndex("ct"))
                    if ("text/plain" == type) {
                        val data = cursor.getString(cursor.getColumnIndex("_data"))
                        result = if (TextUtils.isEmpty(data))
                            cursor.getString(cursor.getColumnIndex("text"))
                        else
                            parseMessageWithPartId(context, partId)
                    }
                }
                cursor.moveToNext()
            }
        }
        cursor?.close()
        return result
    }

    /**
     * MMS 다운로드.
     */
    open fun downloadMms(contentLocation: String, onDownloaded: (GenericPdu) -> Unit) {
        LogUtil.d(TAG, "downloadMms. contentLocation : $contentLocation")

        if (contentLocation.isEmpty()) {
            return
        }
        val connectManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectManager.requestNetwork(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                            connectManager.bindProcessToNetwork(network)
                        }

                        else -> {
                            ConnectivityManager.setProcessDefaultNetwork(network)
                        }
                    }
                    thread {
                        var connection: HttpURLConnection? = null
                        var inputStream: InputStream? = null
                        try {
                            val url = URL(contentLocation).openConnection().apply {
                                useCaches = false
                                readTimeout = 60_000
                                connectTimeout = 60_000
                            }

                            connection = if (url is HttpsURLConnection) {
                                url
                            } else {
                                url as HttpURLConnection
                            }

                            connection.connect()

                            var index = 0
                            val response = ByteArray(connection.contentLength)

                            inputStream = DataInputStream(connection.inputStream)

                            while (true) {
                                val read = inputStream.read()
                                if (read == -1) break
                                response[index++] = read.toByte()
                            }
                            onDownloaded(PduParser(response).parse())

                        } catch (e: Exception) {
                            LogUtil.exception(TAG, e)

                        } finally {
                            inputStream?.close()
                            connection?.disconnect()
                        }
                    }.join()

                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                            connectManager.bindProcessToNetwork(null)
                        }

                        else -> {
                            ConnectivityManager.setProcessDefaultNetwork(null)
                        }
                    }
                    connectManager.unregisterNetworkCallback(this)
                }
            })
    }

    /**
     * MMS 문자 이외 데이터가 포함된 경우 PartId를 리턴.
     * 코드 수정 필요.
     */
    protected open fun parseMessageWithPartId(context: Context, id: String?): String {
        val partUrl: Uri = Uri.parse("content://mms/part/$id")
        var inputStream: InputStream? = null
        val stringBuilder = StringBuilder()

        try {
            inputStream = context.contentResolver.openInputStream(partUrl)
            val inputStreamReader = InputStreamReader(inputStream!!, StandardCharsets.UTF_8)
            val bufferedReader = BufferedReader(inputStreamReader)
            var temp = bufferedReader.readLine()
            while (!TextUtils.isEmpty(temp)) {
                stringBuilder.append(temp)
                temp = bufferedReader.readLine()
            }
        } catch (e: IOException) {
            LogUtil.exception(TAG, e)
        } finally {
            inputStream?.close()
        }
        return stringBuilder.toString()
    }

    /**
     * 문자 전체 삭제.
     */
    open fun deleteMessages() {
        deleteSmsMessages()
        deleteMmsMessages()
        LogUtil.d(TAG, "deleteMessages. db.count:${getMessageCount()} ")
    }


    /**
     * SMS 전체 삭제.
     */
    open fun deleteSmsMessages() = applicationContext.contentResolver.delete(
        Sms.CONTENT_URI, null, null
    )

    /**
     * MMS 전체 삭제.
     */
    open fun deleteMmsMessages() = applicationContext.contentResolver.delete(
        Mms.CONTENT_URI, null, null
    )


    /**
     * 문자 삭제.
     */
    open fun deleteMessage(id: String) {
        deleteSmsMessage(id)
        deleteMmsMessage(id)
    }

    /**
     * SMS 삭제.
     */
    open fun deleteSmsMessage(id: String) = applicationContext.contentResolver.delete(
        Uri.parse("${Sms.CONTENT_URI}/$id"), null, null
    )

    /**
     * MMS 삭제.
     */
    open fun deleteMmsMessage(id: String) = applicationContext.contentResolver.delete(
        Uri.parse("${Mms.CONTENT_URI}/$id"), null, null
    )

    /**
     * 기본 문자 앱 설정.
     */
    open fun requestDefaultMessageApp(
        context: Context,
        onCallback: (isGranted: Boolean) -> Unit
    ): Disposable {
        if (isDefaultMessageApp) {
            onCallback(true)
            return emptyDisposable()
        }
        val intent = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
            }

            else -> Intent(Sms.Intents.ACTION_CHANGE_DEFAULT).apply {
                putExtra(Sms.Intents.EXTRA_PACKAGE_NAME, applicationContext.packageName)
            }
        }
        return TedRxOnActivityResult.with(context)
            .startActivityForResult(intent)
            .toV3()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    onCallback(isDefaultMessageApp)
                },
                onError = {
                    LogUtil.exception(TAG, it)
                }
            )
    }

}