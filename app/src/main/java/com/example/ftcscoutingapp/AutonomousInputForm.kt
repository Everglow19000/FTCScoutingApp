package com.example.ftcscoutingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class AutonomousInputForm : Fragment() {
    private lateinit var inputModulesMap: MutableMap<String, SingleInputModule>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var inflated = inflater.inflate(R.layout.fragment_autonomous_input_form, container, false)

        inputModulesMap = mutableMapOf(
            "Net Samples" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormAutonomousNetSamplesSubtractionButton), inflated.findViewById<Button>(R.id.inputFormAutonomousNetSamplesAdditionButton), inflated.findViewById<TextView>(R.id.inputFormAutonomousNetSamplesDisplay)),
            "Low Basket" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormAutonomousLowBasketSubtractionButton), inflated.findViewById<Button>(R.id.inputFormAutonomousLowBasketAdditionButton), inflated.findViewById<TextView>(R.id.inputFormAutonomousLowBasketDisplay)),
            "High Basket" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormAutonomousHighBasketSubtractionButton), inflated.findViewById<Button>(R.id.inputFormAutonomousHighBasketAdditionButton), inflated.findViewById<TextView>(R.id.inputFormAutonomousHighBasketDisplay)),
            "Low Specimens" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormAutonomousLowSpecimensSubtractionButton), inflated.findViewById<Button>(R.id.inputFormAutonomousLowSpecimensAdditionButton), inflated.findViewById<TextView>(R.id.inputFormAutonomousLowSpecimensDisplay)),
            "High Specimens" to SingleInputModule(inflated.findViewById<Button>(R.id.inputFormAutonomousHighSpecimensSubtractionButton), inflated.findViewById<Button>(R.id.inputFormAutonomousHighSpecimensAdditionButton), inflated.findViewById<TextView>(R.id.inputFormAutonomousHighSpecimensDisplay))
        )

        return inflated
    }

    fun getInputModuleValue(name: String): Int {
        return inputModulesMap[name]!!.numberValue
    }

    fun reset() {
        for (item in inputModulesMap.values) {
            item.numberValue = 0
        }
    }
}