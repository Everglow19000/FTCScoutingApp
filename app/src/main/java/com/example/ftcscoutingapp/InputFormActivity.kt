package com.example.ftcscoutingapp

import DecodeResults
import FTCApiHandler
import MatchResults
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min
import kotlinx.serialization.json.Json


private const val TAG = "InputFormActivityTAG"
class InputFormActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private lateinit var editTextTeamName: AutoCompleteTextView
    private lateinit var buttonSendButton: Button
    private lateinit var viewResultsButton: Button

    private lateinit var countrySelector: Spinner
    private lateinit var eventSelector: Spinner

    private lateinit var countriesList: MutableList<String>
    private lateinit var eventsList: MutableList<FTCApiHandler.EventCodeAndNamePair>
    private lateinit var teamNamesList: MutableList<String>

    private lateinit var selectedCountry: String
    private lateinit var selectedEventCode: String

    private lateinit var countriesArrayAdapter: ArrayAdapter<String>
    private lateinit var eventsArrayAdapter: ArrayAdapter<FTCApiHandler.EventCodeAndNamePair>
    private lateinit var teamNamesArrayAdapter: ArrayAdapter<String>

    private var FTCApiHandlerInstance: FTCApiHandler = Utils.ftcApiHandlerInstance

    public lateinit var matchResult: DecodeResults

    private lateinit var totalScoreDisplay: TextView

    suspend fun updateEvents() {
        eventsList.clear()
        eventsList.addAll(FTCApiHandlerInstance!!.countriesToCodesAndNames[selectedCountry]!!.toMutableList())
        eventsArrayAdapter.notifyDataSetChanged()
        selectedEventCode = eventsList[0].eventCode

        updateTeamNames()
    }

    suspend fun updateTeamNames() {
        val temp = FTCApiHandlerInstance!!.getTeamsInEvent(Utils.getCurrentYear(), selectedEventCode).sorted()

        teamNamesArrayAdapter.clear()
        teamNamesArrayAdapter.addAll(temp)
        teamNamesArrayAdapter.notifyDataSetChanged()

        teamNamesList.clear()
        teamNamesList.addAll(temp)

        editTextTeamName.setText("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inputformlayout)

        totalScoreDisplay = findViewById(R.id.inputFormTotalScoreDisplay)

        matchResult = DecodeResults(totalScoreDisplay)

        countrySelector = findViewById(R.id.inputFormCountrySelector)
        eventSelector = findViewById(R.id.inputFormEventSelector)


        lifecycleScope.launch {
            countriesList = FTCApiHandlerInstance!!.countriesToCodesAndNames.keys.toMutableList().sorted().toMutableList()
            selectedCountry = countriesList[0]

            countriesArrayAdapter = ArrayAdapter(
                this@InputFormActivity,
                android.R.layout.simple_list_item_1,
                countriesList
            )
            countriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            countrySelector.adapter = countriesArrayAdapter


            eventsList = FTCApiHandlerInstance!!.countriesToCodesAndNames[selectedCountry]!!.toMutableList()
            selectedEventCode = eventsList[0].eventCode

            eventsArrayAdapter = ArrayAdapter(
                this@InputFormActivity,
                android.R.layout.simple_list_item_1,
                eventsList
            )
            eventsArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            eventSelector.adapter = eventsArrayAdapter


            teamNamesList = FTCApiHandlerInstance!!.getTeamsInEvent(Utils.getCurrentYear(), selectedEventCode).sorted().toMutableList()
            teamNamesArrayAdapter = ArrayAdapter(
                this@InputFormActivity,
                android.R.layout.simple_list_item_1,
                teamNamesList
            )
            teamNamesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            editTextTeamName.setAdapter(teamNamesArrayAdapter)


            countrySelector.setOnItemSelectedListener(
                object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        selectedCountry = parent.getItemAtPosition(position).toString()

                        lifecycleScope.launch {
                            updateEvents()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        selectedCountry = countriesList[0]

                        lifecycleScope.launch {
                            updateEvents()
                        }
                    }
                }
            )
            eventSelector.setOnItemSelectedListener(
                object: AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        selectedEventCode = (parent.getItemAtPosition(position) as FTCApiHandler.EventCodeAndNamePair).eventCode

                        lifecycleScope.launch {
                            updateTeamNames()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        selectedEventCode = eventsList[0].eventCode

                        lifecycleScope.launch {
                            updateTeamNames()
                        }
                    }
                }
            )
        }


        tabLayout = findViewById(R.id.inputFormTabLayout)
        viewPager2 = findViewById(R.id.inputFormViewPager)
        viewPagerAdapter = ViewPagerAdapter(this)

        viewPager2.adapter = viewPagerAdapter

        tabLayout.addOnTabSelectedListener(
            object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(p0: TabLayout.Tab?) {
                    if (p0?.position == null) {
                        throw NullPointerException()
                    }
                    viewPager2.setCurrentItem(p0.position)

                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabReselected(p0: TabLayout.Tab?) {

                }
            }
        )

        viewPager2.registerOnPageChangeCallback(
            object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.getTabAt(position)?.select()
                }
            }
        )

        viewPagerAdapter.opmodeFragment.matchResult = matchResult
        viewPagerAdapter.autonomousFragment.matchResult = matchResult


        editTextTeamName = findViewById(R.id.inputFormTeamNameInput)
        buttonSendButton = findViewById(R.id.inputFormSendButton)
        viewResultsButton = findViewById(R.id.switchToResults)

        buttonSendButton.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    val teamName: String = editTextTeamName.text.toString()

                    if (teamName in teamNamesList) {
                        val opModeInputForm: OpmodeInputForm = viewPagerAdapter.opmodeFragment
                        val autonomousInputForm: AutonomousInputForm = viewPagerAdapter.autonomousFragment

                        DatabaseHandler.writeMatchResultToDataBase(
                            matchResult,
                            teamName)

                        opModeInputForm.reset()
                        autonomousInputForm.reset()

                        matchResult.clearScores()

                        editTextTeamName.text.clear()
                    }
                    else {
                        showAlertDialogWithoutButtons(this@InputFormActivity, "Enter valid team name!", "you must enter a valid team name!")
                    }
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

    fun showAlertDialogWithoutButtons(context: Context, title: String, message: String) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setCancelable(true)

        var dialog: AlertDialog = builder.create()
        dialog.show()
    }
}

class SingleInputModule(var methodName: String, subtractionButton: Button, additionButton: Button, var numberDisplay: TextView, var scoreDisplay: TextView, var scoreCalculator: (Long) -> Long, var isAutonomous: Boolean, var scoreStringProcessor: (Long) -> String, var matchResultToUpdate: MatchResults, val minValue: Long = 0, val maxValue: Long = 20, initialValue: Long = 0) {
    var numberValue: Long = 0
        set(value) {
            field = min(max(value, minValue), maxValue).toLong()
            numberDisplay.text = "$value"
            if (isAutonomous) {
                matchResultToUpdate.autonomousScoringMethods.setValueOfMethod(methodName, numberValue.toString())
            }
            else {
                matchResultToUpdate.teleopScoringMethods.setValueOfMethod(methodName, numberValue.toString())
            }
            scoreDisplay.text = scoreStringProcessor(scoreCalculator(numberValue))
        }
    init {
        numberValue = initialValue
        numberDisplay.text = "${min(max(initialValue, minValue), maxValue)}"
        scoreDisplay.text = scoreStringProcessor(0)

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
