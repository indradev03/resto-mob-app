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
import androidx.compose.ui.text.font.FontWeight
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
    val context = LocalContext.current
    val repository = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repository) }


    //for first name and lastname
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var showFirstNameError by remember { mutableStateOf(false) }
    var showLastNameError by remember { mutableStateOf(false) }

    //for email
    var email by remember { mutableStateOf("") }
    var showEmailError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }


    //For password
    var password by remember { mutableStateOf("") }
    var showPasswordError by remember { mutableStateOf(false) }
    var passwordErrorMessage by remember { mutableStateOf("") }

    //this is for Country
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    val options = listOf("Nepal", "USA", "New Zealand" , "Australia" ,"Germany")
    var showOptionError by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    // This is for Gender
    var selectedGender by remember { mutableStateOf("") }
    var showGenderError by remember { mutableStateOf(false) }

    // This is for terms and condition
    var rememberMe by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    //for date
    var selectedDate by remember { mutableStateOf("") }
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    var showDateError by remember { mutableStateOf(false) }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
            showDateError = false // clear error on selection
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
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = {
                            firstName = it
                            showFirstNameError = false // clear error on typing
                        },
                        label = { Text("First Name", color = Color(0xFF444444)) },
                        isError = showFirstNameError,
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0A84FF),
                            unfocusedBorderColor = if (showFirstNameError) Color.Red else Color.Gray,
                            errorBorderColor = Color.Red,
                            focusedLabelColor = Color(0xFF0A84FF),
                            cursorColor = Color(0xFF0A84FF),
                            focusedTextColor = Color(0xFF222222),
                            unfocusedTextColor = Color(0xFF222222),
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showFirstNameError) {
                        Text(
                            text = "First name is required",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            showLastNameError = false
                        },
                        label = { Text("Last Name", color = Color(0xFF444444)) },
                        isError = showLastNameError,
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF0A84FF),
                            unfocusedBorderColor = if (showLastNameError) Color.Red else Color.Gray,
                            errorBorderColor = Color.Red,
                            focusedLabelColor = Color(0xFF0A84FF),
                            cursorColor = Color(0xFF0A84FF),
                            focusedTextColor = Color(0xFF222222),
                            unfocusedTextColor = Color(0xFF222222),
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (showLastNameError) {
                        Text(
                            text = "Last name is required",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
        }

        fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        item {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    leadingIcon = {   // Use leadingIcon, not prefix
                        Icon(
                            painter = painterResource(R.drawable.baseline_email_24),
                            contentDescription = null,
                            tint = Color(0xFF0A84FF)
                        )
                    },
                    placeholder = { Text("Email", color = Color.Gray) },
                    value = email,
                    onValueChange = {
                        email = it
                        showEmailError = false  // clear error while typing
                    },
                    isError = showEmailError,
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0A84FF),
                        unfocusedBorderColor = if (showEmailError) Color.Red else Color.Gray.copy(alpha = 0.3f),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color(0xFF0A84FF),
                        cursorColor = Color(0xFF0A84FF),
                        focusedTextColor = Color(0xFF222222),
                        unfocusedTextColor = Color(0xFF222222),
                    )
                )
                if (showEmailError) {
                    Text(
                        text = emailErrorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }


        item {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_password_24),
                            contentDescription = null,
                            tint = Color(0xFF0A84FF)
                        )
                    },
                    placeholder = { Text("Password", color = Color.Gray) },
                    value = password,
                    onValueChange = {
                        password = it
                        showPasswordError = false // Clear error on typing
                    },
                    isError = showPasswordError,
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFF0A84FF),
                        unfocusedBorderColor = if (showPasswordError) Color.Red else Color.Gray.copy(alpha = 0.3f),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = Color(0xFF0A84FF),
                        cursorColor = Color(0xFF0A84FF),
                        focusedTextColor = Color(0xFF222222),
                        unfocusedTextColor = Color(0xFF222222),
                    )
                )
                if (showPasswordError) {
                    Text(
                        text = passwordErrorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }


        item {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                ) {
                    OutlinedTextField(
                        value = selectedOptionText,
                        onValueChange = {},
                        enabled = false,
                        placeholder = { Text("Select your Country", color = Color.Gray) },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFF0A84FF)
                            )
                        },
                        isError = showOptionError,
                        colors = outlinedTextFieldColors(
                            disabledTextColor = Color.Black,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                            disabledBorderColor = if (showOptionError) Color.Red else Color.Gray.copy(alpha = 0.3f)
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
                                    showOptionError = false  // Clear error on selection
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (showOptionError) {
                    Text(
                        text = "Please select a country",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }


        item {
            Column {
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
                        placeholder = { Text("DOB", color = Color.Gray) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedTextFieldColors(
                            disabledTextColor = Color.Black,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                            disabledBorderColor = if (showDateError) Color.Red else Color.Gray.copy(alpha = 0.3f)
                        )
                    )
                }

                if (showDateError) {
                    Text(
                        text = "Please select your Date of Birth",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }




        item {
            Text(
                text = "Gender",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                listOf("Male", "Female", "Other").forEach { gender ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedGender = gender
                            showGenderError = false // clear error on selection
                        }
                    ) {
                        RadioButton(
                            selected = (selectedGender == gender),
                            onClick = {
                                selectedGender = if (selectedGender == gender) "" else gender
                                showGenderError = false
                                showGenderError = false // clear error on selection
                            },
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

            if (showGenderError) {
                Text(
                    text = "Please select a gender",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
        }


        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = {
                            rememberMe = it
                            if (it) showError = false
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF0A84FF),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "I accept terms and condition", color = Color(0xFF222222))
                }

                if (showError && !rememberMe) {
                    Text(
                        text = "You must accept the terms and conditions",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }


        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    var hasError = false

                    if (firstName.trim().isEmpty()) {
                        showFirstNameError = true
                        hasError = true
                    } else {
                        showFirstNameError = false
                    }

                    if (lastName.trim().isEmpty()) {
                        showLastNameError = true
                        hasError = true
                    } else {
                        showLastNameError = false
                    }


                    if (email.trim().isEmpty()) {
                        showEmailError = true
                        emailErrorMessage = "Email is required"
                    } else if (!isValidEmail(email.trim())) {
                        showEmailError = true
                        emailErrorMessage = "Please enter a valid email address"
                    } else {
                        showEmailError = false
                        emailErrorMessage = ""
                    }

                    if (password.isEmpty()) {
                        showPasswordError = true
                        passwordErrorMessage = "Password is required"
                    } else if (password.length < 8) {
                        showPasswordError = true
                        passwordErrorMessage = "Password must be at least 8 characters"
                    } else {
                        showPasswordError = false
                        passwordErrorMessage = ""
                    }

                    if (selectedOptionText.trim().isEmpty()) {
                        showOptionError = true
                        Toast.makeText(context, "Please select a country", Toast.LENGTH_LONG).show()
                        return@Button
                    }


                    if (selectedDate.isEmpty()) {
                        showDateError = true
                        Toast.makeText(context, "Please select your Date of Birth", Toast.LENGTH_LONG).show()
                        return@Button
                    }


                    // GENDER
                    if (selectedGender.isEmpty()) {
                        showGenderError = true
                        hasError = true
                    } else {
                        showGenderError = false
                    }

                    // Checkbox validation
                    if (!rememberMe) {
                        showError = true
                        Toast.makeText(context, "Please accept the terms and conditions", Toast.LENGTH_LONG).show()
                        return@Button
                    }


                    if (hasError) {
                        Toast.makeText(context, "All fields are empty", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Proceed with registration if all fields are valid
                    showError = false // clear error

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

