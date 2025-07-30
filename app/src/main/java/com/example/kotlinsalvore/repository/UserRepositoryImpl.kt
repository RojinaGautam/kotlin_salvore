package com.example.kotlinsalvore.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.kotlinsalvore.model.UserModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserRepositoryImpl : UserRepository {

    private var context: Context? = null
    private val gson = Gson()
    private val usersKey = "local_users"
    private val currentUserKey = "current_user"
    
    // Local storage for users
    private val localUsers = mutableListOf<UserModel>()
    private var currentUser: UserModel? = null

    private fun getSharedPreferences(): SharedPreferences? {
        return context?.getSharedPreferences("UserStorage", Context.MODE_PRIVATE)
    }

    private fun saveUsersToLocal() {
        getSharedPreferences()?.edit()?.apply {
            putString(usersKey, gson.toJson(localUsers))
            apply()
        }
    }

    private fun loadUsersFromLocal() {
        val json = getSharedPreferences()?.getString(usersKey, "[]")
        val type = object : TypeToken<List<UserModel>>() {}.type
        val loadedUsers = gson.fromJson<List<UserModel>>(json, type) ?: emptyList()
        localUsers.clear()
        localUsers.addAll(loadedUsers)
    }

    private fun saveCurrentUser() {
        getSharedPreferences()?.edit()?.apply {
            putString(currentUserKey, gson.toJson(currentUser))
            apply()
        }
    }

    private fun loadCurrentUser() {
        val json = getSharedPreferences()?.getString(currentUserKey, null)
        currentUser = if (json != null) {
            gson.fromJson(json, UserModel::class.java)
        } else {
            null
        }
    }

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            loadUsersFromLocal()
            val user = localUsers.find { it.email == email }
            
            if (user != null) {
                currentUser = user
                saveCurrentUser()
                callback(true, "Login successful")
            } else {
                callback(false, "Invalid email")
            }
        } catch (e: Exception) {
            callback(false, "Login failed: ${e.message}")
        }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        try {
            loadUsersFromLocal()
            
            // Check if user already exists
            if (localUsers.any { it.email == email }) {
                callback(false, "User with this email already exists", "")
                return
            }
            
            // Create new user
            val userId = (localUsers.size + 1).toString()
            val newUser = UserModel(
                userId = userId,
                email = email,
                firstName = "User",
                lastName = userId,
                gender = "",
                address = ""
            )
            
            localUsers.add(newUser)
            saveUsersToLocal()
            
            callback(true, "Registration successful", userId)
        } catch (e: Exception) {
            callback(false, "Registration failed: ${e.message}", "")
        }
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            loadUsersFromLocal()
            
            // Check if user already exists
            val existingIndex = localUsers.indexOfFirst { it.userId == userId }
            if (existingIndex != -1) {
                localUsers[existingIndex] = model
            } else {
                localUsers.add(model)
            }
            
            saveUsersToLocal()
            callback(true, "User added successfully")
        } catch (e: Exception) {
            callback(false, "Failed to add user: ${e.message}")
        }
    }

    override fun updateProfile(
        userId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            loadUsersFromLocal()
            val userIndex = localUsers.indexOfFirst { it.userId == userId }
            
            if (userIndex != -1) {
                val user = localUsers[userIndex]
                
                // Update user fields
                data["firstName"]?.let { user.firstName = it.toString() }
                data["lastName"]?.let { user.lastName = it.toString() }
                data["email"]?.let { user.email = it.toString() }
                data["gender"]?.let { user.gender = it.toString() }
                data["address"]?.let { user.address = it.toString() }
                
                saveUsersToLocal()
                
                // Update current user if it's the same user
                if (currentUser?.userId == userId) {
                    currentUser = user
                    saveCurrentUser()
                }
                
                callback(true, "Profile updated successfully")
            } else {
                callback(false, "User not found")
            }
        } catch (e: Exception) {
            callback(false, "Failed to update profile: ${e.message}")
        }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        try {
            loadUsersFromLocal()
            val user = localUsers.find { it.email == email }
            
            if (user != null) {
                // In a real app, you would send an email here
                // For now, we'll just return success
                callback(true, "Password reset instructions sent to $email")
            } else {
                callback(false, "No user found with this email")
            }
        } catch (e: Exception) {
            callback(false, "Failed to process password reset: ${e.message}")
        }
    }

    override fun getCurrentUser(): UserModel? {
        loadCurrentUser()
        return currentUser
    }

    override fun getUserById(
        userId: String,
        callback: (UserModel?, Boolean, String) -> Unit
    ) {
        try {
            loadUsersFromLocal()
            val user = localUsers.find { it.userId == userId }
            
            if (user != null) {
                callback(user, true, "User found")
            } else {
                callback(null, false, "User not found")
            }
        } catch (e: Exception) {
            callback(null, false, "Failed to get user: ${e.message}")
        }
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            currentUser = null
            saveCurrentUser()
            callback(true, "Logged out successfully")
        } catch (e: Exception) {
            callback(false, "Failed to logout: ${e.message}")
        }
    }

    // Helper method to set context
    fun setContext(context: Context) {
        this.context = context
    }

    // Helper method to get current local user
    fun getCurrentLocalUser(): UserModel? {
        loadCurrentUser()
        return currentUser
    }
}