package com.example.ftcscoutingapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ViewResultsActivity : AppCompatActivity() {
    private lateinit var yearSelector: AutoCompleteTextView
    private lateinit var downloadDataButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewresultslayout)

        var yearRanges: Array<String> = DatabaseHandler.getAvailableYearRanges()

        yearSelector = findViewById(R.id.viewResultsYearSelector)
        downloadDataButton = findViewById(R.id.viewResultsDownloadDataButton)

        val arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, yearRanges
        )
        yearSelector.setAdapter(arrayAdapter)

        yearSelector.setText(yearRanges[0])

        downloadDataButton.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    DatabaseHandler.fetchAllResultsFromSeason(yearSelector.text.toString())
                }
            }
        )
    }
}