import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DecodeResults extends MatchResults {
    public TextView totalScoreDisplay;
    public static String[] excelTitle = {"Team Name", "Date of Entry", "Autonomous Classified Artifacts", "Autonomous Overflow Artifacts", "Autonomous Artifacts Matching Pattern", "Autonomous Did Leave", "Teleop Classified Artifacts", "Teleop Overflow Artifacts", "Teleop Depot Artifacts", "Teleop Artifacts Matching Pattern", "Teleop Base Return"};

    public enum BaseReturn {
        NONE(0),
        PARTIAL(5),
        FULL(10),
        BONUS(10);

        public long value;

        private BaseReturn(long value) {
            this.value = value;
        }

        public static BaseReturn fromString(String value) {
            if (value.equals("None")){
                return NONE;
            }
            else if (value.equals("Partial")) {
                return PARTIAL;
            }
            else if (value.equals("Full")) {
                return FULL;
            }
            else if (value.equals("Bonus")) {
                return BONUS;
            }
            return NONE;
        }

        @NonNull
        @Override
        public String toString() {
            if (this == NONE) {
                return "None";
            }
            else if (this == PARTIAL) {
                return "Partial";
            }
            else if (this == FULL) {
                return "Full";
            }
            else if (this == BONUS) {
                return "Bonus";
            }
            return "None";
        }
    }

    public static class DecodeScoringMethods extends ScoringMethods {
        public long classifiedArtifacts;
        public long overflowArtifacts;
        public long depotArtifacts;
        public long artifactsMatchingPattern;
        public boolean didLeave;
        public BaseReturn baseReturn;
        public DecodeResults containingClass;


        public DecodeScoringMethods(String classifiedArtifacts, String overflowArtifacts, String depotArtifacts, String artifactsMatchingPattern, String didLeave, String baseReturn, DecodeResults containingClass) {
            this.classifiedArtifacts = Long.parseLong(classifiedArtifacts);
            this.overflowArtifacts = Long.parseLong(overflowArtifacts);
            this.depotArtifacts = Long.parseLong(depotArtifacts);
            this.artifactsMatchingPattern = Long.parseLong(artifactsMatchingPattern);
            this.didLeave = Boolean.parseBoolean(didLeave);
            this.baseReturn = BaseReturn.fromString(baseReturn);
            this.containingClass = containingClass;
        }

        @Override
        public void setScoreOfMethod(String method, String value) {
            if (method.equals("Classified Artifacts")) {
                this.classifiedArtifacts = Long.parseLong(value);
            }
            else if (method.equals("Overflow Artifacts")) {
                this.overflowArtifacts = Long.parseLong(value);
            }
            else if (method.equals("Depot Artifacts")) {
                this.depotArtifacts = Long.parseLong(value);
            }
            else if (method.equals("Artifacts Matching Pattern")) {
                this.artifactsMatchingPattern = Long.parseLong(value);
            }
            else if (method.equals("Did Leave")) {
                this.didLeave = Boolean.parseBoolean(value);
            }
            else if (method.equals("Base Return")) {
                this.baseReturn = BaseReturn.fromString(value);
            }

            if (containingClass != null) {
                containingClass.updateTotalScoreDisplay(containingClass.totalScoreDisplay);
            }
        }

        @Override
        public String getScoreOfMethod(String method) {
            if (method.equals("Classified Artifacts")) {
                return Long.toString(this.classifiedArtifacts);
            }
            else if (method.equals("Overflow Artifacts")) {
                return Long.toString(this.overflowArtifacts);
            }
            else if (method.equals("Depot Artifacts")) {
                return Long.toString(this.depotArtifacts);
            }
            else if (method.equals("Artifacts Matching Pattern")) {
                return Long.toString(this.artifactsMatchingPattern);
            }
            else if (method.equals("Did Leave")) {
                return Boolean.toString(this.didLeave);
            }
            else if (method.equals("Base Return")) {
                return this.baseReturn.toString();
            }
            throw new IllegalArgumentException();
        }

        @Override
        public long calculateScore() {
            return (3*classifiedArtifacts) + (1*overflowArtifacts) + (1*depotArtifacts) + (2*artifactsMatchingPattern) + (didLeave ? 3 : 0) + (baseReturn.value);
        }
    }

    private DecodeScoringMethods opMode;
    private DecodeScoringMethods autonomous;

    public DecodeResults(TextView totalScoreDisplay) {
        this.totalScoreDisplay = totalScoreDisplay;
        this.autonomous = new DecodeScoringMethods("0", "0", "0", "0", "false", "NONE", this);
        this.opMode = new DecodeScoringMethods("0", "0", "0", "0", "false", "NONE", this);
    }

    public DecodeResults(Map<String, Map<String, String>> databaseEntry, TextView totalScoreDisplay) {
        this.totalScoreDisplay = totalScoreDisplay;
        this.autonomous = new DecodeScoringMethods(
                databaseEntry.get("autonomous").get("classifiedArtifacts"),
                databaseEntry.get("autonomous").get("overflowArtifacts"),
                databaseEntry.get("autonomous").get("depotArtifacts"),
                databaseEntry.get("autonomous").get("artifactsMatchingPattern"),
                databaseEntry.get("autonomous").get("didLeave"),
                databaseEntry.get("autonomous").get("baseReturn"),
                this
        );
        this.opMode = new DecodeScoringMethods(
                databaseEntry.get("opMode").get("classifiedArtifacts"),
                databaseEntry.get("opMode").get("overflowArtifacts"),
                databaseEntry.get("opMode").get("depotArtifacts"),
                databaseEntry.get("opMode").get("artifactsMatchingPattern"),
                databaseEntry.get("opMode").get("didLeave"),
                databaseEntry.get("opMode").get("baseReturn"),
                this
        );
    }

    @Override
    public long getOpModeScore() {
        return opMode.calculateScore();
    }

    @Override
    public long getAutonomousScore() {
        return autonomous.calculateScore();
    }

    @Override
    public void clearScores() {
        this.autonomous = new DecodeScoringMethods("0", "0", "0", "0", "false", "NONE", this);
        this.opMode = new DecodeScoringMethods("0", "0", "0", "0", "false", "NONE", this);
        updateTotalScoreDisplay(totalScoreDisplay);
    }

    @Override
    public Map<String, Map<String, String>> getDatabaseEntry() {
        Map<String, String> autonomousMap = new HashMap<>(6);
        autonomousMap.put("classifiedArtifacts", Long.toString(autonomous.classifiedArtifacts));
        autonomousMap.put("overflowArtifacts", Long.toString(autonomous.overflowArtifacts));
        autonomousMap.put("depotArtifacts", "0");
        autonomousMap.put("artifactsMatchingPattern", Long.toString(autonomous.artifactsMatchingPattern));
        autonomousMap.put("didLeave", Boolean.toString(autonomous.didLeave));
        autonomousMap.put("baseReturn", "NONE");

        Map<String, String> opModeMap = new HashMap<>(6);
        opModeMap.put("classifiedArtifacts", Long.toString(opMode.classifiedArtifacts));
        opModeMap.put("overflowArtifacts", Long.toString(opMode.overflowArtifacts));
        opModeMap.put("depotArtifacts", Long.toString(opMode.depotArtifacts));
        opModeMap.put("artifactsMatchingPattern", Long.toString(opMode.artifactsMatchingPattern));
        opModeMap.put("didLeave", "false");
        opModeMap.put("baseReturn", opMode.baseReturn.toString());


        Map<String, Map<String, String>> map = new HashMap<>();
        map.put("opMode", opModeMap);
        map.put("autonomous", autonomousMap);

        return map;
    }

    @Override
    public String[] getExcelRowData() {
        return new String[]{
                Long.toString(autonomous.classifiedArtifacts),
                Long.toString(autonomous.overflowArtifacts),
                Long.toString(autonomous.artifactsMatchingPattern),
                Boolean.toString(autonomous.didLeave),
                Long.toString(opMode.classifiedArtifacts),
                Long.toString(opMode.overflowArtifacts),
                Long.toString(opMode.depotArtifacts),
                Long.toString(opMode.artifactsMatchingPattern),
                opMode.baseReturn.toString()
        };
    }
}
