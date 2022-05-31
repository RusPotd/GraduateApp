package com.r.graduateregistration.domain.util

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


private const val TAG = "FirebaseExtensions"

const val ERROR_DOCUMENT_NOT_EXIST = "Document does not exist."

internal suspend inline fun <reified T, R> CollectionReference.getAwaitResult(mapper: (T) -> R): List<R> {
    return get()
        .await()
        .toObjects(T::class.java)
        .map { mapper(it) }
}

internal suspend inline fun <reified T, R> DocumentReference.getAwaitResult(mapper: (T) -> R): R {
    val result = get()
        .await()
        .toObject(T::class.java)
        ?: throw Exception(ERROR_DOCUMENT_NOT_EXIST)

    return mapper(result)
}

internal suspend inline fun <reified T, R> Query.getAwaitResult(mapper: (T) -> R): List<R> {
    return get()
        .await()
        .toObjects(T::class.java)
        .map { mapper(it) }
}
