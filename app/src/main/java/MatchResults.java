import java.util.Map;

public abstract class MatchResults {
    public abstract int getOpModeScore();

    //modifiers such as *2 score are included here
    public abstract int getAutonomousScore();

    // maps the name of a scoring method to the points gained from that method (i.e. how many points did that team get from samples and specimens in into the deep)
    public abstract Map<String, Integer> getScoringMethodPoints();

    // maps the name of a scoring method to the points gained from that method (i.e. how many points did that team get from samples and specimens in into the deep)
    public abstract Map<String, Integer> getScoringMethodPointsNoAutonomousMultiplication();
    // the initial map has an auto and opMode seperation, and then scoring ways by counting or other if applicable
    public abstract Map<String, Map<String, Integer>> getDatabaseEntry();
    public int getTotalScore() {
        return getOpModeScore() + getAutonomousScore();
    }
}
