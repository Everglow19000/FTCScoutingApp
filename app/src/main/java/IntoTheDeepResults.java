import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

// match results for the 2024-2025 season IntoTheDeep
public class IntoTheDeepResults extends MatchResults {
    public TextView totalScoreDisplay;
    public static String[] excelTitle = {"Team Name", "Date of Entry", "Autonomous Net Samples", "Autnomous Low Samples", "Autonomous High Samples", "Autonomous Low Specimens", "Autonmous High Specimens", "Opmode Net Samples", "Opmode Low Samples", "Opmode High Samples", "Opmode Low Specimens", "Opmode High Specimens", "Opmode Ascent"};
    public static enum AscentLevel {
        NONE(0),
        PARKING(3),
        FIRST_LEVEL(3),
        SECOND_LEVEL(15),
        THIRD_LEVEL(30);

        public long value;

        private AscentLevel(long value) {
            this.value = value;
        }
        public static AscentLevel fromString(String value) {
            if (value.equals("None")) {
                return NONE;
            }
            else if (value.equals("Parking")) {
                return FIRST_LEVEL;
            }
            else if (value.equals("First Level")) {
                return FIRST_LEVEL;
            }
            else if (value.equals("Second Level")) {
                return SECOND_LEVEL;
            }
            else if (value.equals("Third Level")) {
                return THIRD_LEVEL;
            }
            return NONE;
        }

        public String getName() {
            String out = "INVALID";
            if (this == NONE) {
                out = "None";
            }
            else if (this == PARKING) {
                out = "Parking";
            }
            else if (this == FIRST_LEVEL) {
                out = "First Level";
            }
            else if (this == SECOND_LEVEL) {
                out = "Second Level";
            }
            else if (this == THIRD_LEVEL) {
                out = "Third Level";
            }
            return out;
        }
    }

    public static class IntoTheDeepScoringMethods extends ScoringMethods {
        public long netSamples;
        public long lowBasketSamples;
        public long highBasketSamples;
        public long lowSpecimens;
        public long highSpecimens;
        public AscentLevel ascentLevel;
        public IntoTheDeepResults containingClass;

        public IntoTheDeepScoringMethods(String netSamples, String lowBasketSamples, String highBasketSamples, String lowSpecimens, String highSpecimens, AscentLevel ascentLevel, IntoTheDeepResults containingClass) {
            this.netSamples = Long.parseLong(netSamples);
            this.lowBasketSamples = Long.parseLong(lowBasketSamples);
            this.highBasketSamples = Long.parseLong(highBasketSamples);
            this.lowSpecimens = Long.parseLong(lowSpecimens);
            this.highSpecimens = Long.parseLong(highSpecimens);
            this.ascentLevel = ascentLevel;
            this.containingClass = containingClass;
        }

        public IntoTheDeepScoringMethods(long netSamples, long lowBasketSamples, long highBasketSamples, long lowSpecimens, long highSpecimens, AscentLevel ascentLevel, IntoTheDeepResults containingClass) {
            this.netSamples = netSamples;
            this.lowBasketSamples = lowBasketSamples;
            this.highBasketSamples = highBasketSamples;
            this.lowSpecimens = lowSpecimens;
            this.highSpecimens = highSpecimens;
            this.ascentLevel = ascentLevel;
            this.containingClass = containingClass;
        }

        public void setScoreOfMethod(String method, String score) {
            if (method.equals("Net Samples")) {
                netSamples = Long.parseLong(score)/2;
            }
            else if (method.equals("Low Basket Samples")) {
                lowBasketSamples = Long.parseLong(score)/4;
            }
            else if (method.equals("High Basket Samples")) {
                highBasketSamples = Long.parseLong(score)/8;
            }
            else if (method.equals("Low Specimens")) {
                lowSpecimens = Long.parseLong(score)/5;
            }
            else if (method.equals("High Specimens")) {
                highSpecimens = Long.parseLong(score)/10;
            }
            else if (method.equals("Ascent Level")) {
                ascentLevel = AscentLevel.fromString(score);
            }

            if (containingClass.totalScoreDisplay != null) {
                containingClass.updateTotalScoreDisplay(containingClass.totalScoreDisplay);
            }
        }

        public String getScoreOfMethod(String method) {
            if (method.equals("Net Samples")) {
                return Long.toString(2*netSamples);
            }
            else if (method.equals("Low Basket Samples")) {
                return Long.toString(4*lowBasketSamples);
            }
            else if (method.equals("High Basket Samples")) {
                return Long.toString(8*highBasketSamples);
            }
            else if (method.equals("Low Specimens")) {
                return Long.toString(5*lowSpecimens);
            }
            else if (method.equals("High Specimens")) {
                return Long.toString(10*highSpecimens);
            }
            else if (method.equals("Ascent Level")) {
                return ascentLevel.getName();
            }
            return "0";
        }

        public long calculateScore() {
            return (netSamples*2) + (lowBasketSamples*4) + (highBasketSamples*8) + (lowSpecimens*5) + (highSpecimens*10) + (ascentLevel.value);
        }
    }

    public IntoTheDeepScoringMethods opMode;
    public IntoTheDeepScoringMethods autonomous;

    public IntoTheDeepResults(long netSamplesAuto, long lowBasketSamplesAuto, long highBasketSamplesAuto, long lowSpecimensAuto, long highSpecimensAuto, long netSamplesOpMode, long lowBasketSamplesOpMode, long highBasketSamplesOpMode, long lowSpecimensOpMode, long highSpecimensOpMode, AscentLevel opModeAscent, TextView totalScoreDisplay) {
        this.totalScoreDisplay = totalScoreDisplay;
        this.autonomous = new IntoTheDeepScoringMethods(netSamplesAuto, lowBasketSamplesAuto, highBasketSamplesAuto, lowSpecimensAuto, highSpecimensAuto, AscentLevel.NONE, this);
        this.opMode = new IntoTheDeepScoringMethods(netSamplesOpMode, lowBasketSamplesOpMode, highBasketSamplesOpMode, lowSpecimensOpMode, highSpecimensOpMode, opModeAscent, this);
    }

    public static String TAG = "DatabaseHandlerTag";

    public IntoTheDeepResults(Map<String, Map<String, String>> databaseEntry, TextView totalScoreDisplay) {
        this.totalScoreDisplay = totalScoreDisplay;
        this.autonomous = new IntoTheDeepScoringMethods(databaseEntry.get("autonomous").get("netSamples"), databaseEntry.get("autonomous").get("lowBasketSamples"), databaseEntry.get("autonomous").get("highBasketSamples"), databaseEntry.get("autonomous").get("lowSpecimens"), databaseEntry.get("autonomous").get("highSpecimens"), AscentLevel.NONE, this);
        this.opMode = new IntoTheDeepScoringMethods(databaseEntry.get("opMode").get("netSamples"), databaseEntry.get("opMode").get("lowBasketSamples"), databaseEntry.get("opMode").get("highBasketSamples"), databaseEntry.get("opMode").get("lowSpecimens"), databaseEntry.get("opMode").get("highSpecimens"), AscentLevel.fromString(databaseEntry.get("opMode").get("ascentScore")), this);
    }


    @Override
    public Map<String, Map<String, String>> getDatabaseEntry() {
        Map<String, String> opModeMap = new HashMap<>();
        opModeMap.put("netSamples", Long.toString(opMode.netSamples));
        opModeMap.put("lowBasketSamples", Long.toString(opMode.lowBasketSamples));
        opModeMap.put("highBasketSamples", Long.toString(opMode.highBasketSamples));
        opModeMap.put("lowSpecimens", Long.toString(opMode.lowSpecimens));
        opModeMap.put("highSpecimens", Long.toString(opMode.highSpecimens));
        opModeMap.put("ascentScore", opMode.ascentLevel.getName());

        Map<String, String> autonomousMap = new HashMap<>();
        autonomousMap.put("netSamples", Long.toString(autonomous.netSamples));
        autonomousMap.put("lowBasketSamples", Long.toString(autonomous.lowBasketSamples));
        autonomousMap.put("highBasketSamples", Long.toString(autonomous.highBasketSamples));
        autonomousMap.put("lowSpecimens", Long.toString(autonomous.lowSpecimens));
        autonomousMap.put("highSpecimens", Long.toString(autonomous.highSpecimens));
        autonomousMap.put("ascentScore", "None");

        Map<String, Map<String, String>> map = new HashMap<>();
        map.put("opMode", opModeMap);
        map.put("autonomous", autonomousMap);

        return map;
    }

    @Override
    public String[] getExcelRowData() {
        return new String[]{
                Long.toString(autonomous.netSamples),
                Long.toString(autonomous.lowBasketSamples),
                Long.toString(autonomous.highBasketSamples),
                Long.toString(autonomous.lowSpecimens),
                Long.toString(autonomous.highSpecimens),
                Long.toString(opMode.netSamples),
                Long.toString(opMode.lowBasketSamples),
                Long.toString(opMode.highBasketSamples),
                Long.toString(opMode.lowSpecimens),
                Long.toString(opMode.highSpecimens),
                opMode.ascentLevel.toString()
        };
    }


    @Override
    public long getOpModeScore() {
        return opMode.calculateScore();
    }

    @Override
    public long getAutonomousScore() {
        return autonomous.calculateScore()*2;
    }

    @Override
    public void clearScores() {
        this.autonomous = new IntoTheDeepScoringMethods(0, 0, 0, 0, 0, AscentLevel.NONE, this);
        this.opMode = new IntoTheDeepScoringMethods(0, 0, 0, 0, 0, AscentLevel.NONE, this);
        if (totalScoreDisplay != null) {
            updateTotalScoreDisplay(totalScoreDisplay);
        }
    }
}
