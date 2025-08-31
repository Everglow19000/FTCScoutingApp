public abstract class ScoringMethods {
    abstract void setScoreOfMethod(String method, Long score);
    abstract long getScoreOfMethod(String method);

    abstract long calculateScore();
}
