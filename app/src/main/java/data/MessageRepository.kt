package data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepository(private val firestore: FirebaseFirestore) {
    
    val tag = "FIREBASE_TEST"

    suspend fun sendMessage(roomId: String, message: Message): Result<Unit> =
        try {
            Log.d(tag, "Attempting to send Message")
            firestore.collection("rooms").document(roomId)
                .collection("messages").add(message).await()
            Log.d(tag, "Message Sent Successfully")
            Result.Success(Unit)
        }catch (e: Exception){
            Log.d(tag, "Error   ${e.message}")
            Result.Error(e)
        }
    
   fun getChatMessages(roomId: String): Flow<List<Message>> = callbackFlow {
        val subscription   = firestore.collection("rooms")
            .document(roomId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener{
                querySnapshot, _ ->
                querySnapshot?.let {
                    trySend(it.documents.map {
                        doc->
                        doc.toObject(Message::class.java)!!
                    }).isSuccess
                }
            }
        awaitClose {subscription.remove()}
   }
}