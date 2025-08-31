package com.example.ftcscoutingapp

import IntoTheDeepResults
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView

class OpmodeInputForm : Fragment() {
    private lateinit var inputModulesMap: MutableMap<String, SingleInputModule>
    private lateinit var ascentLevelMap: MutableMap<Int, IntoTheDeepResults.AscentLevel>
    private lateinit var radioGroup: RadioGroup
    public lateinit var matchResult: IntoTheDeepResults
    private var checkedAscentLevel: IntoTheDeepResults.AscentLevel = IntoTheDeepResults.AscentLevel.NONE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var inflated = inflater.inflate(R.layout.fragment_opmode_input_form, container, false)

        ascentLevelMap = mutableMapOf(
            R.id.inputFormOpModeAscentLevelButtonNone to IntoTheDeepResults.AscentLevel.NONE,
            R.id.inputFormOpModeAscentLevelButtonParking to IntoTheDeepResults.AscentLevel.PARKING,
            R.id.inputFormOpModeAscentLevelButtonFirstLevel to IntoTheDeepResults.AscentLevel.FIRST_LEVEL,
            R.id.inputFormOpModeAscentLevelButtonSecondLevel to IntoTheDeepResults.AscentLevel.SECOND_LEVEL,
            R.id.inputFormOpModeAscentLevelButtonThirdevel to IntoTheDeepResults.AscentLevel.THIRD_LEVEL
        )

        radioGroup = inflated.findViewById(R.id.inputFormOpModeAscentLevelGroup)
        radioGroup.setOnCheckedChangeListener(
            object: RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(
                    group: RadioGroup,
                    checkedId: Int
                ) {
                    checkedAscentLevel = ascentLevelMap.getOrDefault(checkedId, IntoTheDeepResults.AscentLevel.NONE)
                    matchResult.opMode.setScoreOfMethod("Ascent Level", checkedAscentLevel.value)
                }
            }
        )

        var netSamplesFunction = { count: Long -> 2*count }
        var lowBasketFunction = { count: Long -> 4*count }
        var highBasketFunction = { count: Long -> 8*count }
        var lowSpecimensFunction = { count: Long -> 5*count }
        var highSpecimensFunction = { count: Long -> 10*count }

        inputModulesMap = mutableMapOf(
            "Net Samples" to SingleInputModule(
                "Net Samples",
                inflated.findViewById<Button>(R.id.inputFormOpModeNetSamplesSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModeNetSamplesAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModeNetSamplesDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModeNetSamplesScoreDisplay),
                netSamplesFunction,
                false,
                matchResult
            ),
            "Low Basket Samples" to SingleInputModule(
                "Low Basket Samples",
                inflated.findViewById<Button>(R.id.inputFormOpModeLowBasketSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModeLowBasketAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModeLowBasketDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModeLowBasketScoreDisplay),
                lowBasketFunction,
                false,
                matchResult
            ),
            "High Basket Samples" to SingleInputModule(
                "High Basket Samples",
                inflated.findViewById<Button>(R.id.inputFormOpModeHighBasketSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModeHighBasketAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModeHighBasketDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModeHighBasketScoreDisplay),
                highBasketFunction,
                false,
                matchResult
            ),
            "Low Specimens" to SingleInputModule(
                "Low Specimens",
                inflated.findViewById<Button>(R.id.inputFormOpModeLowSpecimensSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModeLowSpecimensAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModeLowSpecimensDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModeLowSpecimensScoreDisplay),
                lowSpecimensFunction,
                false,
                matchResult
            ),
            "High Specimens" to SingleInputModule(
                "High Specimens",
                inflated.findViewById<Button>(R.id.inputFormOpModeHighSpecimensSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModeHighSpecimensAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModeHighSpecimensDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModeHighSpecimensScoreDisplay),
                highSpecimensFunction,
                false,
                matchResult
            )
        )

        return inflated

    }

    fun getInputModuleValue(name: String): Long {
        return inputModulesMap[name]!!.numberValue.toLong()
    }

    fun getAscentLevel(): IntoTheDeepResults.AscentLevel {
        return checkedAscentLevel
    }

    fun reset() {
        for (item in inputModulesMap.values) {
            item.numberValue = 0
        }
        radioGroup.clearCheck()
    }
}