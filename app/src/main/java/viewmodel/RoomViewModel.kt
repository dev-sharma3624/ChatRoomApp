package viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.Result
import data.Room
import data.RoomRepository
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms: LiveData<List<Room>> get() = _rooms
    private val roomRepository: RoomRepository

    init {
        roomRepository = RoomRepository(Injection.instance())
        Log.d("FIREBASE_TEST", "init invocation of loadRooms")
        loadRooms()
    }

    fun createRoom(name: String){
        viewModelScope.launch {
            roomRepository.createRoom(name)
            Log.d("FIREBASE_TEST", "createRoom invokation of loadRooms")
        }
        loadRooms()
    }

    /*fun loadRooms(){
        viewModelScope.launch {
            roomRepository.getRooms().collect{
                _rooms.value = it
            }
        }
    }*/

    fun loadRooms(){
        viewModelScope.launch{
            when (val result = roomRepository.getRooms()){
                is Result.Success -> _rooms.value = result.data
                is Result.Error -> {}
            }
        }
    }
}