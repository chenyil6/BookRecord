package com.example.BookRecord

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class RegisterViewModel (private val userRepository: UserRepository) : ViewModel() {

    private val _registrationStatus = MutableLiveData<Boolean>()
    val registrationStatus: LiveData<Boolean> = _registrationStatus
    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    fun registerUser(email: String, password: String) {
        // 直接调用userRepository进行注册逻辑
        userRepository.registerUser(email, password) { isSuccess ->
            _registrationStatus.postValue(isSuccess)
        }
    }

    fun loginUser(email: String, password: String) {
        userRepository.loginUser(email, password) { isSuccess ->
            _loginStatus.postValue(isSuccess)
        }
    }
}


