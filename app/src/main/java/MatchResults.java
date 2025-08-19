import java.util.Map;

public abstract class MatchResults {
    public abstract long getOpModeScore();

    //modifiers such as *2 score are included here
    public abstract long getAutonomousScore();

    // maps the name of a scoring method to the points gained from that method (i.e. how many points did that team get from samples and specimens in into the deep)
    public abstract Map<String, Long> getScoringMethodPoints();

    // maps the name of a scoring method to the points gained from that method (i.e. how many points did that team get from samples and specimens in into the deep)
    public abstract Map<String, Long> getScoringMethodPointsNoAutonomousMultiplication();
    // the initial map has an auto and opMode seperation, and then scoring ways by counting or other if applicable
    public abstract Map<String, Map<String, Long>> getDatabaseEntry();

    public abstract long[] getScoringMethodsCounts();
    public abstract String[] getExcelTitles();
    public long getTotalScore() {
        return getOpModeScore() + getAutonomousScore();
    }
}
