package com.example.indradev_resto.view.pages

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.indradev_resto.R
import com.example.indradev_resto.model.UserModel
import com.example.indradev_resto.view.LoginActivityResto
import com.example.indradev_resto.viewmodel.UserViewModel



@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    selectedImageUri: Uri?,
    onPickImage: () -> Unit,
    onEditClick: () -> Unit,
    setSelectedImageUri: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var userModel by remember { mutableStateOf<UserModel?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }



    // Load user profile
    LaunchedEffect(Unit) {
        val userId = userViewModel.getCurrentUser()?.uid
        if (userId == null) {
            isLoadingProfile = false
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return@LaunchedEffect
        }

        userViewModel.getUserFromDatabase(userId) { success, message, user ->
            isLoadingProfile = false
            if (success && user != null) {
                userModel = user
            } else {
                Toast.makeText(context, message ?: "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Upload image on URI change
    LaunchedEffect(selectedImageUri) {
        if (selectedImageUri == null || userModel == null) return@LaunchedEffect

        isUploading = true
        userViewModel.uploadImage(context, selectedImageUri) { imageUrl ->
            isUploading = false
            if (imageUrl != null) {
                Log.d("ProfileScreen", "Image uploaded: $imageUrl")
                val data = mapOf("profileImageUrl" to imageUrl)
                userViewModel.editProfile(userModel!!.userId, data) { success, message, _ ->
                    Log.d("ProfileScreen", "Profile update: success=$success, msg=$message")
                    if (success) {
                        userModel = userModel?.copy(profileImageUrl = imageUrl)
                        setSelectedImageUri(null)
                    } else {
                        Toast.makeText(context, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {

            }
        }
    }



    if (isLoadingProfile) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
                .background(Color.White).clickable{
                    onPickImage()
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                isUploading -> CircularProgressIndicator()
                selectedImageUri != null -> AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected profile image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.default_user),
                    error = painterResource(R.drawable.default_user)
                )
                !userModel?.profileImageUrl.isNullOrEmpty() -> AsyncImage(
                    model = userModel!!.profileImageUrl,
                    contentDescription = "Profile image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.default_user),
                    error = painterResource(R.drawable.default_user)
                )
                else -> Image(
                    painter = painterResource(R.drawable.default_user),
                    contentDescription = "Default profile image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

//            // Camera icon button
//            IconButton(
//                onClick = onPickImage,
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .offset(x = 4.dp, y = 4.dp)
//                    .size(36.dp)
//                    .background(Color.White, CircleShape)
//                    .alpha(if (isUploading) 0.3f else 1f),
//                enabled = !isUploading
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_camera),
//                    contentDescription = "Change photo",
//                    tint = Color.Gray
//                )
//            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userModel?.firstName ?: "No Name",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = userModel?.email ?: "",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Edit Profile")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                userViewModel.logout { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        context.startActivity(
                            Intent(context, LoginActivityResto::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "App Version 1.0.0",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
