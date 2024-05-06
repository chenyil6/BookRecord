package com.example.BookRecord

//class UserRepository (private val sharedPreferences: SharedPreferences) {
//
//    fun registerUser(email: String, password: String, callback: (Boolean) -> Unit) {
//        // 检查邮箱是否已注册
//        if (sharedPreferences.contains(email)) {
//            callback(false)
//            return
//        }
//
//        // 模拟数据库插入操作
//        sharedPreferences.edit().putString(email, password).apply()
//
//        callback(true)
//    }
//
//    fun loginUser(email: String, password: String, callback: (Boolean) -> Unit) {
//        val storedPassword = sharedPreferences.getString(email, null)
//        if (storedPassword != null && storedPassword == password) {
//            callback(true)
//        } else {
//            callback(false)
//        }
//    }
//}


import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
data class UserInfo(
    val email: String,
    val username: String?,
    val gender: String?,
    val phoneNumber: String?
)

class UserRepository(private val appDatabase: AppDatabase) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    // LiveData to track login success
    val loginSuccess = MutableLiveData<Boolean>()
    var userAlreadyExists = false
        private set  // 限制外部直接修改

    suspend fun registerUser(email: String, password: String, userInfo: UserInfo): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user
                //user?.sendEmailVerification()?.await()
                val userId = user?.uid ?: ""
                val storeUserInfoResult = storeUserInfoInFirebase(userId, userInfo)
                if (storeUserInfoResult) {
                    val newUser = User(uid = userId)
                    appDatabase.userDao().insert(newUser)
                    return@withContext true
                } else {
                    return@withContext false
                }
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }

    private suspend fun storeUserInfoInFirebase(userId: String, userInfo: UserInfo): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                FirebaseDatabase.getInstance("https://bookrecord-c2220-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(userId)
                    .setValue(userInfo)
                    .await()
                return@withContext true
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid ?: ""
                if (userId.isNotEmpty()) {
                    checkAndInsertUser(userId)
                    loginSuccess.postValue(true) // Post login success
                    return@withContext true
                } else {
                    loginSuccess.postValue(false) // Post login failure
                    return@withContext false
                }
            } catch (e: Exception) {
                loginSuccess.postValue(false) // Post login failure on exception
                return@withContext false
            }
        }
    }

    suspend fun checkAndInsertUser(uid: String): Boolean {
        return withContext(Dispatchers.IO) {
            val userExists = appDatabase.userDao().findUserById(uid)
            if (userExists == null) {
                try {
                    val newUser = User(uid = uid)
                    appDatabase.userDao().insert(newUser)
                    userAlreadyExists = false
                    true
                } catch (e: Exception) {
                    false
                }
            } else {
                userAlreadyExists = true
                false
            }
        }
    }
}
