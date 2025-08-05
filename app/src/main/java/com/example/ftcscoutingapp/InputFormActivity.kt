package com.example.ftcscoutingapp

import IntoTheDeepResults
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlin.math.max
import kotlin.math.min


private const val TAG = "InputFormActivityTAG"
class InputFormActivity : ComponentActivity() {

    private lateinit var buttonAdditionButton: Button
    private lateinit var buttonSubtractionButton: Button
    private lateinit var textViewNumberDisplay: TextView
    private lateinit var editTextTeamName: EditText
    private lateinit var buttonSendButton: Button
    private lateinit var viewResultsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inputformlayout)

        buttonAdditionButton = findViewById(R.id.additionButton)
        buttonSubtractionButton = findViewById(R.id.subtractionButton)
        textViewNumberDisplay = findViewById(R.id.numberDisplay)
        editTextTeamName = findViewById(R.id.teamName)
        buttonSendButton = findViewById(R.id.sendButton)
        viewResultsButton = findViewById(R.id.switchToResults)


        buttonSendButton.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    val teamName: String = editTextTeamName.text.toString()
//                    DatabaseHandler.writeMatchResultToDataBase(IntoTheDeepResults(), teamName)



                    editTextTeamName.text.clear()
                }
            }
        )


        viewResultsButton.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    val intent = Intent(this@InputFormActivity, ViewResultsActivity::class.java)
                    startActivity(intent)
                }
            }
        )
    }
}

class SingleInputModule(subtractionButton: Button, additionButton: Button, var numberDisplay: TextView, val minValue: Int = 0, val maxValue: Int = 20, initialValue: Int = 0) {
    var numberValue: Int = initialValue
        set(value) {
            numberValue = value
            numberValue = max(minValue, numberValue)
            numberValue = min(maxValue, numberValue)
            numberDisplay.text = "$numberValue"
        }
    init {
        additionButton.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    numberValue++
                    numberValue = min(numberValue, maxValue)
                    numberDisplay.text = "$numberValue"
                }
            }
        )

        subtractionButton.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    numberValue--
                    numberValue = max(numberValue, minValue)
                    numberDisplay.text = "$numberValue"
                }
            }
        )
    }
}
