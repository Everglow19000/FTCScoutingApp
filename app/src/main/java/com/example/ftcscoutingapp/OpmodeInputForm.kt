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
                }
            }
        )

        inputModulesMap = mutableMapOf(
            "Net Samples" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormOpModeNetSamplesSubtractionButton), inflated.findViewById<Button>(R.id.inputFormOpModeNetSamplesAdditionButton), inflated.findViewById<TextView>(R.id.inputFormOpmodeNetSamplesDisplay)),
            "Low Basket" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormOpModeLowBasketSubtractionButton), inflated.findViewById<Button>(R.id.inputFormOpModeLowBasketAdditionButton), inflated.findViewById<TextView>(R.id.inputFormOpmodeLowBasketDisplay)),
            "High Basket" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormOpModeHighBasketSubtractionButton), inflated.findViewById<Button>(R.id.inputFormOpModeHighBasketAdditionButton), inflated.findViewById<TextView>(R.id.inputFormOpmodeHighBasketDisplay)),
            "Low Specimens" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormOpModeLowSpecimensSubtractionButton), inflated.findViewById<Button>(R.id.inputFormOpModeLowSpecimensAdditionButton), inflated.findViewById<TextView>(R.id.inputFormOpmodeLowSpecimensDisplay)),
            "High Specimens" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormOpModeHighSpecimensSubtractionButton), inflated.findViewById<Button>(R.id.inputFormOpModeHighSpecimensAdditionButton), inflated.findViewById<TextView>(R.id.inputFormOpmodeHighSpecimensDisplay))
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