import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// match results for the 2024-2025 season IntoTheDeep
public class IntoTheDeepResults extends MatchResults {
    public static enum AscentLevel {
        NONE(0),
        PARKING(3),
        FIRST_LEVEL(3),
        SECOND_LEVEL(15),
        THIRD_LEVEL(30);

        public int value;

        private AscentLevel(int value) {
            this.value = value;
        }
    }

    public static class ScoringMethods {
        private int netSamples;
        private int lowBasketSamples;
        private int highBasketSamples;
        private int lowSpecimens;
        private int highSpecimens;
        private AscentLevel ascentLevel;

        public ScoringMethods(int netSamples, int lowBasketSamples, int highBasketSamples, int lowSpecimens, int highSpecimens, AscentLevel ascentLevel) {
            this.netSamples = netSamples;
            this.lowBasketSamples = lowBasketSamples;
            this.highBasketSamples = highBasketSamples;
            this.lowSpecimens = lowSpecimens;
            this.highSpecimens = highSpecimens;
            this.ascentLevel = ascentLevel;
        }

        public int getNetSamples() {
            return netSamples;
        }

        public int getLowBasketSamples() {
            return lowBasketSamples;
        }

        public int getHighBasketSamples() {
            return highBasketSamples;
        }

        public int getLowSpecimens() {
            return lowSpecimens;
        }

        public int getHighSpecimens() {
            return highSpecimens;
        }

        public AscentLevel getAscentLevel() {
            return ascentLevel;
        }

        public int calculateScore() {
            return (netSamples*2) + (lowBasketSamples*4) + (highBasketSamples*8) + (lowSpecimens*5) + (highSpecimens*10) + (ascentLevel.value);
        }
    }

    private ScoringMethods opMode;
    private ScoringMethods autonomous;

    public IntoTheDeepResults(int netSamplesAuto, int lowBasketSamplesAuto, int highBasketSamplesAuto, int lowSpecimensAuto, int highSpecimensAuto, int netSamplesOpMode, int lowBasketSamplesOpMode, int highBasketSamplesOpMode, int lowSpecimensOpMode, int highSpecimensOpMode, AscentLevel opModeAscent) {
        this.autonomous = new ScoringMethods(netSamplesAuto, lowBasketSamplesAuto, highBasketSamplesAuto, lowSpecimensAuto, highSpecimensAuto, AscentLevel.NONE);
        this.opMode = new ScoringMethods(netSamplesOpMode, lowBasketSamplesOpMode, highBasketSamplesOpMode, lowSpecimensOpMode, highSpecimensOpMode, opModeAscent);
    }

    @Override
    public Map<String, Integer> getScoringMethodPoints() {
        Map<String, Integer> map = new HashMap<>();
        map.put("samples", (opMode.netSamples*2) + (opMode.lowBasketSamples*4) + (opMode.highBasketSamples*8) + (2*((autonomous.netSamples*2) + (autonomous.lowBasketSamples*4) + (autonomous.highBasketSamples*8))));
        map.put("specimens", (opMode.lowSpecimens*5) + (opMode.highSpecimens*10) + (2*((opMode.lowSpecimens*5) + (opMode.highSpecimens*10))));
        return map;
    }

    @Override
    public Map<String, Integer> getScoringMethodPointsNoAutonomousMultiplication() {
        Map<String, Integer> map = new HashMap<>();
        map.put("samples", (opMode.netSamples*2) + (opMode.lowBasketSamples*4) + (opMode.highBasketSamples*8) + (autonomous.netSamples*2) + (autonomous.lowBasketSamples*4) + (autonomous.highBasketSamples*8));
        map.put("specimens", (opMode.lowSpecimens*5) + (opMode.highSpecimens*10) + (opMode.lowSpecimens*5) + (opMode.highSpecimens*10));
        return map;
    }

    @Override
    public Map<String, Map<String, Integer>> getDatabaseEntry() {
        Map<String, Integer> opModeMap = new HashMap<>();
        opModeMap.put("netSamples", opMode.netSamples);
        opModeMap.put("lowBasketSamples", opMode.lowBasketSamples);
        opModeMap.put("highBasketSamples", opMode.highBasketSamples);
        opModeMap.put("lowSpecimens", opMode.lowSpecimens);
        opModeMap.put("highSpecimens", opMode.highSpecimens);
        opModeMap.put("ascentScore", opMode.ascentLevel.value);

        Map<String, Integer> autonomousMap = new HashMap<>();
        autonomousMap.put("netSamples", autonomous.netSamples);
        autonomousMap.put("lowBasketSamples", autonomous.lowBasketSamples);
        autonomousMap.put("highBasketSamples", autonomous.highBasketSamples);
        autonomousMap.put("lowSpecimens", autonomous.lowSpecimens);
        autonomousMap.put("highSpecimens", autonomous.highSpecimens);
        autonomousMap.put("ascentScore", 0);

        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("opMode", opModeMap);
        map.put("autonomous", autonomousMap);

        return map;
    }

    @Override
    public int getOpModeScore() {
        return opMode.calculateScore();
    }

    @Override
    public int getAutonomousScore() {
        return autonomous.calculateScore()*2;
    }
}
