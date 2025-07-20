package com.example.indradev_resto.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.indradev_resto.R
import com.example.indradev_resto.repository.UserRepositoryImpl
import com.example.indradev_resto.view.ui.theme.Indradev_RESTOTheme
import com.example.indradev_resto.viewmodel.UserViewModel

class LoginActivityResto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Indradev_RESTOTheme {
                Scaffold { innerPadding ->
                    RestoLoginBody(innerPadding)
                }
            }
        }
    }
}

@Composable
fun RestoLoginBody(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val repository = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repository) }

    val adminEmail = "admin@gmail.com"
    val adminPassword = "admin123"

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val activity = context as? Activity
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(Unit) {
        email = sharedPreferences.getString("email", "") ?: ""
        password = sharedPreferences.getString("password", "") ?: ""
    }

    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 5.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState,
        contentPadding = PaddingValues(vertical = 20.dp, horizontal = 20.dp, )
    ) {

        item {
            Image(
                painter = painterResource(R.drawable.restologo),
                contentDescription = null,
                modifier = Modifier
                    .height(350.dp)
                    .width(350.dp)
            )
        }

        item {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().testTag("loginEmailField"),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    placeholder = { Text(text = "Enter your email", color = Color.Gray) },
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null  // Clear error when user types
                    },
                    textStyle = TextStyle(color = Color.Black),
                    isError = emailError != null
                )

                // Show error message if any
                if (emailError != null) {
                    Text(
                        text = emailError ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }


        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().testTag("loginPasswordField"),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (!passwordVisibility)
                        PasswordVisualTransformation()
                    else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = passwordError != null,
                    suffix = {
                        Icon(
                            painter = painterResource(
                                if (!passwordVisibility)
                                    R.drawable.baseline_visibility_off_24
                                else R.drawable.baseline_visibility_24
                            ),
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                passwordVisibility = !passwordVisibility
                            }
                        )
                    },
                    placeholder = { Text(text = "******", color = Color.Gray) },
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null  // Clear error on input
                    },
                    textStyle = TextStyle(color = Color.Black)
                )

                if (passwordError != null) {
                    Text(
                        text = passwordError ?: "",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }


        item { Spacer(modifier = Modifier.height(10.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF0A84FF),
                            checkmarkColor = Color.White
                        )
                    )
                    Text(text = "Remember Me", color = Color.Black)
                }

                Text(
                    text = "Forget Password",
                    modifier = Modifier.clickable {
                        val intent = Intent(context, ForgetPasswordActivity::class.java)
                        context.startActivity(intent)
                    },
                    color = Color(0xFF0A84FF)
                )

            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }

        item {
            Button(
                onClick = {

                    var hasError = false

                    if (email.isBlank()) {
                        emailError = "Email is required"
                        hasError = true
                    }

                    if (password.isBlank()) {
                        passwordError = "Password is required"
                        hasError = true
                    }

                    if (hasError) return@Button


                    if (email == adminEmail && password == adminPassword) {
                        val intent = Intent(context, AdminDashboardActivityResto::class.java)
                        context.startActivity(intent)
                        activity?.finish()
                        activity?.overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                    } else {
                        userViewModel.login(email, password) { success, message ->
                            if (success) {
                                val userId = userViewModel.getCurrentUser()?.uid
                                if (userId != null) {
                                    userViewModel.getUserFromDatabase(userId) { dbSuccess, _, userModel ->
                                        if (dbSuccess && userModel != null) {
                                            editor.putString("email", email)
                                            editor.putString("password", password)
                                            editor.putString("firstName", userModel.firstName)
                                            editor.apply()

                                            val intent = Intent(context, DashboardActivityResto::class.java)
                                            context.startActivity(intent)
                                            activity?.finish()
                                            activity?.overridePendingTransition(
                                                android.R.anim.fade_in,
                                                android.R.anim.fade_out
                                            )
                                        } else {
                                            Toast.makeText(context, "Failed to fetch user info", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Login user ID not found", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("loginButton"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF))
            ) {
                Text("Login", color = Color.White)
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account? ", color = Color.Black)
                Text(
                    text = "Sign Up",
                    modifier = Modifier.clickable {
                        val intent = Intent(context, RegistrationActivityResto::class.java)
                        context.startActivity(intent)
                        activity?.overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                    },
                    color = Color(0xFF0A84FF)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RestoLoginPreview() {
    Indradev_RESTOTheme {
        RestoLoginBody(innerPadding = PaddingValues(0.dp))
    }
}
