package com.example.ftcscoutingapp

import FTCApiHandler
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.ftcscoutingapp.ui.theme.FTCScoutingAppTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var mainToResultsButton: Button
    private lateinit var mainToInputForm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        Utils.ftcApiHandlerInstance = runBlocking {
            Utils.getFTCApiHandler(this@MainActivity)!!
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainlayout)


        mainToResultsButton = findViewById(R.id.mainToResultsButton)
        mainToInputForm = findViewById(R.id.mainToInputButton)

        mainToResultsButton.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    var moveToResults: Intent = Intent(this@MainActivity, ViewResultsActivity::class.java)
                    startActivity(moveToResults)
                }
            }
        )

        mainToInputForm.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    var moveToResults: Intent = Intent(this@MainActivity, InputFormActivity::class.java)
                    startActivity(moveToResults)
                }
            }
        )
    }
}