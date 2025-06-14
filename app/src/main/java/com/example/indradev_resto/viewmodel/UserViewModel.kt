package com.example.indradev_resto.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.indradev_resto.model.UserModel
import com.example.indradev_resto.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


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


    fun editProfile(
        userId: String,
        data: MutableMap<String, Any?>,
        callback: (Boolean, String, Boolean) -> Unit // third param = true if password changed
    ) {
        val newPassword = data["password"] as? String
        val user = FirebaseAuth.getInstance().currentUser

        val isPasswordChanged = !newPassword.isNullOrEmpty()

        // Ensure email cannot be updated
        data.remove("email")

        if (user != null && isPasswordChanged) {
            user.updatePassword(newPassword!!)
                .addOnSuccessListener {
                    repository.editProfile(userId, data) { success, message ->
                        callback(success, message, true) // password changed
                    }
                }
                .addOnFailureListener { exception ->
                    callback(false, exception.message ?: "Failed to update password", false)
                }
        } else {
            repository.editProfile(userId, data) { success, message ->
                callback(success, message, false)
            }
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

}
