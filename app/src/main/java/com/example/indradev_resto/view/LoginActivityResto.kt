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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.indradev_resto.R
import com.example.indradev_resto.repository.UserRepositoryImpl
import com.example.indradev_resto.viewmodel.UserViewModel


class LoginActivityResto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                // This is the base (Scaffold)
                Scaffold { innerPadding->
                    RestoLoginBody(innerPadding)
                }
            }
        }
    }
}


@Composable
fun RestoLoginBody(innerPadding: PaddingValues)
{

    // LocalContext le arko file sanga add garauxa
    val context = LocalContext.current  // Add this at the top of LoginBody()


    val repository = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repository) }


    // email
    var  email by remember { mutableStateOf("") }
    //Password
    var  password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    //Checkbox
    var rememberMe by remember { mutableStateOf(false) }

    // for connecting registeration
    val activity = context as? Activity

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    val localEmail : String? = sharedPreferences.getString("email","")
    val localpassword : String? = sharedPreferences.getString("password","")


    //yo lai cmt hanye remember me wala passowrd hatxa
    LaunchedEffect(Unit) {
        val localEmail = sharedPreferences.getString("email", "")
        val localPassword = sharedPreferences.getString("password", "")
        email = localEmail ?: ""
        password = localPassword ?: ""
    }

    val editor = sharedPreferences.edit()


    Column (
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(color = Color.White)
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Top or LOGO Image
        Image(
            painter = painterResource(R.drawable.restologo),
            contentDescription = null,
            modifier = Modifier
                .height(350.dp)
                .width(350.dp)
        )


        // For Email
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(top = 50.dp),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

            placeholder = {
                Text(
                    text = "Enter your email",

                    )
            },
            value = email,
            onValueChange = { input -> email = input }
        )

        Spacer(modifier = Modifier.height(20.dp))


        // for Password
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(12.dp),
            // Lukaune kaam garxa
            visualTransformation =
                if (!passwordVisibility) PasswordVisualTransformation()
                else VisualTransformation.None,

            // Keyboard Option EMail, Text, Password
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

            // For visibilty and non visibility
            suffix = {
                Icon(
                    painter = painterResource(
                        if(!passwordVisibility)
                            R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24
                    ),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        passwordVisibility = !passwordVisibility
                    }
                )
            },
            placeholder = {
                Text(
                    text = "******",
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            value = password,
            onValueChange = { input -> password = input }
        )

        // Check box and Forget Password Linkup
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically)
        {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ){
                Checkbox(checked = rememberMe,
                    onCheckedChange = {remember->
                        rememberMe = remember
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Blue, // this is for chnage color after checked in

                        checkmarkColor = Color.White // This is for tick one
                    )
                )

                Text(
                    text = "Remember Me"
                )

            }
            // 🔗 Forgot Password Text
            Text(
                text = "Forget Password",
                modifier = Modifier.padding(end = 5.dp)
                    .clickable {  },

                color = Color.Blue,
            )
        }


        Spacer(modifier = Modifier.height(30.dp))

        // 🔘 Login Button
        Button(
            onClick = {
                userViewModel.login(email, password) { success, message ->
                    if (success) {
                        val userId = userViewModel.getCurrentUser()?.uid

                        if (userId != null) {
                            userViewModel.getUserFromDatabase(userId) { dbSuccess, dbMessage, userModel ->
                                if (dbSuccess && userModel != null) {
                                    // Save email, password, and firstName in SharedPreferences
                                    editor.putString("email", email)
                                    editor.putString("password", password)
                                    editor.putString("firstName", userModel.firstName) // ✅ save first name
                                    editor.apply()

                                    // Navigate to Dashboard
                                    val intent = Intent(context, DashboardActivityResto::class.java)
                                    context.startActivity(intent)
                                    activity?.finish()
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


            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF)) // Blue
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(20.dp))


        // If already have aacount Sign up link
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){


            Text("Don't have an account? ")
            // 🆕 Sign Up Prompt
            // Then inside the clickable Sign Up text:
            Text(
                text = "Sign Up",
                modifier = Modifier.clickable {
                    // Connected with registrationActivity
                    val intent = Intent(context, RegistrationActivityResto::class.java)
                    context.startActivity(intent)
                },

                color = Color.Blue
            )
        }
    }
}

@Preview
@Composable
fun RestoLoginpreBody(){
    RestoLoginBody(innerPadding = PaddingValues(0.dp))
}