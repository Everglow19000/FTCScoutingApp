package com.example.ftcscoutingapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val TAG: String = "ViewResultsActivityTAG"
class ViewResultsActivity : AppCompatActivity() {
    private lateinit var yearSelector: Spinner
    private lateinit var downloadDataButton: Button

    private lateinit var selectStartDateButton: Button
    private lateinit var selectEndDateButton: Button

    private lateinit var startDateDisplay: TextView
    private lateinit var endDateDisplay: TextView



    private var startDate: Date? = null
    private var endDate: Date? = null

    private lateinit var selectedYearRange: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewresultslayout)

        DatabaseHandler.getAvailableYearRanges { yearRanges ->
            selectedYearRange = yearRanges[0]

            yearSelector = findViewById(R.id.viewResultsYearSelector)
            downloadDataButton = findViewById(R.id.viewResultsDownloadDataButton)

            selectStartDateButton = findViewById(R.id.viewResultsSelectStartDateButton)
            selectEndDateButton = findViewById(R.id.viewResultsSelectEndDateButton)

            startDateDisplay = findViewById(R.id.viewResultsStartDateDisplay)
            endDateDisplay = findViewById(R.id.viewResultsEndDateDisplay)

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
                        else {
                            DatabaseHandler.fetchAllResultsFromSeason(selectedYearRange, startDate, endDate)
                        }
                    }
                }
            )

            selectStartDateButton.setOnClickListener(
                object: View.OnClickListener {
                    override fun onClick(v: View?) {
                        showStartDatePicker()
                    }
                }
            )

            selectEndDateButton.setOnClickListener(
                object: View.OnClickListener {
                    override fun onClick(v: View?) {
                        showEndDatePicker()
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

    fun showStartDatePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Start Date")
            .build()

        // Show the picker
        dateRangePicker.show(supportFragmentManager, "start_date_picker")

        // Handle the result
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            this.startDate = Date(selection)
            val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            this.startDateDisplay.text = dateFormat.format(startDate)
        }

        dateRangePicker.addOnNegativeButtonClickListener {
            // User cancelled
            println("Start date selection cancelled")
        }
    }

    fun showEndDatePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select End Date")
            .build()

        // Show the picker
        dateRangePicker.show(supportFragmentManager, "end_date_picker")

        // Handle the result
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            this.endDate = Date(selection)
            val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            this.endDateDisplay.text = dateFormat.format(endDate)
        }

        dateRangePicker.addOnNegativeButtonClickListener {
            // User cancelled
            println("End date selection cancelled")
        }
    }
}