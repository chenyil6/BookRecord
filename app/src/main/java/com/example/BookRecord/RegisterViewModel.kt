package com.example.BookRecord


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> = _registrationStatus
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus
    val userAlreadyExists: Boolean
        get() = userRepository.userAlreadyExists

    fun checkAndInsertUser(uid: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = userRepository.checkAndInsertUser(uid)
        }
        return result
    }

    fun registerUser(email: String, password: String, userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = userRepository.registerUser(email, password, userInfo)
            withContext(Dispatchers.Main) {
                _registrationStatus.value = isSuccess
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = userRepository.loginUser(email, password)
            withContext(Dispatchers.Main) {
                _loginStatus.value = isSuccess
            }
        }
    }
}




