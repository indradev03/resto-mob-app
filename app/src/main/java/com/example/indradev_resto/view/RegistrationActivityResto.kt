package com.example.indradev_resto.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.indradev_resto.R
import com.example.indradev_resto.model.UserModel
import com.example.indradev_resto.repository.UserRepositoryImpl
import com.example.indradev_resto.view.ui.theme.Indradev_RESTOTheme
import com.example.indradev_resto.viewmodel.UserViewModel

class RegistrationActivityResto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Indradev_RESTOTheme {
                Scaffold { innerPadding ->
                    RestoRegistrationBody(innerPadding)
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestoRegistrationBody(innerPadding: PaddingValues) {
    val repository = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repository) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Select Country") }
    val options = listOf("Nepal", "USA", "New Zealand")
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    var selectedDate by remember { mutableStateOf("DOB") }

    var selectedGender by remember { mutableStateOf("Male") }
    var rememberMe by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year, month, day
    )

    val activity = context as? Activity

    // Animate changes for the whole form content
    val transition = updateTransition(targetState = rememberMe, label = "rememberMeTransition")

    LazyColumn(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .imePadding() // makes space for keyboard
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        activity?.finish()
                        activity?.overridePendingTransition(
                            android.R.anim.fade_in,
                            android.R.anim.fade_out
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0A84FF)
                    )
                }
            }
        }


        item {
            Text(
                text = "Register",
                fontSize = 30.sp,
                color = Color(0xFF222222), // Darker text color for headings
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.headlineMedium
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name", color = Color(0xFF444444)) },
                    modifier = Modifier.weight(1f),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0A84FF),
                        focusedLabelColor = Color(0xFF0A84FF),
                        cursorColor = Color(0xFF0A84FF),
                        focusedTextColor = Color(0xFF222222),
                        unfocusedTextColor = Color(0xFF222222),
                    )
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name", color = Color(0xFF444444)) },
                    modifier = Modifier.weight(1f),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0A84FF),
                        focusedLabelColor = Color(0xFF0A84FF),
                        cursorColor = Color(0xFF0A84FF),
                        focusedTextColor = Color(0xFF222222),
                        unfocusedTextColor = Color(0xFF222222),
                    )
                )
            }
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                prefix = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_email_24),
                        contentDescription = null,
                        tint = Color(0xFF0A84FF)
                    )
                },
                placeholder = { Text("Email", color = Color.Gray) },
                value = email,
                onValueChange = { email = it },
                colors = outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF0A84FF),
                    focusedLabelColor = Color(0xFF0A84FF),
                    cursorColor = Color(0xFF0A84FF),
                    focusedTextColor = Color(0xFF222222),
                    unfocusedTextColor = Color(0xFF222222),
                )
            )
        }

        item {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                prefix = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_password_24),
                        contentDescription = null,
                        tint = Color(0xFF0A84FF)
                    )
                },
                placeholder = { Text("Password", color = Color.Gray) },
                value = password,
                onValueChange = { password = it },
                colors = outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF0A84FF),
                    focusedLabelColor = Color(0xFF0A84FF),
                    cursorColor = Color(0xFF0A84FF),
                    focusedTextColor = Color(0xFF222222),
                    unfocusedTextColor = Color(0xFF222222),
                )
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
            ) {
                OutlinedTextField(
                    value = selectedOptionText,
                    onValueChange = {},
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFF0A84FF)
                        )
                    },
                    colors = outlinedTextFieldColors(
                        disabledTextColor = Color.Black,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                        disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = Color.White) },
                            onClick = {
                                selectedOptionText = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        datePickerDialog.show()
                    }
            ) {
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("DOB", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(
                        disabledTextColor = Color.Black,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                        disabledBorderColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
            }
        }

        item {
            Text(text = "Gender", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color(0xFF222222))
            Row(
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                listOf("Male", "Female", "Other").forEach { gender ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedGender = gender }
                    ) {
                        RadioButton(
                            selected = (selectedGender == gender),
                            onClick = { selectedGender = gender },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF))
                        )
                        Text(
                            text = gender,
                            modifier = Modifier.padding(start = 8.dp),
                            color = Color(0xFF222222)
                        )
                    }
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF0A84FF),
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "I accept terms and condition", color = Color(0xFF222222))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    userViewModel.register(email, password) { success, message, userId ->
                        if (success) {
                            val model = UserModel(
                                userId, email, password, firstName, lastName,
                                selectedOptionText, selectedDate, selectedGender
                            )
                            userViewModel.addUserToDatabase(userId, model) { addSuccess, addMessage ->
                                if (addSuccess) {
                                    activity?.finish()
                                    Toast.makeText(context, addMessage, Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, addMessage, Toast.LENGTH_LONG).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF))
            ) {
                Text("Register", color = Color.White)
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Already have an account? ", color = Color(0xFF222222))
                Text(
                    text = "Sign In",
                    modifier = Modifier.clickable {
                        val intent = Intent(context, LoginActivityResto::class.java)
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

