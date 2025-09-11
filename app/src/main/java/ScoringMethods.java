public abstract class ScoringMethods {
    abstract void setScoreOfMethod(String method, String value);
    abstract String getScoreOfMethod(String method);

    abstract long calculateScore();
}
