package com.irlink.meritz.util.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.irlink.meritz.util.toUri
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import java.io.File

class RemoteStorageManager {

    companion object {
        const val TAG: String = "RemoteStorageManager"

        const val APP_LOGS: String = "applogs"
    }

    /**
     * 루트 저장소.
     */
    val rootStorage: StorageReference by lazy {
        Firebase.storage.reference
    }

    /**
     * 앱 로그 저장소.
     */
    val logStorage: StorageReference by lazy {
        rootStorage.child(APP_LOGS)
    }

}

/**
 * 업로드.
 */
fun StorageReference.upload(
    file: File,
    paths: List<String?>? = null,
    name: String? = null
): Flowable<UploadTask.TaskSnapshot> = Flowable.create({
    try {
        var ref = this
        if (paths != null) {
            for (path: String? in paths) {
                if (!path.isNullOrEmpty()) ref = ref.child(path)
            }
        }
        when (name.isNullOrEmpty()) {
            true -> file.name
            else -> name
        }.let { name ->
            ref = ref.child(name)
        }
        ref.putFile(file.toUri())
            .addOnSuccessListener { task ->
                it.onNext(task)
            }
            .addOnFailureListener { e ->
                it.onError(e)
            }
    } catch (e: Exception) {
        it.onError(e)
    }
}, BackpressureStrategy.BUFFER)