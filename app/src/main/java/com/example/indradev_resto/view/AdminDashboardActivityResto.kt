package com.example.indradev_resto.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.indradev_resto.view.pages.AdminDashboardScreen
import com.example.indradev_resto.view.ui.theme.Indradev_RESTOTheme

class AdminDashboardActivityResto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

                AdminDashboardScreen(onLogout = {
                val sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                val intent = Intent(this, LoginActivityResto::class.java)
                startActivity(intent)
                finish()
            })}
        }

    }