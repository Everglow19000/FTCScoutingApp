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
        public static AscentLevel fromScore(long score) throws IllegalArgumentException{
            if (score == 0) {
                return NONE;
            }
            else if (score == 3) {
                return FIRST_LEVEL;
            }
            else if (score == 15) {
                return SECOND_LEVEL;
            }
            else if (score == 30) {
                return THIRD_LEVEL;
            }
            throw new IllegalArgumentException("score must have a corresponding AscentLevel value!");
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

        public IntoTheDeepScoringMethods(long netSamples, long lowBasketSamples, long highBasketSamples, long lowSpecimens, long highSpecimens, AscentLevel ascentLevel, IntoTheDeepResults containingClass) {
            this.netSamples = netSamples;
            this.lowBasketSamples = lowBasketSamples;
            this.highBasketSamples = highBasketSamples;
            this.lowSpecimens = lowSpecimens;
            this.highSpecimens = highSpecimens;
            this.ascentLevel = ascentLevel;
            this.containingClass = containingClass;
        }

        public void setScoreOfMethod(String method, Long score) {
            if (method.equals("Net Samples")) {
                netSamples = score/2;
            }
            else if (method.equals("Low Basket Samples")) {
                lowBasketSamples = score/4;
            }
            else if (method.equals("High Basket Samples")) {
                highBasketSamples = score/8;
            }
            else if (method.equals("Low Specimens")) {
                lowSpecimens = score/5;
            }
            else if (method.equals("High Specimens")) {
                highSpecimens = score/10;
            }
            else if (method.equals("Ascent Level")) {
                ascentLevel = AscentLevel.fromScore(score);
            }

            if (containingClass.totalScoreDisplay != null) {
                containingClass.updateTotalScoreDisplay(containingClass.totalScoreDisplay);
            }
        }

        public long getScoreOfMethod(String method) {
            if (method.equals("Net Samples")) {
                return 2*netSamples;
            }
            else if (method.equals("Low Basket Samples")) {
                return 4*lowBasketSamples;
            }
            else if (method.equals("High Basket Samples")) {
                return 8*highBasketSamples;
            }
            else if (method.equals("Low Specimens")) {
                return 5*lowSpecimens;
            }
            else if (method.equals("High Specimens")) {
                return 10*highSpecimens;
            }
            else if (method.equals("Ascent Level")) {
                return ascentLevel.value;
            }
            return 0;
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

    public IntoTheDeepResults(Map<String, Map<String, Long>> databaseEntry, TextView totalScoreDisplay) {
        this.totalScoreDisplay = totalScoreDisplay;
        this.autonomous = new IntoTheDeepScoringMethods(databaseEntry.get("autonomous").get("netSamples"), databaseEntry.get("autonomous").get("lowBasketSamples"), databaseEntry.get("autonomous").get("highBasketSamples"), databaseEntry.get("autonomous").get("lowSpecimens"), databaseEntry.get("autonomous").get("highSpecimens"), AscentLevel.NONE, this);
        this.opMode = new IntoTheDeepScoringMethods(databaseEntry.get("opMode").get("netSamples"), databaseEntry.get("opMode").get("lowBasketSamples"), databaseEntry.get("opMode").get("highBasketSamples"), databaseEntry.get("opMode").get("lowSpecimens"), databaseEntry.get("opMode").get("highSpecimens"), AscentLevel.fromScore(databaseEntry.get("opMode").get("ascentScore")), this);
    }

    @Override
    public Map<String, Long> getScoringMethodPoints() {
        Map<String, Long> map = new HashMap<>();
        map.put("samples", (opMode.netSamples*2) + (opMode.lowBasketSamples*4) + (opMode.highBasketSamples*8) + (2*((autonomous.netSamples*2) + (autonomous.lowBasketSamples*4) + (autonomous.highBasketSamples*8))));
        map.put("specimens", (opMode.lowSpecimens*5) + (opMode.highSpecimens*10) + (2*((opMode.lowSpecimens*5) + (opMode.highSpecimens*10))));
        return map;
    }

    @Override
    public Map<String, Long> getScoringMethodPointsNoAutonomousMultiplication() {
        Map<String, Long> map = new HashMap<>();
        map.put("samples", (opMode.netSamples*2) + (opMode.lowBasketSamples*4) + (opMode.highBasketSamples*8) + (autonomous.netSamples*2) + (autonomous.lowBasketSamples*4) + (autonomous.highBasketSamples*8));
        map.put("specimens", (opMode.lowSpecimens*5) + (opMode.highSpecimens*10) + (opMode.lowSpecimens*5) + (opMode.highSpecimens*10));
        return map;
    }

    @Override
    public Map<String, Map<String, Long>> getDatabaseEntry() {
        Map<String, Long> opModeMap = new HashMap<>();
        opModeMap.put("netSamples", opMode.netSamples);
        opModeMap.put("lowBasketSamples", opMode.lowBasketSamples);
        opModeMap.put("highBasketSamples", opMode.highBasketSamples);
        opModeMap.put("lowSpecimens", opMode.lowSpecimens);
        opModeMap.put("highSpecimens", opMode.highSpecimens);
        opModeMap.put("ascentScore", opMode.ascentLevel.value);

        Map<String, Long> autonomousMap = new HashMap<>();
        autonomousMap.put("netSamples", autonomous.netSamples);
        autonomousMap.put("lowBasketSamples", autonomous.lowBasketSamples);
        autonomousMap.put("highBasketSamples", autonomous.highBasketSamples);
        autonomousMap.put("lowSpecimens", autonomous.lowSpecimens);
        autonomousMap.put("highSpecimens", autonomous.highSpecimens);
        autonomousMap.put("ascentScore", 0L);

        Map<String, Map<String, Long>> map = new HashMap<>();
        map.put("opMode", opModeMap);
        map.put("autonomous", autonomousMap);

        return map;
    }

    @Override
    public long[] getScoringMethodsCounts() {
        return new long[]{autonomous.netSamples, autonomous.lowBasketSamples, autonomous.highBasketSamples, autonomous.lowSpecimens, autonomous.highSpecimens, opMode.netSamples, opMode.lowBasketSamples, opMode.highBasketSamples, opMode.lowSpecimens, opMode.highSpecimens, opMode.ascentLevel.value};
    }

    @Override
    public String[] getExcelTitles() {
        return excelTitle;
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

    @Override
    public ScoringMethods getAutonomousScoringMethods() {
        return autonomous;
    }

    @Override
    public ScoringMethods getOpModeScoringMethods() {
        return opMode;
    }
}
