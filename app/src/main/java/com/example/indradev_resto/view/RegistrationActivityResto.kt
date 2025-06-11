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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.indradev_resto.viewmodel.UserViewModel

class RegistrationActivityResto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding ->
                RestoRegistrationBody(innerPadding)
            }
        }
    }
}

@Composable
fun RestoRegistrationBody(innerPadding: PaddingValues) {

    //this is better
    val repository = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repository) }



    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }


    //Email
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    //DropDown Menu
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("Select Country") }
    val options = listOf("Nepal", "USA", "New Zealand")
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    // LocalContext le arko file sanga add garauxa
    //And This if for Date Picker
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    var selectedDate by remember { mutableStateOf("DOB") }

    //Radio Button Or Gender
    var selectedGender by remember { mutableStateOf("Male") }
    var rememberMe by remember { mutableStateOf(false) }

    //DatePicker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year, month, day
    )

    val activity = context as? Activity

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 10.dp)
                .padding(8.dp)
        ) {
            Button(
                onClick = {
                    activity?.finish()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0A84FF)// Blue
                )
            ) {
                // Then inside the clickable Sign In text:
                Text(
                    text = "Back",
                    modifier = Modifier.clickable {
                        // Connected with LoginActivity
                        val intent = Intent(context, LoginActivityResto::class.java)
                        context.startActivity(intent)
                    },
                    color = Color.White
                )

            }
        }
        // For Main Heading
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Register", fontSize = 25.sp)
        }


        Spacer(modifier = Modifier.height(30.dp))
        // For Firstname and last name textfiled
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
        }
        // For Email textfiled
        Row {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                prefix = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_email_24),
                        contentDescription = null
                    )
                },
                placeholder = { Text("Email") },
                value = email,
                onValueChange = { input -> email = input }
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        Row {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                prefix = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_password_24),
                        contentDescription = null
                    )
                },
                placeholder = { Text("Password") },
                value = password,
                onValueChange = { input -> password = input }
            )
        }
        //Dropdown menu or Select Country
        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                OutlinedTextField(
                    value = selectedOptionText,
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        }
                        .clickable { expanded = true },
                    placeholder = { Text("") },
                    enabled = false,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = Color.Black
                    ),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                )
                // Drop Down Menu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(with(LocalDensity.current) {
                        textFieldSize.width.toDp()
                    })
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedOptionText = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        //For Date of birth or datepicker
        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        datePickerDialog.show()
                    }
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = selectedDate,
                    onValueChange = {},
                    enabled = false,
                    placeholder = { Text("DOB") },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        // For radio button or Genders
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gender label at the top
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Gender",
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Row for the radio buttons, placed below the "Gender" label
                Row(
                    horizontalArrangement = Arrangement.spacedBy(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Male", "Female", "Other").forEach { gender ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedGender = gender }
                        ) {
                            RadioButton(
                                selected = (selectedGender == gender),
                                onClick = { selectedGender = gender }
                            )
                            Text(
                                text = gender,
                                modifier = Modifier.padding(start = 15.dp)
                            )
                        }
                    }
                }
            }
        }

        //For check box and term and condtion
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
                        checkedColor = Color.Green, // this is for chnage color after checked in

                        checkmarkColor = Color.White // This is for tick one
                    )
                )

                Text(
                    text = "I accept terms and condition"
                )

            }
        }

        // This is spacer between check box and Register button
        Spacer(modifier = Modifier.height(20.dp))

        // 🔘 Register Button
        Button(
            onClick = {
                // Handle login click
                userViewModel.register(email, password){
                        success,message,userId ->
                    if (success){
                        var model = UserModel(
                            userId, email, password, firstName, lastName,
                            selectedOptionText,selectedDate,selectedGender
                        )
                        userViewModel.addUserToDatabase(userId,model){
                                success,message ->
                            if (success){
                                activity?.finish() // Close registration activity
                                Toast.makeText(context,message, Toast.LENGTH_LONG).show()



                            }else{
                                Toast.makeText(context,message, Toast.LENGTH_LONG).show()

                            }
                        }
                    }else{
                        Toast.makeText(context,message, Toast.LENGTH_LONG).show()

                    }

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF)) // Blue
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(20.dp))


        // If already have aacount Sign In link
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){

            Text("Already have an account? ")
            // 🆕 Sign Up Prompt
            // Then inside the clickable Sign In text:
            Text(
                text = "Sign In",
                modifier = Modifier.clickable {
                    // Connected with LoginActivity
                    val intent = Intent(context, LoginActivityResto::class.java)
                    context.startActivity(intent)
                },
                color = Color.Blue
            )
        }

    }
}

@Preview
@Composable
fun RestoRegistrationPreBody() {
    RestoRegistrationBody(innerPadding = PaddingValues(0.dp))
}
