package com.example.BookRecord

import android.content.SharedPreferences

class UserRepository (private val sharedPreferences: SharedPreferences) {

    fun registerUser(email: String, password: String, callback: (Boolean) -> Unit) {
        // 检查邮箱是否已注册
        if (sharedPreferences.contains(email)) {
            callback(false)
            return
        }

        // 模拟数据库插入操作
        sharedPreferences.edit().putString(email, password).apply()

        callback(true)
    }

    fun loginUser(email: String, password: String, callback: (Boolean) -> Unit) {
        val storedPassword = sharedPreferences.getString(email, null)
        if (storedPassword != null && storedPassword == password) {
            callback(true)
        } else {
            callback(false)
        }
    }
}
