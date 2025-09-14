import android.widget.TextView;

import java.util.Map;

public abstract class MatchResults {
    public abstract long getOpModeScore();

    //modifiers such as *2 score are included here
    public abstract long getAutonomousScore();

    public void updateTotalScoreDisplay(TextView totalScoreDisplay) {
        if (totalScoreDisplay != null) {
            StringBuilder builder = new StringBuilder("Total Score: ");
            builder.append(getTotalScore());
            builder.append("p");
            totalScoreDisplay.setText(builder.toString());
        }
    }

    public abstract void clearScores();

    // the initial map has an auto and opMode seperation, and then scoring ways by counting or other if applicable
    public abstract Map<String, Map<String, String>> getDatabaseEntry();

    public abstract String[] getExcelRowData();
    public long getTotalScore() {
        return getOpModeScore() + getAutonomousScore();
    }
}
