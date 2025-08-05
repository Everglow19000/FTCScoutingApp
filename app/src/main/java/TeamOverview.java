import java.util.HashMap;
import java.util.Map;

public class TeamOverview {
    public static class ScoreOverview {
        private MatchResults[] matches;

        public ScoreOverview(MatchResults[] matches) {
            this.matches = matches;
        }

        public void setMatches(MatchResults[] matches) {
            this.matches = matches;
        }

        public MatchResults[] getMatches() {
            return matches;
        }

        public void addMatch(MatchResults match) {
            MatchResults[] temp = new MatchResults[matches.length+1];
            for (int i = 0; i < matches.length; i++) {
                temp[i] = matches[i];
            }
            temp[temp.length-1] = match;
            matches = temp;
        }

        public String getBestScoringMethod() {
            Map<String, Integer> cumulativeScore = new HashMap<>(matches[0].getScoringMethodPointsNoAutonomousMultiplication());

            for (int match = 1; match < matches.length; match++) {
                Map<String, Integer> scoringMethodPointsNoAuto = matches[match].getScoringMethodPointsNoAutonomousMultiplication();
                for (
                        String method :
                     scoringMethodPointsNoAuto.keySet()
                ) {
                    cumulativeScore.put(method, cumulativeScore.getOrDefault(method, 0) + scoringMethodPointsNoAuto.getOrDefault(method, 0));
                }
            }

            String bestFound = null;
            for (
                    String method :
                    cumulativeScore.keySet()
            ) {
                if (bestFound == null || cumulativeScore.get(method) > cumulativeScore.get(bestFound)) {
                    bestFound = method;
                }
            }

            return bestFound;
        }

        public double getAverageScore() {
            int sum = 0;
            for (int i = 0; i < matches.length; i++) {
                sum += matches[i].getTotalScore();
            }
            return (double)(sum)/(double)(matches.length);
        }

        public double standardDeviationOfScores() {
            double average = getAverageScore();
            double sumOfSquares = 0;
            for (int i = 0; i < matches.length; i++) {
                sumOfSquares += Math.pow((matches[i].getTotalScore() - average), 2);
            }
            sumOfSquares /= (double)(matches.length);
            return Math.sqrt(sumOfSquares);
        }

        public int getMaxScore() {
            int maxIndex = 0;
            for (int i = 0; i < matches.length; i++) {
                if (matches[i].getTotalScore() > matches[maxIndex].getTotalScore()) {
                    maxIndex = i;
                }
            }
            return matches[maxIndex].getTotalScore();
        }

        public int getMinScore() {
            int minIndex = 0;
            for (int i = 0; i < matches.length; i++) {
                if (matches[i].getTotalScore() < matches[minIndex].getTotalScore()) {
                    minIndex = i;
                }
            }
            return matches[minIndex].getTotalScore();
        }
    }

    public ScoreOverview scoreOverview;

    private String teamName;
}
