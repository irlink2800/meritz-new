package com.irlink.meritz.util.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable

class RemoteDbManager {

    object Table {
        const val USERS: String = "users"
    }

    val database: FirebaseDatabase by lazy {
        Firebase.database
    }

    val users: DatabaseReference by lazy {
        database.getReference(Table.USERS)
    }

}

/**
 * 데이터 쓰기.
 */
fun DatabaseReference.write(
    value: Any,
    paths: List<String>? = null
): Flowable<Unit> = Flowable.create({
    try {
        var ref = this
        if (paths != null) {
            for (path: String in paths) {
                if (path.isNotEmpty()) ref = ref.child(path)
            }
        }
        ref.setValue(value) { e, _ ->
            if (e != null) {
                it.onError(e.toException())
                return@setValue
            }
            it.onNext(Unit)
        }
    } catch (e: Exception) {
        it.onError(e)
    }
}, BackpressureStrategy.BUFFER)