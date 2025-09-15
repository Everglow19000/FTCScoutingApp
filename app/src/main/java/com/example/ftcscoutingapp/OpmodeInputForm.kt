package com.example.ftcscoutingapp

import DecodeResults
import MatchResults
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
    private lateinit var BaseReturnMap: MutableMap<Int, DecodeResults.BaseReturn>
    private lateinit var radioGroup: RadioGroup
    public lateinit var matchResult: MatchResults
    private var checkedBaseReturn: DecodeResults.BaseReturn = DecodeResults.BaseReturn.NONE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var inflated = inflater.inflate(R.layout.fragment_opmode_input_form, container, false)

        BaseReturnMap = mutableMapOf(
            R.id.inputFormOpModeBaseReturnButtonNone to DecodeResults.BaseReturn.NONE,
            R.id.inputFormOpModeBaseReturnButtonPartial to DecodeResults.BaseReturn.PARTIAL,
            R.id.inputFormOpModeBaseReturnButtonFull to DecodeResults.BaseReturn.FULL,
            R.id.inputFormOpModeBaseReturnButtonBonus to DecodeResults.BaseReturn.BONUS,
        )

        radioGroup = inflated.findViewById(R.id.inputFormOpModeBaseReturnGroup)
        radioGroup.setOnCheckedChangeListener(
            object: RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(
                    group: RadioGroup,
                    checkedId: Int
                ) {
                    checkedBaseReturn = BaseReturnMap.getOrDefault(checkedId, DecodeResults.BaseReturn.NONE)
                    matchResult.teleopScoringMethods.setValueOfMethod("Base Return", checkedBaseReturn.toString())
                }
            }
        )

        var classifiedArtifactsFunction = { count: Long -> 3*count }
        var overflowAndDepotArtifactsFunction = { count: Long -> 1*count }
        var patternArtifactsFunction = { count: Long -> 2*count }

        var everythingStringFunction = {score: Long -> "Score: ${score}p"}

        inputModulesMap = mutableMapOf(
            "Classified Artifacts" to SingleInputModule(
                "Classified Artifacts",
                inflated.findViewById<Button>(R.id.inputFormOpModeClassifiedArtifactsSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModeClassifiedArtifactsAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModeClassifiedArtifactsDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModeClassifiedArtifactsScoreDisplay),
                classifiedArtifactsFunction,
                false,
                everythingStringFunction,
                matchResult
            ),
            "Overflow Artifacts" to SingleInputModule(
                "Overflow Artifacts",
                inflated.findViewById<Button>(R.id.inputFormOpModeOverflowArtifactsSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModeOverflowArtifactsAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModeOverflowArtifactsDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModeOverflowArtifactsScoreDisplay),
                overflowAndDepotArtifactsFunction,
                false,
                everythingStringFunction,
                matchResult
            ),
            "Depot Artifacts" to SingleInputModule(
                "Depot Artifacts",
                inflated.findViewById<Button>(R.id.inputFormOpModeDepotArtifactsSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModeDepotArtifactsAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModeDepotArtifactsDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModeDepotArtifactsScoreDisplay),
                overflowAndDepotArtifactsFunction,
                false,
                everythingStringFunction,
                matchResult
            ),
            "Artifacts Matching Pattern" to SingleInputModule(
                "Artifacts Matching Pattern",
                inflated.findViewById<Button>(R.id.inputFormOpModePatternArtifactsSubtractionButton),
                inflated.findViewById<Button>(R.id.inputFormOpModePatternArtifactsAdditionButton),
                inflated.findViewById<TextView>(R.id.inputFormOpModePatternArtifactsDisplay),
                inflated.findViewById<TextView>(R.id.inputFormOpModePatternArtifactsScoreDisplay),
                patternArtifactsFunction,
                false,
                everythingStringFunction,
                matchResult
            )
        )

        return inflated

    }

    fun getInputModuleValue(name: String): Long {
        return inputModulesMap[name]!!.numberValue.toLong()
    }

    fun getBaseReturn(): DecodeResults.BaseReturn {
        return checkedBaseReturn
    }

    fun reset() {
        for (item in inputModulesMap.values) {
            item.numberValue = 0
        }
        radioGroup.clearCheck()
    }
}