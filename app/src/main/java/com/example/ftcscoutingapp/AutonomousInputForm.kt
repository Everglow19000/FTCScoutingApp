package com.example.ftcscoutingapp

import IntoTheDeepResults
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class AutonomousInputForm : Fragment() {
    public lateinit var matchResult: IntoTheDeepResults
    private lateinit var inputModulesMap: MutableMap<String, SingleInputModule>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var inflated = inflater.inflate(R.layout.fragment_autonomous_input_form, container, false)

        var netSamplesFunction = { count: Long -> 2*count }
        var lowBasketFunction = { count: Long -> 4*count }
        var highBasketFunction = { count: Long -> 8*count }
        var lowSpecimensFunction = { count: Long -> 5*count }
        var highSpecimensFunction = { count: Long -> 10*count }

        inputModulesMap = mutableMapOf(
            "Net Samples" to SingleInputModule(
                "Net Samples",
                inflated.findViewById<Button>(R.id.inputFormAutonomousNetSamplesSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormAutonomousNetSamplesAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousNetSamplesDisplay),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousNetSamplesScoreDisplay),
                netSamplesFunction,
                true,
                matchResult
            ),
            "Low Basket Samples" to SingleInputModule(
                "Low Basket Samples",
                inflated.findViewById<Button>(R.id.inputFormAutonomousLowBasketSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormAutonomousLowBasketAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousLowBasketDisplay),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousLowBasketScoreDisplay),
                lowBasketFunction,
                true,
                matchResult
            ),
            "High Basket Samples" to SingleInputModule(
                "High Basket Samples",
                inflated.findViewById<Button>(R.id.inputFormAutonomousHighBasketSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormAutonomousHighBasketAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousHighBasketDisplay),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousHighBasketScoreDisplay),
                highBasketFunction,
                true,
                matchResult
            ),
            "Low Specimens" to SingleInputModule(
                "Low Specimens",
                inflated.findViewById<Button>(R.id.inputFormAutonomousLowSpecimensSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormAutonomousLowSpecimensAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousLowSpecimensDisplay),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousLowSpecimensScoreDisplay),
                lowSpecimensFunction,
                true,
                matchResult
            ),
            "High Specimens" to SingleInputModule(
                "High Specimens",
                inflated.findViewById<Button>(R.id.inputFormAutonomousHighSpecimensSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormAutonomousHighSpecimensAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousHighSpecimensDisplay),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousHighSpecimensScoreDisplay),
                highSpecimensFunction,
                true,
                matchResult
            )
        )

        return inflated
    }

    fun getInputModuleValue(name: String): Long {
        return inputModulesMap[name]!!.numberValue.toLong()
    }

    fun reset() {
        for (item in inputModulesMap.values) {
            item.numberValue = 0
        }
    }
}