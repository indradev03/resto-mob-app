package com.example.indradev_resto.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.indradev_resto.view.ui.theme.Indradev_RESTOTheme
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Indradev_RESTOTheme {
                ForgetPasswordScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ForgetPasswordScreen() {
        var email by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current
        val activity = context as? Activity

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Reset Password") },
                    navigationIcon = {
                        IconButton(onClick = {
                            activity?.finish()
                            activity?.overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF0A84FF)
                            )
                        }
                    }
                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White) // ✅ Set white background here
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = {
                                    email = it
                                    emailError = null
                                },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Enter your email", color = Color.Gray) }, // Optional: placeholder color
                                label = { Text("Email", color = Color.Black) }, // Optional: label color
                                isError = emailError != null,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color.Black,
                                    focusedBorderColor = Color(0xFF0A84FF),
                                    unfocusedBorderColor = Color.Gray,
                                    errorTextColor = Color.Red
                                )
                            )

                            if (emailError != null) {
                                Text(
                                    text = emailError ?: "",
                                    color = Color.Red,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (email.isBlank()) {
                                        emailError = "Email is required"
                                        return@Button
                                    }

                                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Reset link sent to your email",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                activity?.finish()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to send reset email",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0A84FF),     // Background color
                                    contentColor = Color.White              // Text color
                                )
                            ) {
                                Text("Send Reset Link")
                            }

                        }
                    }
                }
            }
        )
    }
}
