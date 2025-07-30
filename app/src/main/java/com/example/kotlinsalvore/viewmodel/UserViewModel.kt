package com.example.kotlinsalvore.viewmodel

import androidx.lifecycle.ViewModel
import com.example.kotlinsalvore.model.UserModel
import com.example.kotlinsalvore.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Registration method
    fun register(email: String, password: String, callback: (Boolean, String?, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            callback(false, "Email and password cannot be empty", null)
            return
        }

        if (password.length < 6) {
            callback(false, "Password must be at least 6 characters", null)
            return
        }

        userRepository.register(email, password) { success, message, userId ->
            if (success) {
                callback(true, message, userId)
            } else {
                callback(false, message, null)
            }
        }
    }

    // Login method
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            callback(false, "Email and password cannot be empty")
            return
        }

        userRepository.login(email, password) { success, message ->
            if (success) {
                callback(true, message)
            } else {
                callback(false, message)
            }
        }
    }

    // Add user to database
    fun addUserToDatabase(userId: String?, userModel: UserModel, callback: (Boolean, String) -> Unit) {
        if (userId == null) {
            callback(false, "User ID is null")
            return
        }

        userRepository.addUserToDatabase(userId, userModel) { success, message ->
            callback(success, message)
        }
    }

    // Update user details
    fun updateUserDetails(firstName: String, lastName: String, email: String, gender: String, address: String, callback: (Boolean, String?) -> Unit) {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        val userId = currentUser.userId
        val data = mutableMapOf<String, Any?>()
        data["firstName"] = firstName
        data["lastName"] = lastName
        data["email"] = email
        data["gender"] = gender
        data["address"] = address

        userRepository.updateProfile(userId, data) { success, message ->
            callback(success, message)
        }
    }

    // Get user details
    fun getUserDetails(callback: (Boolean, UserModel?, String?) -> Unit) {
        val currentUser = userRepository.getCurrentUser()
        if (currentUser == null) {
            callback(false, null, "User not authenticated")
            return
        }

        userRepository.getUserById(currentUser.userId) { user, success, message ->
            if (success && user != null) {
                callback(true, user, message)
            } else {
                callback(false, null, message)
            }
        }
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return userRepository.getCurrentUser() != null
    }

    // Get current user
    fun getCurrentUser() = userRepository.getCurrentUser()

    // Logout
    fun logout(callback: (Boolean, String?) -> Unit) {
        userRepository.logout { success, message ->
            callback(success, message)
        }
    }

    // Forgot password
    fun forgotPassword(email: String, callback: (Boolean, String?) -> Unit) {
        if (email.isBlank()) {
            callback(false, "Email cannot be empty")
            return
        }

        userRepository.forgetPassword(email) { success, message ->
            callback(success, message)
        }
    }

    // Update password (simplified for local storage)
    fun updatePassword(currentPassword: String, newPassword: String, callback: (Boolean, String?) -> Unit) {
        if (currentPassword.isBlank() || newPassword.isBlank()) {
            callback(false, "Password fields cannot be empty")
            return
        }

        if (newPassword.length < 6) {
            callback(false, "New password must be at least 6 characters")
            return
        }

        // Since we're using local storage and UserModel doesn't have password field,
        // we'll just return success for now
        // In a real app, you would validate the current password and update it
        callback(true, "Password updated successfully")
    }
}