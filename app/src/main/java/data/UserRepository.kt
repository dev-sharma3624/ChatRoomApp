package data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository (
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
){

    private val tag = "FIREBASE_TEST"

    suspend fun signUp(
        email : String,
        password : String,
        firstName : String,
        lastName : String
    ) : Result<Boolean> {
        return try{
            Log.d(tag, "Attempting to register user")
            auth.createUserWithEmailAndPassword(email, password).await()
            Log.d(tag, "User registerd with firebase")
            val user = User(firstName, lastName, email)
            saveUserToFirestore(user)
            Log.d(tag, "User saved to firestore")
            Result.Success(true)
        } catch (e : Exception){
            Log.d(tag, "Registration failed    ${e.message}")
            Result.Error(e)
        }
    }

    suspend fun login(email: String, password: String) : Result<Boolean> =
        try {
            Log.d(tag, "Attempting to Sign In")
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d(tag, "Sign in Successful")
            Result.Success(true)
        }catch (e : Exception){
            Log.d(tag, "Error   ${e.message}")
            Result.Error(e)
        }

    private suspend fun saveUserToFirestore(user : User){
        firestore.collection("users")
            .document(user.email)
            .set(user)
            .await()
    }

    suspend fun getCurrentUser(): Result<User> = try {
        val uid = auth.currentUser?.email
        if (uid != null) {
            Log.d(tag, "uid : $uid")
            val userDocument = firestore.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)
            if (user != null) {
                Log.d(tag, "user object: ${user.email}")
                Result.Success(user)
            } else {
                Result.Error(Exception("User data not found"))
            }
        } else {
            Result.Error(Exception("User not authenticated"))
        }
    } catch (e: Exception) {
        Log.d(tag, "Error   ${e.message}")
        Result.Error(e)
    }
}