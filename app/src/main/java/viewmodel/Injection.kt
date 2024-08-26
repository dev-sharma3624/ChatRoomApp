package viewmodel

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object Injection {
    private val instance : FirebaseFirestore by lazy {
        Log.d("FIREBASE_TEST", "CREATING INSTANCE OF FIRESTORE")
        FirebaseFirestore.getInstance()
    }

    fun instance() : FirebaseFirestore{
        return instance
    }
}