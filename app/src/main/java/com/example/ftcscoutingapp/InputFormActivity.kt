package com.example.ftcscoutingapp

import IntoTheDeepResults
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlin.math.max
import kotlin.math.min


private const val TAG = "InputFormActivityTAG"
class InputFormActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private lateinit var editTextTeamName: EditText
    private lateinit var buttonSendButton: Button
    private lateinit var viewResultsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inputformlayout)

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


        editTextTeamName = findViewById(R.id.teamName)
        buttonSendButton = findViewById(R.id.sendButton)
        viewResultsButton = findViewById(R.id.switchToResults)

        buttonSendButton.setOnClickListener(
            object: View.OnClickListener {
                override fun onClick(v: View?) {
                    val teamName: String = editTextTeamName.text.toString()

                    val opModeInputForm: OpmodeInputForm = viewPagerAdapter.opmodeFragment
                    val autonomousInputForm: AutonomousInputForm = viewPagerAdapter.autonomousFragment

                    DatabaseHandler.writeMatchResultToDataBase(
                        IntoTheDeepResults(
                            autonomousInputForm.getInputModuleValue("Net Samples"),
                            autonomousInputForm.getInputModuleValue("Low Basket"),
                            autonomousInputForm.getInputModuleValue("High Basket"),
                            autonomousInputForm.getInputModuleValue("Low Specimens"),
                            autonomousInputForm.getInputModuleValue("High Specimens"),
                            opModeInputForm.getInputModuleValue("Net Samples"),
                            opModeInputForm.getInputModuleValue("Low Basket"),
                            opModeInputForm.getInputModuleValue("High Basket"),
                            opModeInputForm.getInputModuleValue("Low Specimens"),
                            opModeInputForm.getInputModuleValue("High Specimens"),
                            opModeInputForm.getAscentLevel()
                        ),
                        teamName)

                    opModeInputForm.reset()
                    autonomousInputForm.reset()

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
            field = min(max(value, minValue), maxValue)
            numberDisplay.text = "$value"
        }
    init {
        numberDisplay.text = "$numberValue"

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
