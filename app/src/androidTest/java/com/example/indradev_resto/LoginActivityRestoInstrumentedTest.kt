package com.example.indradev_resto

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.indradev_resto.view.LoginActivityResto
import com.example.indradev_resto.viewmodel.UserViewModel
import com.example.indradev_resto.repository.UserRepositoryImpl
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityRestoInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivityResto>()

    @Test
    fun testUserLoginWithRegisteredEmail() {
        // Input email & password you registered in registration test
        composeTestRule.onNodeWithTag("loginEmailField")
            .performTextInput("testuser@gmail.com")

        composeTestRule.onNodeWithTag("loginPasswordField")
            .performTextInput("123456")

        // Click login button
        composeTestRule.onNodeWithTag("loginButton")
            .performClick()
    }
}
