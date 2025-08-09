package com.travelassistant.travella.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.travelassistant.travella.data.database.UserDatabase
import com.travelassistant.travella.data.model.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = UserDatabase.getDatabase(application).userDao()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = userDao.getUser(email, password)
            if (user != null) {
                _loginSuccess.value = true
                _loginError.value = null
            } else {
                _loginSuccess.value = false
                _loginError.value = "Invalid email or password"
            }
        }
    }

    fun signup(user: UserEntity, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser == null) {
                userDao.insertUser(user)
                onSuccess()
            } else {
                onFailure("Email already registered")
            }
        }
    }
}
