package com.example.indradev_resto

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.indradev_resto.view.RegistrationActivityResto
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationActivityRestoInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<RegistrationActivityResto>()


    @Test
    fun testValidInput_registersSuccessfully() {
        // First & Last Name
        composeTestRule.onNodeWithTag("firstNameField").performScrollTo().performTextInput("Saroj")
        composeTestRule.onNodeWithTag("lastNameField").performScrollTo().performTextInput("Ayer")

        // Email & Password
        composeTestRule.onNodeWithTag("emailField").performScrollTo().performTextInput("testuser@gmail.com")
        composeTestRule.onNodeWithTag("passwordField").performScrollTo().performTextInput("123456")

        // Country selection
        composeTestRule.onNodeWithTag("countryDropdown").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("countryOption_Nepal").performClick()

        // Gender selection
        composeTestRule.onNodeWithTag("genderOption_Male").performScrollTo().performClick()

        // Accept terms
        composeTestRule.onNodeWithTag("termsCheckbox").performScrollTo().performClick()

        // Note: DOB picker uses DatePickerDialog (non-Compose), can't be tested directly here

        // Submit
        composeTestRule.onNodeWithTag("registerButton").performScrollTo().performClick()

        // You can add expectations for mock callbacks or navigation if needed
    }
}
