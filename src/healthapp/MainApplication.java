package healthapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

// ============================================================
// Class       : MainApplication
// Creator     : Muhammad Izzat (105244)
// Tester      : Jocelyn (104561)
// OOP         : Inheritance, Interfaces, Encapsulation
// Description : Serves as the main GUI container for HealthApp.
//               Inherits from JFrame and implements UserInteraction
//               to manage navigation, screen rendering, and badge states.
// ============================================================

public class MainApplication extends JFrame implements UserInteraction {

    private static final Color BG_COLOR      = new Color(240, 248, 245);
    private static final Color PRIMARY_COLOR = new Color(34, 139, 87);

    private UserProfile currentUser;
    private JPanel      mainPanel;
    private JPanel      topNavBar;
    private CardLayout  cardLayout;

    // ── Process badge unlock state (tracked here so UserProfile needs no changes) ──
    private boolean badgeKnowledgeSeeker = false;   // 📖 unlocked when Learning Module done
    private boolean badgeFirstAttempt    = false;   // 🎯 unlocked when quiz first started
    private boolean badgePerfectScore    = false;   // ⭐ unlocked when score == total questions

    private static final String PANEL_SPLASH   = "SPLASH";
    private static final String PANEL_WELCOME  = "WELCOME";
    private static final String PANEL_LEARNING = "LEARNING";
    private static final String PANEL_QUIZ     = "QUIZ";
    private static final String PANEL_LEADER   = "LEADERBOARD";
    private static final String PANEL_BADGES   = "BADGES";
    private static final String PANEL_SHUTDOWN = "SHUTDOWN";

    // Variable to hold the shutdown GIF in memory
    private Image shutdownImg;

    // Helper to locate resources robustly regardless of working directory/classpath layout
    private java.net.URL findResource(String name) {
        String[] candidates = new String[] {
            "/" + name,
            "/healthapp/" + name,
            name,
            "healthapp/" + name
        };
        for (String cand : candidates) {
            try {
                java.net.URL u = getClass().getResource(cand);
                if (u != null) return u;
            } catch (Exception ignored) {}
            try {
                String clPath = cand.startsWith("/") ? cand.substring(1) : cand;
                java.net.URL u2 = Thread.currentThread().getContextClassLoader().getResource(clPath);
                if (u2 != null) return u2;
            } catch (Exception ignored) {}
        }
        return null;
    }

    public MainApplication() {
        setTitle("HealthApp  \u2014  SDG 3: Good Health & Well-being");
        setSize(420, 750);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Pre-load the shutdown GIF in a background thread so it doesn't freeze the app
        new Thread(() -> {
            java.net.URL shutURL = findResource("shutdown.gif");
            if (shutURL != null) {
                shutdownImg = new ImageIcon(shutURL).getImage();
            } else {
                System.err.println("Note: Could not find shutdown.gif. Showing solid color on exit.");
            }
        }).start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { exitApplication(); }
        });

        buildTopNavBar();

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        
        mainPanel.add(buildSplashPanel(), PANEL_SPLASH);
        mainPanel.add(buildWelcomePanel(), PANEL_WELCOME);
        mainPanel.add(buildShutdownPanel(), PANEL_SHUTDOWN);

        add(topNavBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        showWelcomeScreen();
    }

    // ── Badge flag setters (called by QuizManager via window ancestor) ───────────
    public void unlockFirstAttemptBadge()  { badgeFirstAttempt = true; }
    public void unlockPerfectScoreBadge()  { badgePerfectScore = true; }

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

    // --- SPLASH SCREEN WITH AUTO-SCALING GIF BACKGROUND ---
    private JPanel buildSplashPanel() {
        java.net.URL imgURL = findResource("FirstBackground.gif");
        Image bgImage = null;
        if (imgURL != null) {
            bgImage = new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Note: Could not find FirstBackground.gif. Showing solid color.");
        }

        final Image finalBgImage = bgImage;

        // Custom JPanel that stretches the background image to fit
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (finalBgImage != null) {
                    g.drawImage(finalBgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    setBackground(PRIMARY_COLOR);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        backgroundPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel lblIcon = new JLabel("🌿", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblIcon.setPreferredSize(new Dimension(120, 110));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        gbc.gridy = 0;
        backgroundPanel.add(lblIcon, gbc);

        JLabel lblTitle = new JLabel("HealthApp");
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 38));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 1;
        backgroundPanel.add(lblTitle, gbc);

        JLabel lblWelcome = new JLabel("Welcome");
        lblWelcome.setFont(new Font("Dialog", Font.ITALIC, 24));
        lblWelcome.setForeground(new Color(220, 245, 230));
        gbc.gridy = 2;
        backgroundPanel.add(lblWelcome, gbc);

        JLabel lblPrompt = new JLabel("\u25B6 Click anywhere to start \u25C0");
        lblPrompt.setFont(new Font("Dialog", Font.BOLD, 14));
        lblPrompt.setForeground(new Color(255, 215, 0));
        gbc.gridy = 3;
        gbc.insets = new Insets(80, 10, 10, 10);
        backgroundPanel.add(lblPrompt, gbc);

        // Click to proceed to Welcome Screen
        MouseAdapter clickToStart = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                navigateTo(PANEL_WELCOME);
            }
        };

        backgroundPanel.addMouseListener(clickToStart);
        lblIcon.addMouseListener(clickToStart);
        lblTitle.addMouseListener(clickToStart);
        lblWelcome.addMouseListener(clickToStart);
        lblPrompt.addMouseListener(clickToStart);

        return backgroundPanel;
    }

    // --- SHUTDOWN SCREEN WITH AUTO-SCALING GIF BACKGROUND ---
    private JPanel buildShutdownPanel() {
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (shutdownImg != null) {
                    g.drawImage(shutdownImg, 0, 0, getWidth(), getHeight(), this);
                } else {
                    setBackground(PRIMARY_COLOR);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel lblIcon = new JLabel("🌿", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblIcon.setPreferredSize(new Dimension(120, 110));
        lblIcon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        gbc.gridy = 0;
        backgroundPanel.add(lblIcon, gbc);

        JLabel lblTitle = new JLabel("HealthApp");
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 38));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 1;
        backgroundPanel.add(lblTitle, gbc);

        JLabel lblGoodbye = new JLabel("Goodbye...");
        lblGoodbye.setFont(new Font("Dialog", Font.ITALIC, 24));
        lblGoodbye.setForeground(new Color(220, 245, 230));
        gbc.gridy = 2;
        backgroundPanel.add(lblGoodbye, gbc);

        return backgroundPanel;
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
        JButton btnLeader = styleButton("\u2605 Leaderboard", new Color(230, 138, 0), Color.WHITE);
        // \uD83C\uDF96 = 🎖  My Badges button — purple theme
        JButton btnBadges = styleButton("\uD83C\uDF96 My Badges", new Color(100, 60, 180), Color.WHITE);
        JButton btnExit   = styleButton("\u2716 Exit",        new Color(204, 51, 51), Color.WHITE);

        JPanel menuGrid = new JPanel(new GridLayout(5, 1, 0, 10));
        menuGrid.setBackground(BG_COLOR);
        menuGrid.add(btnLearn);
        menuGrid.add(btnQuiz);
        menuGrid.add(btnLeader);
        menuGrid.add(btnBadges);
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
        btnBadges.addActionListener(e -> showBadgesPanel(txtName.getText()));
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
            badgeKnowledgeSeeker = true;
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

    // ── Badges panel ──────────────────────────────────────────

    /**
     * Build and show the "My Badges" screen.
     * Reads unlock state from UserProfile (or treats all as locked if no name entered).
     * Triggered by the "\uD83C\uDF96 My Badges" button on the main menu.
     */
    private void showBadgesPanel(String nameInput) {
        // Determine which badges are unlocked from local tracking fields
        boolean hasKnowledge  = badgeKnowledgeSeeker;
        boolean hasFirstTry   = badgeFirstAttempt;
        boolean hasPerfect    = badgePerfectScore;
        int unlockedCount     = (hasKnowledge ? 1 : 0) + (hasFirstTry ? 1 : 0) + (hasPerfect ? 1 : 0);

        // ── Wrapper panel ─────────────────────────────────────
        JPanel badgesWrapper = new JPanel(new BorderLayout(0, 0));
        badgesWrapper.setBackground(BG_COLOR);

        // ── Header bar ────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 100, 60));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitle = new JLabel("SDG 3: Good Health & Well-being  |  My Badges");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(Color.WHITE);

        // \uD83C\uDF96 = 🎖
        JLabel lblCount = new JLabel(unlockedCount + " / 3 unlocked");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCount.setForeground(new Color(184, 255, 218));

        header.add(lblTitle, BorderLayout.WEST);
        header.add(lblCount, BorderLayout.EAST);
        badgesWrapper.add(header, BorderLayout.NORTH);

        // ── Content area ──────────────────────────────────────
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));

        JLabel lblSub = new JLabel("Complete activities to unlock all badges", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSub.setForeground(new Color(130, 140, 135));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(lblSub);
        content.add(Box.createVerticalStrut(14));

        // Badge rows: icon, name, description, unlocked?, accent colour
        Object[][] badges = {
            // \uD83D\uDCD6 = 📖
            { "\uD83D\uDCD6", "Knowledge Seeker", "Complete the Learning Module",
              hasKnowledge, new Color(34, 139, 87), new Color(240, 250, 244), new Color(163, 217, 184) },
            // \uD83C\uDFAF = 🎯
            { "\uD83C\uDFAF", "First Attempt",    "Start the quiz for the first time",
              hasFirstTry,  new Color(24, 95, 165), new Color(234, 243, 251), new Color(163, 196, 232) },
            // \u2B50 = ⭐
            { "\u2B50",       "Perfect Score",     "Answer all questions correctly",
              hasPerfect,   new Color(176, 122, 0), new Color(250, 245, 220), new Color(230, 190, 100) }
        };

        for (Object[] b : badges) {
            String  icon      = (String)  b[0];
            String  badgeName = (String)  b[1];
            String  desc      = (String)  b[2];
            boolean unlocked  = (Boolean) b[3];
            Color   accent    = (Color)   b[4];
            Color   rowBg     = unlocked ? (Color) b[5] : new Color(245, 245, 245);
            Color   rowBorder = unlocked ? (Color) b[6] : new Color(210, 210, 210);

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(rowBg);
            row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(rowBorder, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

            // Icon
            JLabel lblIcon = new JLabel(icon);
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, unlocked ? 30 : 28));
            if (!unlocked) lblIcon.setForeground(new Color(180, 180, 180));
            row.add(lblIcon, BorderLayout.WEST);

            // Name + description
            JPanel textCol = new JPanel();
            textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
            textCol.setBackground(rowBg);

            JLabel lblName = new JLabel(badgeName);
            lblName.setFont(new Font("Dialog", Font.BOLD, 13));
            lblName.setForeground(unlocked ? new Color(accent.getRed() / 2, accent.getGreen() / 2, accent.getBlue() / 2)
                                           : new Color(140, 140, 140));

            JLabel lblDesc = new JLabel(desc);
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblDesc.setForeground(unlocked ? accent : new Color(170, 170, 170));

            textCol.add(lblName);
            textCol.add(Box.createVerticalStrut(2));
            textCol.add(lblDesc);
            row.add(textCol, BorderLayout.CENTER);

            // Status pill
            JLabel lblStatus = new JLabel(unlocked ? "Unlocked" : "Locked");
            lblStatus.setFont(new Font("Dialog", Font.BOLD, 10));
            lblStatus.setOpaque(true);
            lblStatus.setBackground(unlocked ? accent : new Color(200, 200, 200));
            lblStatus.setForeground(unlocked ? Color.WHITE : new Color(100, 100, 100));
            lblStatus.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            row.add(lblStatus, BorderLayout.EAST);

            content.add(row);
            content.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 230, 210), 1, true));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setBackground(BG_COLOR);
        centerWrap.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerWrap.add(scroll, BorderLayout.CENTER);

        badgesWrapper.add(centerWrap, BorderLayout.CENTER);

        mainPanel.add(badgesWrapper, PANEL_BADGES);
        topNavBar.setVisible(true);
        navigateTo(PANEL_BADGES);
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
        topNavBar.setVisible(false);
        cardLayout.show(mainPanel, PANEL_SPLASH);
    }

    @Override
    public void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?", "Exit",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            topNavBar.setVisible(false);
            cardLayout.show(mainPanel, PANEL_SHUTDOWN);
            
            Timer shutdownTimer = new Timer(3000, e -> System.exit(0));
            shutdownTimer.setRepeats(false);
            shutdownTimer.start();
        }
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