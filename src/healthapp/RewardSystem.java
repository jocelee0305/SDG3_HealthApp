package healthapp;

// Class: RewardSystem
// Creator: Member 3
// Tester: Member 2

public class RewardSystem {

    private int    points;
    private String badge;

    public RewardSystem() {
        this.points = 0;
        this.badge  = "None";
    }

    public void addPoints(int p) { this.points += p; }
    public int  getPoints()      { return points; }
    public String getBadge()     { return badge; }

    // assign badge based on score
    public void assignBadge(int score) {
        if      (score >= 80) badge = "Gold";
        else if (score >= 60) badge = "Silver";
        else if (score >= 40) badge = "Bronze";
        else                  badge = "Keep Going";
    }

    // return motivational message based on score
    public String getMotivationalMessage(int score) {
        if      (score >= 80) return "Outstanding!";
        else if (score >= 60) return "That's good!";
        else if (score >= 40) return "Good try!";
        else if (score >= 20) return "You can do better!";
        else                  return "Don't give up!";
    }
}
