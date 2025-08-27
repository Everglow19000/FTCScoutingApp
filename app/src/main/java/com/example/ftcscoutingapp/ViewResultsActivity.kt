package com.example.ftcscoutingapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

private val TAG: String = "ViewResultsActivity"
class ViewResultsActivity : AppCompatActivity() {
    private lateinit var yearSelector: Spinner
    private lateinit var downloadDataButton: Button

    private lateinit var selectedYearRange: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewresultslayout)

        DatabaseHandler.getAvailableYearRanges { yearRanges ->
            selectedYearRange = yearRanges[0]

            yearSelector = findViewById(R.id.viewResultsYearSelector)
            downloadDataButton = findViewById(R.id.viewResultsDownloadDataButton)

            val arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, yearRanges
            )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            yearSelector.setAdapter(arrayAdapter)

            yearSelector.setOnItemSelectedListener(
                object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        selectedYearRange= parent.getItemAtPosition(position).toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        selectedYearRange = ""
                    }
                }
            )

            downloadDataButton.setOnClickListener(
                object: View.OnClickListener {
                    override fun onClick(v: View?) {
                        if (selectedYearRange.equals("")) {
                            showAlertDialogWithoutButtons(this@ViewResultsActivity, "No Year Range Selected!", "You must select a year range in order to download data!")
                        }
                        DatabaseHandler.fetchAllResultsFromSeason(selectedYearRange)
                    }
                }
            )
        }
    }

    fun showAlertDialogWithoutButtons(context: Context, title: String, message: String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setCancelable(true)

        var dialog: AlertDialog = builder.create()
        dialog.show()
    }
}