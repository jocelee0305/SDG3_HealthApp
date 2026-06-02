package healthapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

// Class      : MainApplication
// Creator    : Member 4
// Tester     : Member 3

public class MainApplication extends JFrame implements UserInteraction {

    private static final Color BG_COLOR      = new Color(240, 248, 245);
    private static final Color PRIMARY_COLOR = new Color(34, 139, 87);

    private UserProfile currentUser;
    private JPanel      mainPanel;
    private JPanel      topNavBar;
    private CardLayout  cardLayout;

    private static final String PANEL_WELCOME  = "WELCOME";
    private static final String PANEL_LEARNING = "LEARNING";
    private static final String PANEL_QUIZ     = "QUIZ";
    private static final String PANEL_LEADER   = "LEADERBOARD";

    public MainApplication() {
        setTitle("HealthApp  \u2014  SDG 3: Good Health & Well-being");
        setSize(420, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exitApplication(); }
        });

        buildTopNavBar();

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.add(buildWelcomePanel(), PANEL_WELCOME);

        add(topNavBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        showWelcomeScreen();
    }

    private void buildTopNavBar() {
        topNavBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topNavBar.setBackground(new Color(20, 100, 60));

        JButton btnHome = new JButton("\u2302 Main Menu");
        btnHome.setFont(new Font("Dialog", Font.BOLD, 12));
        btnHome.setBackground(new Color(240, 248, 245));
        btnHome.setForeground(new Color(40, 50, 45));
        btnHome.setFocusPainted(false);
        btnHome.setOpaque(true);
        btnHome.setContentAreaFilled(true);
        btnHome.setBorderPainted(false);
        btnHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnHome.addActionListener(e -> showMainMenu());

        topNavBar.add(btnHome);
        topNavBar.setVisible(false);
    }

    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 30, 12, 30);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0;

        // app icon - original leaf
        JLabel lblIcon = new JLabel("\uD83C\uDF3F", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcon.setPreferredSize(new Dimension(100, 90));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        gbc.gridy = 0;
        panel.add(lblIcon, gbc);

        JLabel lblTitle = new JLabel(
            "<html><center><b>HealthApp</b><br/>"
            + "<span style='font-size:11px;'>SDG 3: Good Health & Well-being</span></center></html>",
            SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);
        gbc.gridy = 1;
        panel.add(lblTitle, gbc);

        // Star and bullet - safe Unicode
        JLabel lblSub = new JLabel(
            "<html><center>\u2605 Learn  \u2022  \u2605 Quiz  \u2022  \u2605 Earn Rewards</center></html>",
            SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSub.setForeground(Color.GRAY);
        gbc.gridy = 2;
        panel.add(lblSub, gbc);

        // Right triangle - safe Unicode
        JLabel lblPrompt = new JLabel("\u25B6  Enter your name to begin:");
        lblPrompt.setFont(new Font("Dialog", Font.PLAIN, 13));
        lblPrompt.setForeground(PRIMARY_COLOR);
        gbc.gridy = 3;
        panel.add(lblPrompt, gbc);

        JTextField txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        gbc.gridy = 4;
        panel.add(txtName, gbc);

        // Buttons with safe Unicode icons only - NO emoji
        JButton btnLearn  = styleButton("\u2139 Read Info",   PRIMARY_COLOR,          Color.WHITE);
        JButton btnQuiz   = styleButton("\u270E Take Quiz",   new Color(0, 102, 204), Color.WHITE);
        // \uD83C\uDFC6 = 🏆  (emoji font renders this correctly via HTML/Segoe UI Emoji)
        JButton btnLeader = styleButton("\u2605 Leaderboard", new Color(230, 138, 0), Color.WHITE);
        JButton btnExit   = styleButton("\u2716 Exit",        new Color(204, 51, 51), Color.WHITE);

        JPanel menuGrid = new JPanel(new GridLayout(4, 1, 0, 10));
        menuGrid.setBackground(BG_COLOR);
        menuGrid.add(btnLearn);
        menuGrid.add(btnQuiz);
        menuGrid.add(btnLeader);
        menuGrid.add(btnExit);

        gbc.gridy = 5;
        panel.add(menuGrid, gbc);

        btnLearn.addActionListener(e -> {
            if (validateUser(txtName.getText(), this)) {
                setupModules();
                topNavBar.setVisible(true);
                navigateTo(PANEL_LEARNING);
            }
        });

        btnQuiz.addActionListener(e -> {
            if (validateUser(txtName.getText(), this)) {
                if (!currentUser.isLearningCompleted()) {
                    int ans = JOptionPane.showConfirmDialog(this,
                        "You haven't finished reading the info yet! Are you sure you want to skip to the quiz?",
                        "Skip Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (ans != JOptionPane.YES_OPTION) return;
                }
                setupModules();
                topNavBar.setVisible(true);
                navigateTo(PANEL_QUIZ);
            }
        });

        // Leaderboard button now delegates entirely to RewardSystem.displayLeaderboard()
        // to avoid duplicate file-reading logic
        btnLeader.addActionListener(e -> showLeaderboardOnly());
        btnExit.addActionListener(e -> exitApplication());

        return panel;
    }

    private boolean validateUser(String name, JFrame parent) {
        if (name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                "Please enter your name to track your progress!",
                "Name Required", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (currentUser == null || !currentUser.getUsername().equals(name.trim())) {
            currentUser = new UserProfile(name.trim());
        }
        return true;
    }

    private void setupModules() {
        LearningModule learningPanel = new LearningModule(currentUser, () -> {
            // \uD83D\uDCD6 Knowledge Seeker badge — fires when user finishes the Learning Module
            BadgePopup.show(this, BadgePopup.KNOWLEDGE_SEEKER);

            int ans = JOptionPane.showConfirmDialog(this,
                "You have finished reading! Would you like to start the quiz now?",
                "Start Quiz", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ans == JOptionPane.YES_OPTION) {
                navigateTo(PANEL_QUIZ);
            } else {
                showMainMenu();
            }
        });
        QuizManager quizPanel = new QuizManager(currentUser);
        mainPanel.add(learningPanel, PANEL_LEARNING);
        mainPanel.add(quizPanel, PANEL_QUIZ);
    }

    /**
     * Show the leaderboard using a RewardSystem instance so all leaderboard
     * logic lives in one place (eliminates duplicate file-reading code).
     * A dummy RewardSystem with score=0 is created just to call displayLeaderboard()
     * — no score is saved because we use a placeholder UserProfile that won't
     * match any real session, and we call clearScores() guard is NOT triggered.
     *
     * NOTE: We build a lightweight read-only leaderboard panel here rather than
     * constructing a full RewardSystem (which would save a 0% score to the file).
     */
    private void showLeaderboardOnly() {
        // Build a read-only leaderboard panel that reuses RewardSystem's display logic
        // without triggering saveScore. We embed a RewardSystem as a view-only panel
        // and override the layout to hide the result card, showing only the leaderboard.
        JPanel leaderWrapper = new JPanel(new BorderLayout(10, 10));
        leaderWrapper.setBackground(BG_COLOR);
        leaderWrapper.setBorder(new EmptyBorder(20, 15, 15, 15));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        // \u2605 = ★  (safe BMP character, no surrogate needed)
        JLabel title = new JLabel("\u2605 Global Leaderboard \u2605", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);
        leaderWrapper.add(header, BorderLayout.NORTH);

        // Leaderboard rows — reuse the same row-building logic via a temporary RewardSystem.
        // We pass a dummy UserProfile that is NOT saved (the PANEL_LEADER view is read-only).
        // To avoid writing 0% to the file, we construct the panel then immediately discard it
        // after calling displayLeaderboard(). The leaderboardPanel is extracted and embedded here.
        //
        // Simpler approach: duplicate just the read + render portion inline.
        // This keeps MainApplication self-contained and avoids cross-class coupling.

        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        rowsPanel.setBackground(new Color(255, 255, 240));

        // Column header
        rowsPanel.add(makeLeaderRow("Rank", "Name", "Score", "Date",
            new Color(34, 139, 87), Color.WHITE, Font.BOLD));

        // ── Deduplicate: keep only each user's highest score ──────────────────
        java.util.List<String[]> raw = readAllScoresStatic();
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
        java.util.List<String[]> entries = new java.util.ArrayList<>(best.values());
        entries.sort((a, b) -> Integer.parseInt(b[1].trim()) - Integer.parseInt(a[1].trim()));

        // Current username for highlight (null-safe: may be null if no login yet)
        String currentName = (currentUser != null) ? currentUser.getUsername() : "";

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("  No scores recorded yet.", SwingConstants.LEFT);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            empty.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            rowsPanel.add(empty);
        } else {
            int rank = 1;
            for (String[] e : entries) {
                String  date   = (e.length >= 3) ? e[2].trim() : "N/A";
                boolean isMe   = !currentName.isEmpty() && e[0].trim().equalsIgnoreCase(currentName);
                String  medal  = getRankMedalStatic(rank);
                // \u25C6 = ◆  marks the current user
                String  name   = medal + (medal.isEmpty() ? "" : " ") + e[0].trim()
                               + (isMe ? "  \u25C6 You" : "");
                Color   rowBg  = isMe ? new Color(197, 232, 255) : getLeaderRowBgStatic(rank);
                Color   rowFg  = isMe ? new Color(10, 60, 120)   : getLeaderRowFgStatic(rank);
                int     style  = isMe ? Font.BOLD : Font.PLAIN;
                rowsPanel.add(makeLeaderRow(String.valueOf(rank), name,
                    e[1].trim() + "%", date, rowBg, rowFg, style));
                rank++;
            }
        }

        JScrollPane scroll = new JScrollPane(rowsPanel);
        scroll.setBorder(new LineBorder(PRIMARY_COLOR, 1, true));
        leaderWrapper.add(scroll, BorderLayout.CENTER);

        mainPanel.add(leaderWrapper, PANEL_LEADER);
        topNavBar.setVisible(true);
        navigateTo(PANEL_LEADER);
    }

    // ── Static helpers used by showLeaderboardOnly() ──────────
    // These mirror the logic in RewardSystem to keep leaderboard rendering consistent.

    private java.util.List<String[]> readAllScoresStatic() {
        java.util.List<String[]> list = new java.util.ArrayList<>();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.FileReader("../scores.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) list.add(line.split(","));
            }
        } catch (java.io.FileNotFoundException e) {
            // No file yet — fine
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading leaderboard:\n" + e.getMessage(),
                "IO Error", JOptionPane.ERROR_MESSAGE);
        }
        return list;
    }

    private String getRankMedalStatic(int rank) {
        // \uD83E\uDD47 = 🥇  \uD83E\uDD48 = 🥈  \uD83E\uDD49 = 🥉
        switch (rank) {
            case 1: return "\uD83E\uDD47";
            case 2: return "\uD83E\uDD48";
            case 3: return "\uD83E\uDD49";
            default: return "";
        }
    }

    private Color getLeaderRowBgStatic(int rank) {
        switch (rank) {
            case 1: return new Color(255, 248, 200);
            case 2: return new Color(235, 235, 240);
            case 3: return new Color(250, 230, 205);
            default: return (rank % 2 == 0) ? new Color(248, 253, 250) : Color.WHITE;
        }
    }

    private Color getLeaderRowFgStatic(int rank) {
        switch (rank) {
            case 1: return new Color(130, 90,  10);
            case 2: return new Color(80,  80, 100);
            case 3: return new Color(120, 70,  20);
            default: return new Color(30, 40, 35);
        }
    }

    /** Build a single leaderboard row (4-column GridLayout). */
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

    private JButton styleButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Dialog", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(12, 10, 12, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    @Override
    public void showMainMenu() {
        topNavBar.setVisible(false);
        cardLayout.show(mainPanel, PANEL_WELCOME);
    }

    @Override
    public void navigateTo(String module) {
        cardLayout.show(mainPanel, module);
    }

    @Override
    public void showWelcomeScreen() {
        showMainMenu();
    }

    @Override
    public void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?", "Exit",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) System.exit(0);
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            JOptionPane.showMessageDialog(null,
                "An unexpected error occurred:\n" + throwable.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            throwable.printStackTrace();
        });
        SwingUtilities.invokeLater(() -> new MainApplication().setVisible(true));
    }
}