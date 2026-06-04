package healthapp;

// Class: UserProfile
// Creator: Andrea Nguang (103324)
// Tester: Muhammad Izzat (105244)
// OOP: Encapsulation

public class UserProfile {

    // private fields (encapsulation)
    private String username;
    private int pagesVisited;
    private int totalPagesCompleted;
    private boolean learningCompleted;

    // constructor
    public UserProfile(String username) {
        this.username = username;
        this.pagesVisited = 0;
        this.totalPagesCompleted = 0;
        this.learningCompleted = false;
    }

    // getters
    public String getUsername()          { return username; }
    public int getPagesVisited()         { return pagesVisited; }
    public int getTotalPagesCompleted()  { return totalPagesCompleted; }
    public boolean isLearningCompleted() { return learningCompleted; }

    // setters
    public void setUsername(String username) { this.username = username; }

    // mark one page as visited
    public void markPageVisited() {
        this.pagesVisited++;
    }

    // mark learning module as completed
    public void completeLearning(int totalPages) {
        this.totalPagesCompleted = totalPages;
        this.learningCompleted = true;
    }

    // reset all progress
    public void resetProgress() {
        this.pagesVisited = 0;
        this.totalPagesCompleted = 0;
        this.learningCompleted = false;
    }

    // return progress as a string
    public String getProgressSummary() {
        return "User: " + username
             + " | Pages Visited: " + pagesVisited
             + " | Completed: " + (learningCompleted ? "Yes" : "No");
    }

    @Override
    public String toString() {
        return "UserProfile{username='" + username
             + "', pagesVisited=" + pagesVisited
             + ", learningCompleted=" + learningCompleted + "}";
    }
}
