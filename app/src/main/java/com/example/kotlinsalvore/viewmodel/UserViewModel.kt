package com.example.kotlinsalvore.repository

import androidx.lifecycle.ViewModel
import com.example.kotlinsalvore.model.UserModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

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

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    callback(true, "Registration successful", userId)
                } else {
                    val errorMessage = task.exception?.message ?: "Registration failed"
                    callback(false, errorMessage, null)
                }
            }
    }

    // Login method
    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            callback(false, "Email and password cannot be empty")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login successful")
                } else {
                    val errorMessage = when {
                        task.exception?.message?.contains("user-not-found") == true ->
                            "No account found with this email"
                        task.exception?.message?.contains("wrong-password") == true ->
                            "Incorrect password"
                        task.exception?.message?.contains("invalid-email") == true ->
                            "Invalid email format"
                        task.exception?.message?.contains("user-disabled") == true ->
                            "This account has been disabled"
                        else -> task.exception?.message ?: "Login failed"
                    }
                    callback(false, errorMessage)
                }
            }
    }

    // Add user to database
    fun addUserToDatabase(userId: String?, userModel: UserModel, callback: (Boolean, String) -> Unit) {
        if (userId == null) {
            callback(false, "User ID is null")
            return
        }

        val usersRef = database.reference.child("users").child(userId)

        usersRef.setValue(userModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "User data saved successfully")
                } else {
                    val errorMessage = task.exception?.message ?: "Failed to save user data"
                    callback(false, errorMessage)
                }
            }
    }

    // Update user details in Firebase Database and Auth
    fun updateUserDetails(fullName: String, email: String, phoneNumber: String, callback: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        val userId = currentUser.uid
        val usersRef = database.reference.child("users").child(userId)

        // First update the database
        val userUpdates = mapOf(
            "fullName" to fullName,
            "email" to email,
            "phoneNumber" to phoneNumber
        )

        usersRef.updateChildren(userUpdates)
            .addOnCompleteListener { databaseTask ->
                if (databaseTask.isSuccessful) {
                    // If database update successful and email changed, update Firebase Auth email
                    if (currentUser.email != email) {
                        currentUser.updateEmail(email)
                            .addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    callback(true, "Profile updated successfully")
                                } else {
                                    // Database updated but email update failed
                                    val errorMessage = emailTask.exception?.message ?: "Failed to update email in authentication"
                                    callback(false, "Profile updated but email change failed: $errorMessage")
                                }
                            }
                    } else {
                        callback(true, "Profile updated successfully")
                    }
                } else {
                    val errorMessage = databaseTask.exception?.message ?: "Failed to update profile"
                    callback(false, errorMessage)
                }
            }
    }

    // Update password
    fun updatePassword(currentPassword: String, newPassword: String, callback: (Boolean, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, "User not authenticated")
            return
        }

        val email = currentUser.email
        if (email == null) {
            callback(false, "User email not found")
            return
        }

        // Re-authenticate user with current password
        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        currentUser.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    // Update password
                    currentUser.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                callback(true, "Password updated successfully")
                            } else {
                                val errorMessage = when {
                                    updateTask.exception?.message?.contains("weak-password") == true ->
                                        "Password is too weak"
                                    else -> updateTask.exception?.message ?: "Failed to update password"
                                }
                                callback(false, errorMessage)
                            }
                        }
                } else {
                    val errorMessage = when {
                        reauthTask.exception?.message?.contains("wrong-password") == true ->
                            "Current password is incorrect"
                        else -> reauthTask.exception?.message ?: "Authentication failed"
                    }
                    callback(false, errorMessage)
                }
            }
    }

    // Get user details from database
    fun getUserDetails(callback: (Boolean, UserModel?, String?) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false, null, "User not authenticated")
            return
        }

        val userId = currentUser.uid
        val usersRef = database.reference.child("users").child(userId)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userModel = snapshot.getValue(UserModel::class.java)
                    if (userModel != null) {
                        callback(true, userModel, "User details retrieved successfully")
                    } else {
                        callback(false, null, "Failed to parse user data")
                    }
                } else {
                    callback(false, null, "User data not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null, error.message)
            }
        })
    }

    // Get current user
    fun getCurrentUser() = auth.currentUser

    // Sign out
    fun signOut() {
        auth.signOut()
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Password reset
    fun resetPassword(email: String, callback: (Boolean, String) -> Unit) {
        if (email.isBlank()) {
            callback(false, "Email cannot be empty")
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password reset email sent")
                } else {
                    val errorMessage = task.exception?.message ?: "Failed to send reset email"
                    callback(false, errorMessage)
                }
            }
    }
}