package com.example.indradev_resto.view


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.indradev_resto.R
import kotlinx.coroutines.delay

class SplashActivityResto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding->
                RestoSplashBody(innerPadding)
            }
        }
    }
}

@Composable
fun RestoSplashBody(innerPadding: PaddingValues){

    val context = LocalContext.current
    val activity = context as? Activity

    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)

    val localEmail : String? = sharedPreferences.getString("email","")
    val localpassword : String? = sharedPreferences.getString("password","")

    LaunchedEffect(Unit) {
        delay(2000)

        if (localEmail.toString().isEmpty()){
            val intent = Intent(context, LoginActivityResto::class.java)
            context.startActivity(intent)
            activity?.finish()
        }else{
            val intent = Intent(context, DashboardActivityResto::class.java)
            context.startActivity(intent)
            activity?.finish()
        }
    }

    Column (modifier = Modifier.padding(innerPadding).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        //Top Image
        Image(
            painter = painterResource(R.drawable.restologo),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 20.dp)
                .height(250.dp)
                .width(250.dp)
        )

    }

}

@Preview
@Composable
fun RestoSplashpreBody() {
    RestoSplashBody(innerPadding = PaddingValues(0.dp))
}
