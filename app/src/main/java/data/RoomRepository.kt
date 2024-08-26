package data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RoomRepository (private val firestore: FirebaseFirestore) {

    val tag = "FIREBASE_TEST"

    suspend fun createRoom(name : String) : Result<Unit> =
        try {
            val room = Room(name = name)
            Log.d(tag, "Attempting to create room")
            firestore.collection("rooms").add(room).await()
            Log.d(tag, "Room created")
            Result.Success(Unit)
        }catch (e: Exception){
            Log.d(tag,"Error    ${e.message}")
            Result.Error(e)
        }

    /*suspend fun getRooms() : Flow<List<Room>> = callbackFlow {
        val subscription = firestore.collection("rooms")
            .addSnapshotListener{
                querySnapshot, _ ->
                querySnapshot?.let {
                    trySend(it.documents.map {
                        doc->
                        Log.d(tag, "doc id : ${doc.id}")
                        Log.d(tag, "doc data: ${doc.data}")
                        doc.toObject(Room::class.java)!!.copy(id = doc.id)
                    }.sortedBy { it.name }).isSuccess
                }
            }
        awaitClose{ subscription.remove()}
    }*/

    suspend fun getRooms() : Result<List<Room>> =
        try {
            Log.d(tag, "Call to getRooms")
            val querySnapshot = firestore.collection("rooms").get().await()
            Log.d(tag, "querySnapshot = ${querySnapshot.metadata}")
            val rooms = querySnapshot.documents.map {
                document ->
                document.toObject(Room::class.java)!!.copy(id = document.id)
            }.sortedBy { it.name }
            Log.d(tag, "rooms = $rooms")
            Log.d(tag, "Getting room data successful")
            Result.Success(rooms)
        }catch (e: Exception){
            Log.d(tag, "Error   ${e.message}")
            Result.Error(e)
        }
}