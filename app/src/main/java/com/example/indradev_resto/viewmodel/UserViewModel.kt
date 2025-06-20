package com.example.indradev_resto.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.indradev_resto.model.UserModel
import com.example.indradev_resto.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.storage


// hamile kun repository sanga yolai connect garaune tei pass garne
class UserViewModel(val repository: UserRepository): ViewModel(){


    fun login( email : String ,
               password : String,
               callback:(Boolean, String) -> Unit
    ){
        repository.login(email, password, callback)
    }


    //     Authentication ko function
    fun register(
        email : String, password: String, callback: (Boolean, String,String) -> Unit
    ){
        repository.register(email, password ,callback)
    }



    //real time database ko functionAdd commentMore actions
    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    ){
        repository.addUserToDatabase(userId,model,callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit
    ){
        repository.forgetPassword(email,callback)
    }


    fun getCurrentUser(): FirebaseUser? {
        return repository.getCurrentUser()
    }



    private val _users = MutableLiveData<UserModel?>()
    val users : LiveData<UserModel?> get() = _users

    fun getUserFromDatabase(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    ){
        repository.getUserFromDatabase(userId) { success, message, users ->
            if (success){
                _users.postValue(users)
            } else {
                _users.postValue(null)
            }
            callback(success, message, users)
        }
    }


    fun logout(callback: (Boolean, String) -> Unit) {
        try {
            FirebaseAuth.getInstance().signOut()
            callback(true, "Logged out")
        } catch (e: Exception) {
            callback(false, e.message ?: "Logout failed")
        }
    }


    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit) {
        FirebaseDatabase.getInstance().reference.child("users").child(userId)
            .removeValue()
            .addOnSuccessListener {
                callback(true, "User deleted successfully.")
            }
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to delete user.")
            }
    }




    private val _allUsers = MutableLiveData<List<UserModel>>()
    val allUsers: LiveData<List<UserModel>> get() = _allUsers

    fun fetchAllUsers(callback: () -> Unit) {
        FirebaseDatabase.getInstance().reference.child("users")
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.children.mapNotNull { it.getValue(UserModel::class.java) }
                _allUsers.postValue(users)
         }
            .addOnFailureListener { exception ->
                _allUsers.postValue(emptyList())
            }


    }


    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)
     {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.e("Upload", "No user ID")
            callback(null)
            return
        }

        val storageRef = Firebase.storage.reference.child("profile_images/$userId.jpg")

        val uploadTask = storageRef.putFile(imageUri)

        uploadTask
            .addOnProgressListener {
                Log.d("Upload", "Uploading: ${it.bytesTransferred} / ${it.totalByteCount}")
            }
            .addOnPausedListener {
                Log.d("Upload", "Upload paused.")
            }
            .addOnCanceledListener {
                Log.d("Upload", "Upload canceled.")
            }
            .addOnSuccessListener {
                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.d("Upload", "Download URL: $uri")
                        callback(uri.toString())
                    }
                    .addOnFailureListener {
                        Log.e("Upload", "Download URL fetch failed: ${it.message}")
                        callback(null)
                    }
            }
            .addOnFailureListener {
                Log.e("Upload", "Upload failed: ${it.message}")
                callback(null)
            }
    }



    fun editProfile(
        userId: String,
        data: Map<String, Any?>, // Immutable map to avoid side effects
        callback: (Boolean, String, Boolean) -> Unit // third param = password changed
    ) {
        val mutableData = data.toMutableMap()
        val newPassword = mutableData["password"] as? String
        val user = FirebaseAuth.getInstance().currentUser
        val isPasswordChanged = !newPassword.isNullOrEmpty()

        // Remove email - assuming email update is handled separately or disallowed here
        mutableData.remove("email")

        if (user == null) {
            callback(false, "User not authenticated", false)
            return
        }

        if (isPasswordChanged) {
            user.updatePassword(newPassword!!)
                .addOnSuccessListener {
                    // Password updated successfully, now update other profile data
                    mutableData.remove("password") // Remove password from map before DB update
                    println("Password updated successfully for user $userId")
                    repository.editProfile(userId, mutableData) { success, message ->
                        println("editProfile repo result after password update: success=$success, message=$message")
                        callback(success, message, true)
                    }
                }
                .addOnFailureListener { exception ->
                    println("Password update failed: ${exception.message}")
                    callback(false, exception.message ?: "Failed to update password", false)
                }
        } else {
            // No password change, remove password if present just in case
            mutableData.remove("password")
            println("Updating profile for user $userId without password change")
            repository.editProfile(userId, mutableData) { success, message ->
                println("editProfile repo result: success=$success, message=$message")
                callback(success, message, false)
            }
        }
    }



}


