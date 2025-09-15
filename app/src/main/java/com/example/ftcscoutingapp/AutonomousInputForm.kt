package com.example.ftcscoutingapp

import IntoTheDeepResults
import MatchResults
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

class AutonomousInputForm : Fragment() {
    private lateinit var didLeaveSwitch: SwitchCompat
    public lateinit var matchResult: MatchResults
    private lateinit var inputModulesMap: MutableMap<String, SingleInputModule>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var inflated = inflater.inflate(R.layout.fragment_autonomous_input_form, container, false)

        didLeaveSwitch = inflated.findViewById(R.id.inputFormAutonomousDidLeaveSwitch)
        didLeaveSwitch.setOnCheckedChangeListener { _, isChecked ->
            matchResult.autonomousScoringMethods.setValueOfMethod("Did Leave", isChecked.toString())
        }

        var classifiedArtifactsFunction = { count: Long -> 3*count }
        var overflowArtifactsFunction = { count: Long -> 1*count }
        var patternArtifactsFunction = { count: Long -> 2*count }

        var everythingStringFunction = {score: Long -> "Score: ${score}p"}

        inputModulesMap = mutableMapOf(
            "Classified Artifacts" to SingleInputModule(
                "Classified Artifacts",
                inflated.findViewById<Button>(R.id.inputFormAutonomousClassifiedArtifactsSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormAutonomousClassifiedArtifactsAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousClassifiedArtifactsDisplay),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousClassifiedArtifactsScoreDisplay),
                classifiedArtifactsFunction,
                true,
                everythingStringFunction,
                matchResult
            ),
            "Overflow Artifacts" to SingleInputModule(
                "Overflow Artifacts",
                inflated.findViewById<Button>(R.id.inputFormAutonomousOverflowArtifactsSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormAutonomousOverflowArtifactsAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousOverflowArtifactsDisplay),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousOverflowArtifactsScoreDisplay),
                overflowArtifactsFunction,
                true,
                everythingStringFunction,
                matchResult
            ),
            "Artifacts Matching Pattern" to SingleInputModule(
                "Artifacts Matching Pattern",
                inflated.findViewById<Button>(R.id.inputFormAutonomousPatternArtifactsSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormAutonomousPatternArtifactsAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousPatternArtifactsDisplay),
                inflated.findViewById<TextView>(R.id.inputFormAutonomousPatternArtifactsScoreDisplay),
                patternArtifactsFunction,
                true,
                everythingStringFunction,
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