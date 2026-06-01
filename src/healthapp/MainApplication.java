package healthapp;
 
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;

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
        JLabel lblIcon = new JLabel("🌿", SwingConstants.CENTER);
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

    private void showLeaderboardOnly() {
        JPanel leaderPanel = new JPanel(new BorderLayout(10, 10));
        leaderPanel.setBackground(BG_COLOR);
        leaderPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("\u2605 Global Leaderboard \u2605", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 20));
        title.setForeground(PRIMARY_COLOR);
        leaderPanel.add(title, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        try (BufferedReader reader = new BufferedReader(new FileReader("../scores.txt"))) {
            StringBuilder sb = new StringBuilder(String.format("%-20s %s\n", "Name", "Score"));
            sb.append("----------------------------------\n");
            String line;
            boolean hasScores = false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    sb.append(String.format("%-20s %s%%\n", parts[0].trim(), parts[1].trim()));
                    hasScores = true;
                }
            }
            if (!hasScores) sb.append("\nNo scores recorded yet.");
            textArea.setText(sb.toString());
        } catch (Exception ex) {
            textArea.setText("\nNo scores recorded yet, or file not found.");
        }

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(new LineBorder(PRIMARY_COLOR, 1, true));
        leaderPanel.add(scroll, BorderLayout.CENTER);

        mainPanel.add(leaderPanel, PANEL_LEADER);
        topNavBar.setVisible(true);
        navigateTo(PANEL_LEADER);
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