package com.example.indradev_resto.repository

import android.content.Context
import android.net.Uri
import com.example.indradev_resto.model.UserModel
import com.google.firebase.auth.FirebaseUser

interface UserRepository {

    fun login( email : String ,password : String, callback:(Boolean, String) -> Unit)

    fun register(
        email : String, password: String, callback: (Boolean, String,String) -> Unit
    )



    //real time database ko functionAdd commentMore actions
    fun addUserToDatabase(
        userId: String, model: UserModel,
        callback: (Boolean, String) -> Unit
    )

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)


    fun getCurrentUser(): FirebaseUser?


    fun getUserFromDatabase(
        userId: String,
        callback: (Boolean, String, UserModel?) -> Unit
    )
    // ? yesko matlab data huna ni sakxa na huna ni sakxa

    fun logout(callback: (Boolean, String) -> Unit)

    fun editProfile(
        userId: String,
        data: MutableMap<String, Any?>, // yole chai data fetch garne ho
        callback: (Boolean, String) -> Unit
    )

    fun deleteAccount(userId: String, callback: (Boolean, String) -> Unit)

    fun getAllUsers(callback: (Boolean, String, List<UserModel>) -> Unit)


    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context, uri: Uri): String?

    fun updateProfileImage(user: UserModel, callback: (Boolean, String) -> Unit)

}
