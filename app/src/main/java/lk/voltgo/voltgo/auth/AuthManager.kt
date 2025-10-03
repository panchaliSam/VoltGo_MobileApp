package lk.voltgo.voltgo.auth

import javax.inject.Inject

class AuthManager @Inject constructor(){

    fun isLoggedIn(): Boolean {
        // Implement actual authentication check logic here
        return true
    }

    fun login(): Boolean {
        // Implement login logic here
        return true
    }

    fun logout(): Boolean {
        // Implement logout logic here
        return false
    }

}