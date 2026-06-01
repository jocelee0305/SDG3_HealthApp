package healthapp;

// ============================================================
// Interface  : ScoreStorage
// Creator    : Jocelyn (104561)
// Tester     : 
// Description: Defines the contract for saving, loading, and
//              displaying user scores for the HealthApp.
// ============================================================

public interface ScoreStorage {

    // Save score (basic — auto-generates date)
    void saveScore(String username, int score);

    // Save score with a specific date (method overloading in class)
    void saveScore(String username, int score, String date);

    // Load the highest score for a given username (-1 if not found)
    int loadScore(String username);

    // Display all scores sorted from highest to lowest
    void displayLeaderboard();

    // Clear all saved scores
    void clearScores();
}