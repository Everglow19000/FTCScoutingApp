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
    }

    public ScoreOverview scoreOverview;

    public String teamName;

    public TeamOverview(String teamName, MatchResults[] matches) {
        this.teamName = teamName;
        this.scoreOverview = new ScoreOverview(matches);
    }

    public TeamOverview(String teamName) {
        this.teamName = teamName;
        this.scoreOverview = new ScoreOverview(new MatchResults[0]);
    }
}
