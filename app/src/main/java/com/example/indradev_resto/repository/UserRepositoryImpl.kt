package com.example.indradev_resto.repository

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.example.indradev_resto.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.InputStream
import java.util.concurrent.Executors

class UserRepositoryImpl(
    var auth: FirebaseAuth = FirebaseAuth.getInstance(),
    var database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    var ref: DatabaseReference = database.reference.child("users"),
    private val cloudinary: Cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "ddmtxv5fh",
            "api_key" to "578697515317363",
            "api_secret" to "qRZSA_ZCgxpW9L1ggHqytGdOSNE"
        )
    )
) : UserRepository {

    // Reference to "users" node in Firebase Realtime Database
    private val usersRef: DatabaseReference = database.reference.child("users")

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login Successful")
                } else {
                    callback(false, task.exception?.message ?: "Login failed")
                }
            }
    }

    override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Registration successful", auth.currentUser?.uid.orEmpty())
                } else {
                    callback(false, task.exception?.message ?: "Registration failed", "")
                }
            }
    }

    override fun addUserToDatabase(userId: String, model: UserModel, callback: (Boolean, String) -> Unit) {
        usersRef.child(userId).setValue(model)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "User added to database successfully")
                } else {
                    callback(false, task.exception?.message ?: "Failed to add user")
                }
            }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Reset email sent to $email")
                } else {
                    callback(false, task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? = auth.currentUser

    override fun getUserFromDatabase(userId: String, callback: (Boolean, String, UserModel?) -> Unit) {
        usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    callback(true, "User data retrieved", user)
                } else {
                    callback(false, "User not found", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun logout(callback: (Boolean, String) -> Unit) {
        try {
            auth.signOut()
            callback(true, "Logout Successful")
        } catch (e: Exception) {
            callback(false, e.message ?: "Logout failed")
        }
    }

    override fun editProfile(userId: String, data: MutableMap<String, Any?>, callback: (Boolean, String) -> Unit) {
        val cleanedData = data.filterValues { it != null }
        usersRef.child(userId).updateChildren(cleanedData)
            .addOnSuccessListener {
                callback(true, "Profile updated successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Unknown error occurred")
            }
    }

    override fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        usersRef.child(userId).removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "User removed from database.")
                } else {
                    callback(false, task.exception?.message ?: "Failed to remove user")
                }
            }
    }

    override fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
                callback(true, "Users fetched successfully", usersList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(imageUri)
                val fileName = getFileNameFromUri(context, imageUri)?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream,
                    ObjectUtils.asMap("public_id", fileName, "resource_type", "image")
                )

                val imageUrl = (response["url"] as? String)?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            } finally {
                inputStream?.close()
            }
        }
    }

    override fun updateProfileImage(user: UserModel, callback: (Boolean, String) -> Unit) {
        if (user.userId.isEmpty() || user.profileImageUrl.isNullOrEmpty()) {
            callback(false, "Invalid user ID or image URL")
            return
        }

        usersRef.child(user.userId).updateChildren(mapOf("profileImageUrl" to user.profileImageUrl))
            .addOnSuccessListener {
                callback(true, "Profile image updated successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to update profile image")
            }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    return it.getString(nameIndex)
                }
            }
        }
        return null
    }

    override fun getUserCount(callback: (Boolean, Int) -> Unit) {
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(true, snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, 0)
            }
        })
    }
}
