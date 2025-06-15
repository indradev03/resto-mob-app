package com.example.indradev_resto.view.pages

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.indradev_resto.R
import com.example.indradev_resto.model.UserModel
import com.example.indradev_resto.view.LoginActivityResto
import com.example.indradev_resto.viewmodel.UserViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userViewModel: UserViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val userId = userViewModel.getCurrentUser()?.uid

    var userModel by remember { mutableStateOf<UserModel?>(null) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (!isLoaded && userId != null) {
            userViewModel.getUserFromDatabase(userId) { success, _, user ->
                if (success && user != null) {
                    userModel = user
                    firstName = user.firstName ?: ""
                    lastName = user.lastName ?: ""
                    email = user.email ?: ""
                    gender = user.selectedGender ?: ""
                    dob = user.selectedDate ?: ""
                    password = user.password ?: ""
                    country = user.selectedOptionText ?: ""
                    isLoaded = true
                } else {
                    Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    if (!isLoaded) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF007AFF))
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                "Edit Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name", color = Color(0xFF007AFF)) },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFAAAAAA),
                    focusedTextColor = Color(0xFF222222),
                    unfocusedTextColor = Color(0xFF222222),

                    cursorColor = Color(0xFF007AFF),
                    focusedLabelColor = Color(0xFF007AFF),
                    unfocusedLabelColor = Color(0xFF666666)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name", color = Color(0xFF007AFF)) },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFAAAAAA),
                    focusedTextColor = Color(0xFF222222),
                    unfocusedTextColor = Color(0xFF222222),
                    cursorColor = Color(0xFF007AFF),
                    focusedLabelColor = Color(0xFF007AFF),
                    unfocusedLabelColor = Color(0xFF666666)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email", color = Color(0xFF007AFF)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = outlinedTextFieldColors(
                    disabledTextColor = Color(0xFF999999),
                    disabledBorderColor = Color(0xFFCCCCCC),
                    disabledLabelColor = Color(0xFF999999),
                    disabledLeadingIconColor = Color(0xFF999999)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color(0xFF007AFF)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val iconRes = if (passwordVisible)
                        R.drawable.baseline_visibility_24
                    else
                        R.drawable.baseline_visibility_off_24
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = description,
                            tint = Color(0xFF007AFF)
                        )
                    }
                },
                colors = outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF007AFF),
                    unfocusedBorderColor = Color(0xFFAAAAAA),
                    focusedTextColor = Color(0xFF222222),
                    unfocusedTextColor = Color(0xFF222222),
                    cursorColor = Color(0xFF007AFF),
                    focusedLabelColor = Color(0xFF007AFF),
                    unfocusedLabelColor = Color(0xFF666666)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = gender,
                onValueChange = {},
                label = { Text("Gender", color = Color(0xFF007AFF)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = outlinedTextFieldColors(
                    disabledTextColor = Color(0xFF999999),
                    disabledBorderColor = Color(0xFFCCCCCC),
                    disabledLabelColor = Color(0xFF999999),
                    disabledLeadingIconColor = Color(0xFF999999)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = dob,
                onValueChange = {},
                label = { Text("Date of Birth", color = Color(0xFF007AFF)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = outlinedTextFieldColors(
                    disabledTextColor = Color(0xFF999999),
                    disabledBorderColor = Color(0xFFCCCCCC),
                    disabledLabelColor = Color(0xFF999999),
                    disabledLeadingIconColor = Color(0xFF999999)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            OutlinedTextField(
                value = country,
                onValueChange = {},
                label = { Text("Country", color = Color(0xFF007AFF)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false,
                colors = outlinedTextFieldColors(
                    disabledTextColor = Color(0xFF999999),
                    disabledBorderColor = Color(0xFFCCCCCC),
                    disabledLabelColor = Color(0xFF999999),
                    disabledLeadingIconColor = Color(0xFF999999)
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Button(
                onClick = {
                    if (userId != null) {
                        val updatedData = mutableMapOf<String, Any?>(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "password" to password
                        )

                        val oldPassword = userModel?.password ?: ""
                        val passwordChanged = password != oldPassword
                        val currentUser = FirebaseAuth.getInstance().currentUser

                        fun updateFirestore() {
                            userViewModel.editProfile(userId, updatedData) { success, message, requiresLogin ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    if (passwordChanged || requiresLogin) {
                                        val intent = Intent(context, LoginActivityResto::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(intent)
                                        activity?.finish()
                                    } else {
                                        activity?.finish()
                                    }
                                }
                            }
                        }

                        if (passwordChanged && currentUser != null) {
                            currentUser.updatePassword(password)
                                .addOnSuccessListener {
                                    updateFirestore()
                                }
                                .addOnFailureListener { exception ->
                                    if (exception is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException) {
                                        val emailAddress = currentUser.email
                                        if (!emailAddress.isNullOrEmpty()) {
                                            val credential = EmailAuthProvider.getCredential(emailAddress, oldPassword)
                                            currentUser.reauthenticate(credential)
                                                .addOnSuccessListener {
                                                    currentUser.updatePassword(password)
                                                        .addOnSuccessListener { updateFirestore() }
                                                        .addOnFailureListener {
                                                            Toast.makeText(context, "Password update failed after re-authentication", Toast.LENGTH_SHORT).show()
                                                        }
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(context, "Re-authentication failed. Please log in again.", Toast.LENGTH_SHORT).show()
                                                    val intent = Intent(context, LoginActivityResto::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    context.startActivity(intent)
                                                    activity?.finish()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(context, "Failed to update password: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            updateFirestore()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFF),
                    contentColor = Color.White
                )
            ) {
                Text("Save")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            OutlinedButton(
                onClick = { onBack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF007AFF)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Text("Cancel")
            }
        }
    }
}
