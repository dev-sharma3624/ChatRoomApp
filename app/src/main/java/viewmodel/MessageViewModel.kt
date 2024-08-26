package viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import data.Message
import data.MessageRepository
import data.Result
import data.User
import data.UserRepository
import kotlinx.coroutines.launch

class MessageViewModel: ViewModel() {
    private  val messageRepository: MessageRepository
    private val userRepository: UserRepository
    private val _messages = MutableLiveData<List<Message>>()
    val message: LiveData<List<Message>> = _messages
    private val _roomId = MutableLiveData<String>()
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    val tag = "FIREBASE_TEST"

    init {
        messageRepository = MessageRepository(Injection.instance())
        Log.d(tag, "messageRepo instantiated")
        userRepository = UserRepository(FirebaseAuth.getInstance(),
            Injection.instance()
        )
        Log.d(tag, "userRepo instantiated")
        loadCurrentUser()
        Log.d(tag, "[init bloc] Current user loaded Current User = ${currentUser.value}")
    }

    private fun loadCurrentUser(){
        viewModelScope.launch() {
            when(val result = userRepository.getCurrentUser()){
                is Result.Success -> _currentUser.value = result.data
                is Result.Error -> {}
            }
            Log.d(tag, "Current user loaded Current User = ${currentUser.value}")
            loadMessages()
        }
    }

    fun loadMessages(){
        viewModelScope.launch {
            Log.d(tag, "loadMessages called")
            messageRepository.getChatMessages(_roomId.value.toString())
                .collect{_messages.value = it}
            Log.d(tag, "Messages loaded")
        }
    }

    fun sendMessage(text: String){
        if(_currentUser.value != null){
            val message = Message(
                senderFirstName = _currentUser.value!!.firstName,
                senderId = _currentUser.value!!.email,
                text = text
            )
            viewModelScope.launch{
                when (messageRepository.sendMessage(_roomId.value.toString(), message)){
                    is Result.Success -> Unit
                    is Result.Error -> {}
                }
            }
        }
    }

    fun setRoomId(roomId: String){
        Log.d(tag, "setRoomId called")
        _roomId.value = roomId
        if(_currentUser.isInitialized){
            loadMessages()
        }
    }
}





















