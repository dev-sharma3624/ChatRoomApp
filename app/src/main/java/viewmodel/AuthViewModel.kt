package viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import data.UserRepository
import kotlinx.coroutines.launch
import data.Result

class AuthViewModel : ViewModel() {
    private val userRepository : UserRepository
    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult : LiveData<Result<Boolean>>get() = _authResult

    val tag = "FIREBASE_TEST"


    init {
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
    }

    fun signUp(email : String, password : String, firstName : String, lastName : String){
        viewModelScope.launch {
            _authResult.value = userRepository.signUp(email, password, firstName, lastName)
        }
    }

    fun login(email: String, password: String){
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }
}