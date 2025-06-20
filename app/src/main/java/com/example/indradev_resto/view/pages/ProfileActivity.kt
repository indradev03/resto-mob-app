package com.example.indradev_resto.view.pages

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.indradev_resto.repository.UserRepositoryImpl // <-- Use your actual implementation
import com.example.indradev_resto.utils.ImageUtils
import com.example.indradev_resto.viewmodel.UserViewModel

class ProfileActivity : ComponentActivity() {

    private lateinit var imageUtils: ImageUtils
    private lateinit var userViewModel: UserViewModel
    private var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize UserViewModel with your UserRepositoryImpl
        userViewModel = UserViewModel(UserRepositoryImpl())

        // Set up image picker
        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
        }

        // Set content
        setContent {
            ProfileScreen(
                userViewModel = userViewModel,
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() },
                onEditClick = {
                    // Navigate to edit profile screen if needed
                    // Example: startActivity(Intent(this, EditProfileActivity::class.java))
                },
                setSelectedImageUri = { selectedImageUri = it }
            )
        }
    }
}
