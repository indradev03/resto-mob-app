package com.example.indradev_resto.repository

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import com.example.indradev_resto.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors


import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils



class UserRepositoryImpl : UserRepository {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.reference.child("users")


    override fun login(email: String,
                       password: String,
                       callback: (Boolean, String) -> Unit)
    {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Login Successful")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { it ->
                if (it.isSuccessful) {
                    callback(true, "Registration successful", "${auth.currentUser?.uid}")
                } else {
                    callback(false, "${it.exception?.message}", "")
                }
            }
    }

    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId)
            .setValue(model)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "User added to database successfully")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Reset email sent to $email")
                } else {
                    callback(false, "${it.exception?.message}")
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }


    override fun getUserFromDatabase(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ) {
        ref.child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val user = snapshot.getValue(UserModel::class.java)
                        if (user != null) {
                            callback(true, "User data retrieved", user)
                        } else {
                            callback(false, "User not found", null)
                        }
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
            callback(true,"Logout Success")
        } catch (e : Exception) {
            callback(true, "${e.message}")
        }
    }

    override fun editProfile(
        userId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String) -> Unit
    ) {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        // Clean data to remove nulls
        val cleanedData = data.filterValues { it != null }

        userRef.updateChildren(cleanedData)
            .addOnSuccessListener {
                callback(true, "Profile updated successfully")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Unknown error occurred")
            }
    }




    override fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        // If this is admin trying to delete someone else:
        ref.child(userId).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "User removed from database.")
                } else {
                    callback(false, "Failed to remove user: ${it.exception?.message}")
                }
            }
    }


    override fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = mutableListOf<UserModel>()
                for (child in snapshot.children) {
                    val user = child.getValue(UserModel::class.java)
                    user?.let { usersList.add(it) }
                }
                callback(true, "Users fetched successfully", usersList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "ddmtxv5fh",
            "api_key" to "578697515317363",
            "api_secret" to "qRZSA_ZCgxpW9L1ggHqytGdOSNE"
        )
    )

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            var inputStream: InputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)
                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )
                var imageUrl = response["url"] as String?
                imageUrl = imageUrl?.replace("http://", "https://")

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
        val userId = user.userId
        val imageUrl = user.profileImageUrl

        if (userId.isEmpty() || imageUrl.isNullOrEmpty()) {
            callback(false, "Invalid user ID or image URL")
            return
        }

        val updates = mapOf<String, Any>(
            "profileImageUrl" to imageUrl
        )

        ref.child(userId)
            .updateChildren(updates)
            .addOnSuccessListener {
                callback(true, "Profile image updated successfully")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to update profile image")
            }
    }


    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    override fun getUserCount(callback: (Boolean, Int) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount.toInt()
                callback(true, count)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, 0)
            }
        })
    }


}
