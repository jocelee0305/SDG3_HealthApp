package healthapp;

// ============================================================
// Class      : RewardSystem
// Creator    : Jocelyn (104561)
// Tester     : Lee Xing Ying (104731)
// OOP        : File I/O, Method Overloading, Exception Handling,
//              Interface Implementation (ScoreStorage)
// Description: Handles gamification (badges, points, stars),
//              saves/loads scores using a text file, and
//              displays a leaderboard panel.
//              Called by QuizManager after quiz completion.
// ============================================================

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RewardSystem extends JPanel implements ScoreStorage {

    // ── Colours & Fonts (matches the rest of the app) ────────
    private static final Color BG_COLOR      = new Color(240, 248, 245);
    private static final Color PRIMARY_COLOR = new Color(34, 139, 87);
    private static final Color ACCENT_COLOR  = new Color(255, 193, 7);
    private static final Color TEXT_COLOR    = new Color(30, 40, 35);
    private static final Font  TITLE_FONT    = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font  BODY_FONT     = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font  SMALL_FONT    = new Font("Segoe UI", Font.ITALIC, 11);

    // ── File for storing scores ───────────────────────────────
    private static final String SCORE_FILE = "../scores.txt";

    // ── User data ─────────────────────────────────────────────
    private String username;
    private int    score;        // percentage score 0–100
    private int    rawScore;     // e.g. 15 out of 20
    private int    totalQ;       // total questions

    // ── GUI components ────────────────────────────────────────
    private JLabel    lblBadge;
    private JLabel    lblStars;
    private JLabel    lblPoints;
    private JLabel    lblMessage;
    private JTextArea leaderboardArea;

    // ── Constructor (called by QuizManager) ───────────────────
    // Parameters: userProfile from Member 1, rawScore and totalQ from Member 2
    public RewardSystem(UserProfile userProfile, int rawScore, int totalQ) {
        this.username = userProfile.getUsername();
        this.rawScore = rawScore;
        this.totalQ   = totalQ;
        this.score    = (totalQ > 0) ? (int) Math.round((double) rawScore / totalQ * 100) : 0;

        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Save score first so leaderboard includes the current result
        saveScore(username, score);

        buildHeader();
        buildResultCard();
        buildLeaderboardPanel();
    }

    // ── Build top header bar ──────────────────────────────────
    private void buildHeader() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("SDG 3: Good Health & Well-being  |  Your Rewards");
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(Color.WHITE);

        topPanel.add(title, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
    }

    // ── Build the result card (badge, stars, points, message) ─
    private void buildResultCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 230, 210), 2, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Score label
        JLabel lblScore = makeCenter(
            "Score: " + rawScore + " / " + totalQ + "  (" + score + "%)",
            new Font("Segoe UI", Font.BOLD, 22), PRIMARY_COLOR);

        // Badge — emoji font covers the badge icon and text
        lblBadge = makeCenter(determineBadge(score),
            new Font("Segoe UI Emoji", Font.PLAIN, 15), TEXT_COLOR);

        // Stars — HTML label: gold ★ for earned, grey ★ for remaining (avoids ☆ rendering issue)
        lblStars = makeCenter(getStarHTML(calculateStars(score)),
            new Font("Segoe UI", Font.PLAIN, 22), ACCENT_COLOR);

        // Points — emoji font so the coin icon renders
        lblPoints = makeCenter("\uD83D\uDCB0 Points Earned: " + calculatePoints(score),
            new Font("Segoe UI Emoji", Font.PLAIN, 14), new Color(60, 100, 70));

        // Motivational message — no emoji, plain font is fine
        lblMessage = makeCenter(getMotivationalMessage(score),
            new Font("Segoe UI", Font.ITALIC, 16), getMessageColor(score));

        // User greeting — emoji font so the party popper renders
        JLabel lblUser = makeCenter("Well done, " + username + "! \uD83C\uDF89",
            new Font("Segoe UI Emoji", Font.PLAIN, 11), new Color(120, 160, 130));

        card.add(Box.createVerticalStrut(8));
        card.add(lblScore);
        card.add(Box.createVerticalStrut(10));
        card.add(lblBadge);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStars);
        card.add(Box.createVerticalStrut(8));
        card.add(lblPoints);
        card.add(Box.createVerticalStrut(12));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(12));
        card.add(lblMessage);
        card.add(Box.createVerticalStrut(6));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(8));

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_COLOR);
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        wrap.add(card, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);
    }

    // ── Build leaderboard section at bottom ───────────────────
    private void buildLeaderboardPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        leaderboardArea = new JTextArea(6, 30);
        leaderboardArea.setEditable(false);
        leaderboardArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        leaderboardArea.setBackground(new Color(255, 255, 240));
        JScrollPane scroll = new JScrollPane(leaderboardArea);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
            "\uD83C\uDFC6 Leaderboard", 0, 0,
            new Font("Segoe UI Emoji", Font.BOLD, 12), PRIMARY_COLOR));
        bottomPanel.add(scroll, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Auto-load leaderboard on open
        displayLeaderboard();
    }

    // ── Gamification Logic ────────────────────────────────────

    /** Assign badge based on score percentage. */
    public String determineBadge(int score) {
        if      (score >= 80) return "\uD83C\uDFC6 Gold Badge  —  Outstanding!";
        else if (score >= 60) return "\uD83E\uDD48 Silver Badge  —  That's good!";
        else if (score >= 40) return "\uD83E\uDD49 Bronze Badge  —  Good try!";
        else if (score >= 20) return "\uD83C\uDF97 Iron Badge  —  You can do better!";
        else                  return "\uD83C\uDF96 Participant Badge  —  Don't give up!";
    }

    /** Motivational message matching project rubric. */
    public String getMotivationalMessage(int score) {
        if      (score >= 80) return "Outstanding!";
        else if (score >= 60) return "That's good!";
        else if (score >= 40) return "Good try!";
        else if (score >= 20) return "You can do better!";
        else                  return "Don't give up!";
    }

    /**
     * Calculate points from score only.
     * METHOD OVERLOADING — version 1
     */
    public int calculatePoints(int score) {
        return score * 10;
    }

    /**
     * Calculate points with a time bonus.
     * METHOD OVERLOADING — version 2 (timeBonus = remaining timer seconds)
     */
    public int calculatePoints(int score, int timeBonus) {
        return (score * 10) + (timeBonus * 2);
    }

    /** Calculate star rating out of 5. */
    public int calculateStars(int score) {
        if      (score >= 80) return 5;
        else if (score >= 60) return 4;
        else if (score >= 40) return 3;
        else if (score >= 20) return 2;
        else                  return 1;
    }

    private String getStarHTML(int stars) {
        StringBuilder sb = new StringBuilder("<html><center>");
        for (int i = 0; i < 5; i++) {
            if (i < stars) {
                sb.append("<font color=\'#FFC107\'>\u2605</font>");  // gold filled star
            } else {
                sb.append("<font color=\'#CCCCCC\'>\u2605</font>");  // grey empty star
            }
        }
        sb.append("</center></html>");
        return sb.toString();
    }

    private Color getMessageColor(int score) {
        if (score >= 80) return new Color(34, 139, 87);
        if (score >= 60) return new Color(0, 150, 136);
        if (score >= 40) return new Color(255, 152, 0);
        if (score >= 20) return new Color(244, 81, 30);
        return new Color(183, 28, 28);
    }

    // ── ScoreStorage Interface Implementation ─────────────────

    /**
     * Save score — auto-generates current date/time.
     * OVERLOADED METHOD 1 (satisfies ScoreStorage interface)
     */
    @Override
    public void saveScore(String username, int score) {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        saveScore(username, score, date);   // calls overloaded version below
    }

    /**
     * Save score with a given date string.
     * OVERLOADED METHOD 2 — allows specifying date (e.g. for testing)
     */
    @Override
    public void saveScore(String username, int score, String date) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(SCORE_FILE, true))) {       // true = append
            writer.write(username + "," + score + "," + date);
            writer.newLine();
        } catch (FileNotFoundException e) {
            // FILE I/O EXCEPTION HANDLING
            JOptionPane.showMessageDialog(null,
                "Score file not found:\n" + e.getMessage(),
                "File Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            // FILE I/O EXCEPTION HANDLING
            JOptionPane.showMessageDialog(null,
                "Could not save score:\n" + e.getMessage(),
                "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load the highest score ever recorded for a given username.
     * Returns -1 if username not found.
     */
    @Override
    public int loadScore(String username) {
        int highest = -1;
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equalsIgnoreCase(username)) {
                    int s = Integer.parseInt(parts[1].trim());
                    if (s > highest) highest = s;
                }
            }
        } catch (FileNotFoundException e) {
            // No scores saved yet — not an error
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                "Error reading scores:\n" + e.getMessage(),
                "IO Error", JOptionPane.ERROR_MESSAGE);
        }
        return highest;
    }

    /**
     * Read all scores from file, sort by score descending,
     * and display in the leaderboard text area.
     */
    @Override
    public void displayLeaderboard() {
        List<String[]> entries = readAllScores();
        entries.sort((a, b) -> Integer.parseInt(b[1].trim()) - Integer.parseInt(a[1].trim()));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-4s %-15s %-8s %-16s%n", "Rank", "Name", "Score", "Date"));
        sb.append("  " + "-".repeat(46) + "\n");

        if (entries.isEmpty()) {
            sb.append("  No scores recorded yet.");
        } else {
            int rank = 1;
            for (String[] e : entries) {
                String date = (e.length >= 3) ? e[2].trim() : "N/A";
                sb.append(String.format("  %-4d %-15s %-8s %-16s%n",
                    rank++, e[0].trim(), e[1].trim() + "%", date));
            }
        }

        if (leaderboardArea != null) leaderboardArea.setText(sb.toString());
    }

    /**
     * Overwrite the scores file with empty content (clear all scores).
     */
    @Override
    public void clearScores() {
        try (FileWriter fw = new FileWriter(SCORE_FILE, false)) {
            // Opening with false overwrites = effectively clears the file
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error clearing scores:\n" + e.getMessage(),
                "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helper Methods ────────────────────────────────────────

    /** Read all lines from scores.txt into a list of String arrays. */
    private List<String[]> readAllScores() {
        List<String[]> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) list.add(line.split(","));
            }
        } catch (FileNotFoundException e) {
            // No file yet
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Error loading leaderboard:\n" + e.getMessage(),
                "IO Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    /** Helper: create a centred JLabel with given font and colour. */
    private JLabel makeCenter(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /** Helper: create a styled button matching the app theme. */
    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Getters (for use by other members if needed) ──────────
    public String getUsername() { return username; }
    public int    getScore()    { return score;    }
    public int    getPoints()   { return calculatePoints(score); }
    public int    getStars()    { return calculateStars(score);  }
    public String getBadge()    { return determineBadge(score);  }
}