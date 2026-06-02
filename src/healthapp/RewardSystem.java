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
    private int    score;        // percentage score 0-100
    private int    rawScore;     // e.g. 15 out of 20
    private int    totalQ;       // total questions
    private int    timeBonus;    // remaining timer seconds passed from QuizManager

    // ── GUI components ────────────────────────────────────────
    private JLabel    lblBadge;
    private JLabel    lblStars;
    private JLabel    lblPoints;
    private JLabel    lblMessage;
    private JPanel    leaderboardPanel;  // upgraded: custom JPanel rows instead of JTextArea

    // ── Constructor (called by QuizManager) — version without timeBonus ──
    // METHOD OVERLOADING — version 1
    public RewardSystem(UserProfile userProfile, int rawScore, int totalQ) {
        this(userProfile, rawScore, totalQ, 0);
    }

    // ── Constructor with timeBonus ────────────────────────────
    // METHOD OVERLOADING — version 2
    public RewardSystem(UserProfile userProfile, int rawScore, int totalQ, int timeBonus) {
        this.username  = userProfile.getUsername();
        this.rawScore  = rawScore;
        this.totalQ    = totalQ;
        this.timeBonus = timeBonus;
        this.score     = (totalQ > 0) ? (int) Math.round((double) rawScore / totalQ * 100) : 0;

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
            new LineBorder(getBadgeBorderColor(score), 3, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Score label
        JLabel lblScore = makeCenter(
            "Score: " + rawScore + " / " + totalQ + "  (" + score + "%)",
            new Font("Segoe UI", Font.BOLD, 22), PRIMARY_COLOR);

        // Badge panel — coloured background tile to visually distinguish tiers
        JPanel badgePanel = buildBadgePanel(score);

        // Stars — enlarged for visual impact (font size 38)
        lblStars = makeCenter(getStarHTML(calculateStars(score)),
            new Font("Segoe UI", Font.PLAIN, 38), ACCENT_COLOR);

        // Points — show base points + time bonus separately if timeBonus > 0
        int basePoints  = calculatePoints(score);
        int totalPoints = calculatePoints(score, timeBonus);
        String pointsText;
        if (timeBonus > 0) {
            // \uD83D\uDCB0 = 💰  \u23F1 = ⏱
            pointsText = "\uD83D\uDCB0 Points: " + basePoints
                       + "  +  \u23F1 Time Bonus: +" + (timeBonus * 2)
                       + "  =  " + totalPoints + " pts";
        } else {
            pointsText = "\uD83D\uDCB0 Points Earned: " + basePoints + " pts";
        }
        lblPoints = makeCenter(pointsText,
            new Font("Segoe UI Emoji", Font.BOLD, 13), new Color(60, 100, 70));

        // Personal best comparison
        JLabel lblPersonalBest = buildPersonalBestLabel();

        // Motivational message
        lblMessage = makeCenter(getMotivationalMessage(score),
            new Font("Segoe UI", Font.ITALIC, 16), getMessageColor(score));

        // User greeting — \uD83C\uDF89 = 🎉
        JLabel lblUser = makeCenter("Well done, " + username + "! \uD83C\uDF89",
            new Font("Segoe UI Emoji", Font.PLAIN, 11), new Color(120, 160, 130));

        card.add(Box.createVerticalStrut(8));
        card.add(lblScore);
        card.add(Box.createVerticalStrut(10));
        card.add(badgePanel);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStars);
        card.add(Box.createVerticalStrut(8));
        card.add(lblPoints);
        card.add(Box.createVerticalStrut(6));
        card.add(lblPersonalBest);
        card.add(Box.createVerticalStrut(12));
        card.add(new JSeparator());
        card.add(Box.createVerticalStrut(12));
        card.add(lblMessage);
        card.add(Box.createVerticalStrut(6));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(8));

        // Animate points label counting up from 0 to totalPoints
        animatePoints(lblPoints, basePoints, totalPoints, timeBonus);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_COLOR);
        wrap.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        wrap.add(card, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);
    }

    // ── Build a coloured badge tile matching the score tier ───
    private JPanel buildBadgePanel(int score) {
        JPanel tile = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        tile.setBackground(getBadgeBgColor(score));
        tile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getBadgeBorderColor(score), 2, true),
            BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));
        tile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        tile.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Badge icon — uses Segoe UI Emoji font for correct emoji rendering
        JLabel iconLbl = new JLabel(getBadgeIcon(score));
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        // Badge text — separate label with Dialog font for the text part
        JLabel textLbl = new JLabel(getBadgeText(score));
        textLbl.setFont(new Font("Dialog", Font.BOLD, 14));
        textLbl.setForeground(getBadgeFgColor(score));

        tile.add(iconLbl);
        tile.add(textLbl);
        return tile;
    }

    // ── Personal best label ───────────────────────────────────
    private JLabel buildPersonalBestLabel() {
        // loadScore reads historical max BEFORE this attempt was saved,
        // but since we already called saveScore in the constructor the file
        // now contains this run — so we grab the max and compare.
        int prevBest = loadScore(username);   // returns highest (including current)
        String pbText;
        if (prevBest == score) {
            // \uD83C\uDF1F = 🌟
            pbText = "\uD83C\uDF1F New Personal Best: " + score + "%!";
        } else {
            // \uD83D\uDCC8 = 📈
            pbText = "\uD83D\uDCC8 Your Best: " + prevBest + "%  |  This attempt: " + score + "%";
        }
        JLabel lbl = makeCenter(pbText,
            new Font("Segoe UI Emoji", Font.PLAIN, 12), new Color(80, 120, 100));
        return lbl;
    }

    // ── Animate points label counting from 0 to target ────────
    private void animatePoints(JLabel label, int basePoints, int totalPoints, int timeBonusSec) {
        final int[] current = {0};
        final int step = Math.max(1, totalPoints / 30);   // 30 ticks to reach total

        Timer timer = new Timer(40, null);
        timer.addActionListener(e -> {
            current[0] = Math.min(current[0] + step, totalPoints);

            if (timeBonusSec > 0) {
                int displayBase  = Math.min(current[0], basePoints);
                int displayBonus = Math.max(0, current[0] - basePoints);
                label.setText("\uD83D\uDCB0 Points: " + displayBase
                    + "  +  \u23F1 Time Bonus: +" + (displayBonus * 1)
                    + "  =  " + current[0] + " pts");
            } else {
                label.setText("\uD83D\uDCB0 Points Earned: " + current[0] + " pts");
            }

            if (current[0] >= totalPoints) {
                ((Timer) ((ActionEvent) e).getSource()).stop();
                // Restore the final accurate text
                if (timeBonusSec > 0) {
                    label.setText("\uD83D\uDCB0 Points: " + basePoints
                        + "  +  \u23F1 Time Bonus: +" + (timeBonusSec * 2)
                        + "  =  " + totalPoints + " pts");
                } else {
                    label.setText("\uD83D\uDCB0 Points Earned: " + basePoints + " pts");
                }
            }
        });
        timer.start();
    }

    // ── Build leaderboard section at bottom ───────────────────
    private void buildLeaderboardPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(BG_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // \uD83C\uDFC6 = 🏆
        JLabel boardTitle = new JLabel("\uD83C\uDFC6 Leaderboard", SwingConstants.CENTER);
        boardTitle.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        boardTitle.setForeground(PRIMARY_COLOR);
        boardTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        bottomPanel.add(boardTitle, BorderLayout.NORTH);

        leaderboardPanel = new JPanel();
        leaderboardPanel.setLayout(new BoxLayout(leaderboardPanel, BoxLayout.Y_AXIS));
        leaderboardPanel.setBackground(new Color(255, 255, 240));

        JScrollPane scroll = new JScrollPane(leaderboardPanel);
        scroll.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true));
        scroll.setPreferredSize(new Dimension(0, 160));
        bottomPanel.add(scroll, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Auto-load leaderboard on open
        displayLeaderboard();
    }

    // ── Gamification Logic ────────────────────────────────────

    /** Assign badge text (no icon — icon is handled separately). */
    public String getBadgeText(int score) {
        if      (score >= 80) return "Gold Badge  —  Outstanding!";
        else if (score >= 60) return "Silver Badge  —  That's good!";
        else if (score >= 40) return "Bronze Badge  —  Good try!";
        else if (score >= 20) return "Iron Badge  —  You can do better!";
        else                  return "Participant Badge  —  Don't give up!";
    }

    /** Return only the badge emoji icon as a String. */
    public String getBadgeIcon(int score) {
        // \uD83C\uDFC6 = 🏆  \uD83E\uDD48 = 🥈  \uD83E\uDD49 = 🥉
        // \uD83C\uDF97 = 🎗  \uD83C\uDF96 = 🎖
        if      (score >= 80) return "\uD83C\uDFC6";
        else if (score >= 60) return "\uD83E\uDD48";
        else if (score >= 40) return "\uD83E\uDD49";
        else if (score >= 20) return "\uD83C\uDF97";
        else                  return "\uD83C\uDF96";
    }

    /**
     * Full badge string (icon + text) — kept for backward compatibility
     * with any existing callers (e.g. getBadge() getter).
     */
    public String determineBadge(int score) {
        return getBadgeIcon(score) + " " + getBadgeText(score);
    }

    /** Background colour of the badge tile, one per tier. */
    private Color getBadgeBgColor(int score) {
        if      (score >= 80) return new Color(255, 248, 220);   // light gold
        else if (score >= 60) return new Color(235, 235, 240);   // light silver
        else if (score >= 40) return new Color(250, 235, 210);   // light bronze
        else if (score >= 20) return new Color(230, 235, 245);   // light steel blue
        else                  return new Color(240, 240, 240);   // light grey
    }

    /** Border / accent colour matching the badge tier. */
    private Color getBadgeBorderColor(int score) {
        if      (score >= 80) return new Color(212, 175, 55);    // gold
        else if (score >= 60) return new Color(169, 169, 169);   // silver
        else if (score >= 40) return new Color(205, 127, 50);    // bronze
        else if (score >= 20) return new Color(100, 130, 180);   // steel
        else                  return new Color(180, 180, 180);   // grey
    }

    /** Text colour inside the badge tile. */
    private Color getBadgeFgColor(int score) {
        if      (score >= 80) return new Color(130, 90, 10);
        else if (score >= 60) return new Color(80, 80, 100);
        else if (score >= 40) return new Color(120, 70, 20);
        else if (score >= 20) return new Color(50, 70, 120);
        else                  return new Color(100, 100, 100);
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
        return (score * 10) + (timeBonus * 1);
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
                sb.append("<font color='#FFC107' size='6'>&#9733;</font>");   // gold star ★
            } else {
                sb.append("<font color='#CCCCCC' size='6'>&#9733;</font>");   // grey star ★
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
     * Read all scores from file, deduplicate per username (keep highest score only),
     * sort by score descending, and display in the leaderboard panel.
     * The current user's row is highlighted in light blue to help them spot their rank.
     */
    @Override
    public void displayLeaderboard() {
        List<String[]> raw = readAllScores();

        // ── Deduplication: keep only each user's highest-score entry ──────────
        // Key = username (case-insensitive), Value = String[] {name, score, date}
        java.util.Map<String, String[]> best = new java.util.LinkedHashMap<>();
        for (String[] parts : raw) {
            if (parts.length < 2) continue;
            String key = parts[0].trim().toLowerCase();
            int    s   = 0;
            try { s = Integer.parseInt(parts[1].trim()); } catch (NumberFormatException ex) { continue; }
            if (!best.containsKey(key) || s > Integer.parseInt(best.get(key)[1].trim())) {
                best.put(key, parts);
            }
        }
        List<String[]> entries = new ArrayList<>(best.values());
        entries.sort((a, b) -> Integer.parseInt(b[1].trim()) - Integer.parseInt(a[1].trim()));

        leaderboardPanel.removeAll();

        // Header row
        JPanel header = makeLeaderRow("Rank", "Name", "Score", "Date",
            new Color(34, 139, 87), Color.WHITE, Font.BOLD);
        leaderboardPanel.add(header);

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("  No scores recorded yet.", SwingConstants.LEFT);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            empty.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            leaderboardPanel.add(empty);
        } else {
            int rank = 1;
            for (String[] e : entries) {
                String  date    = (e.length >= 3) ? e[2].trim() : "N/A";
                boolean isMe    = e[0].trim().equalsIgnoreCase(username);
                String  medal   = getRankMedal(rank);
                // \u25C6 = ◆  used to mark the current user
                String  nameStr = medal + (medal.isEmpty() ? "" : " ") + e[0].trim()
                                + (isMe ? "  \u25C6 You" : "");

                // Current-user row overrides normal rank colour with a highlight
                Color rowBg = isMe ? new Color(197, 232, 255)   // light blue highlight
                                   : getLeaderRowBg(rank);
                Color rowFg = isMe ? new Color(10, 60, 120)     // dark blue text
                                   : getLeaderRowFg(rank);
                int   style = isMe ? Font.BOLD : Font.PLAIN;

                JPanel row = makeLeaderRow(
                    String.valueOf(rank), nameStr,
                    e[1].trim() + "%", date,
                    rowBg, rowFg, style);
                leaderboardPanel.add(row);
                rank++;
            }
        }

        leaderboardPanel.revalidate();
        leaderboardPanel.repaint();
    }

    /** Build a single leaderboard row as a JPanel with four labels. */
    private JPanel makeLeaderRow(String rank, String name, String score,
                                  String date, Color bg, Color fg, int fontStyle) {
        JPanel row = new JPanel(new GridLayout(1, 4, 4, 0));
        row.setBackground(bg);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        Font f = new Font("Segoe UI Emoji", fontStyle, 12);

        for (String text : new String[]{rank, name, score, date}) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(f);
            lbl.setForeground(fg);
            row.add(lbl);
        }
        return row;
    }

    /** Return medal emoji for ranks 1–3, empty string otherwise. */
    private String getRankMedal(int rank) {
        // \uD83E\uDD47 = 🥇  \uD83E\uDD48 = 🥈  \uD83E\uDD49 = 🥉
        switch (rank) {
            case 1: return "\uD83E\uDD47";
            case 2: return "\uD83E\uDD48";
            case 3: return "\uD83E\uDD49";
            default: return "";
        }
    }

    /** Background colour for leaderboard rows based on rank. */
    private Color getLeaderRowBg(int rank) {
        switch (rank) {
            case 1: return new Color(255, 248, 200);   // gold
            case 2: return new Color(235, 235, 240);   // silver
            case 3: return new Color(250, 230, 205);   // bronze
            default: return (rank % 2 == 0)
                        ? new Color(248, 253, 250)     // alternating light green
                        : Color.WHITE;
        }
    }

    /** Foreground colour for leaderboard rows based on rank. */
    private Color getLeaderRowFg(int rank) {
        switch (rank) {
            case 1: return new Color(130, 90, 10);
            case 2: return new Color(80,  80, 100);
            case 3: return new Color(120, 70,  20);
            default: return TEXT_COLOR;
        }
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
    public int    getPoints()   { return calculatePoints(score, timeBonus); }
    public int    getStars()    { return calculateStars(score);  }
    public String getBadge()    { return determineBadge(score);  }
}