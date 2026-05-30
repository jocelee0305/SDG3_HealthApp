package healthapp;

// Interface: ScoreStorage
// Creator: Member 3
// Tester: Member 2

public interface ScoreStorage {
    void saveScore(String username, int score);
    int  loadScore(String username);
    void displayLeaderboard();
}
