package healthapp;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
public class MainApplication extends JFrame implements UserInteraction {
 
    private static final Color BG_COLOR      = new Color(240, 248, 245);
    private static final Color PRIMARY_COLOR = new Color(34, 139, 87);
 
    private UserProfile currentUser;
    private JPanel      mainPanel;
    private CardLayout  cardLayout;
 
    private static final String PANEL_WELCOME  = "WELCOME";
    private static final String PANEL_LEARNING = "LEARNING";
    private static final String PANEL_QUIZ     = "QUIZ";
 
    public MainApplication() {
        setTitle("HealthApp  —  SDG 3: Good Health & Well-being");
        setSize(420, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
 
        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.add(buildWelcomePanel(), PANEL_WELCOME);
 
        add(mainPanel);
        showWelcomeScreen();
    }
 
    // build the welcome/login screen
    private JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 30, 12, 30);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.gridx  = 0;
 
        // app icon
        JLabel lblIcon = new JLabel("🌿", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        lblIcon.setPreferredSize(new Dimension(100, 90));  // ← FIX: give enough height
        lblIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // ← FIX: push down
        gbc.gridy = 0;
        panel.add(lblIcon, gbc);
 
        // app title
        JLabel lblTitle = new JLabel(
            "<html><center><b>HealthApp</b><br/>"
            + "<span style='font-size:11px;'>SDG 3: Good Health & Well-being</span></center></html>",
            SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(PRIMARY_COLOR);
        gbc.gridy = 1;
        panel.add(lblTitle, gbc);
 
        // subtitle
        JLabel lblSub = new JLabel(
            "<html><center>Learn  •  Quiz  •  Earn Rewards</center></html>",
            SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSub.setForeground(Color.GRAY);
        gbc.gridy = 2;
        panel.add(lblSub, gbc);
 
        // name prompt
        JLabel lblPrompt = new JLabel("Enter your name to begin:");
        lblPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 3;
        panel.add(lblPrompt, gbc);
 
        // name input field
        JTextField txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        gbc.gridy = 4;
        panel.add(txtName, gbc);
 
        // start button
        JButton btnStart = new JButton("  Start Learning  ");
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnStart.setBackground(PRIMARY_COLOR);
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        btnStart.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnStart.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        panel.add(btnStart, gbc);
 
        // action: validate name then launch learning module
        ActionListener startAction = e -> {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter your name to continue!",
                    "Name Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            currentUser = new UserProfile(name);
            
            LearningModule learningPanel = new LearningModule(currentUser, () -> navigateTo(PANEL_QUIZ));
            QuizManager quizPanel = new QuizManager(currentUser);

            mainPanel.add(learningPanel, PANEL_LEARNING);
            mainPanel.add(quizPanel, PANEL_QUIZ);

            navigateTo(PANEL_LEARNING);
        };
 
        btnStart.addActionListener(startAction);
        txtName.addActionListener(startAction); // press Enter also works
 
        return panel;
    }
 
    // --- UserInteraction interface methods ---
 
    @Override
    public void showMainMenu() {
        cardLayout.show(mainPanel, PANEL_WELCOME);
    }
 
    @Override
    public void navigateTo(String module) {
        cardLayout.show(mainPanel, module);
    }
 
    @Override
    public void showWelcomeScreen() {
        cardLayout.show(mainPanel, PANEL_WELCOME);
    }
 
    @Override
    public void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?", "Exit",
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) System.exit(0);
    }
 
    // main entry point
    public static void main(String[] args) {
 
        // global exception handler
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            JOptionPane.showMessageDialog(null,
                "An unexpected error occurred:\n" + throwable.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            throwable.printStackTrace();
        });
 
        // run on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // use default look and feel
            }
            new MainApplication().setVisible(true);
        });
    }
}