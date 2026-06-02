package healthapp;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// ============================================================
// Class      : QuizManager
// Creator    : Lee Xing Ying (104731)
// Tester     : Member 3
// OOP        : Encapsulation, Polymorphism, Exception Handling, GUI
// Description: Manages 20 quiz questions, user interactions,
//              answer validation, score calculation, and
//              integration with RewardSystem.
// ============================================================

public class QuizManager extends JPanel {

    private static final Color BG_COLOR      = new Color(240, 248, 245);
    private static final Color PRIMARY_COLOR = new Color(34, 139, 87);
    private static final Color ACCENT_COLOR  = new Color(255, 193, 7);
    private static final Color TEXT_COLOR    = new Color(30, 40, 35);
    private static final Color CARD_BORDER   = new Color(200, 230, 210);
    private static final Font  TITLE_FONT    = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font  BODY_FONT     = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font  SMALL_FONT    = new Font("Segoe UI", Font.ITALIC, 11);
    private static final Font  BUTTON_FONT   = new Font("Segoe UI", Font.BOLD, 13);

    private UserProfile userProfile;
    private ArrayList<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    // ── Timer fields ──────────────────────────────────────────
    private static final int SECONDS_PER_QUESTION = 30;  // 每题时限
    private int   secondsLeft;                            // 当前题目剩余秒数
    private int   totalTimeBonusSeconds = 0;              // 累计所有题目的剩余秒数
    private Timer questionTimer;                          // javax.swing.Timer

    // GUI Components
    private JLabel       lblModuleTitle;
    private JLabel       lblPageCounter;
    private JLabel       lblTimer;                        // 倒计时 label
    private JProgressBar progressBar;

    private JPanel       cardPanel;

    // MCQ / True-False
    private JLabel       lblEmoji;
    private JLabel       lblQuestionType;
    private JTextArea    txtQuestion;
    private JRadioButton[] radioOptions;
    private ButtonGroup  buttonGroup;

    // Fill-in-the-blank
    private JTextField   txtFillBlank;

    // Navigation
    private JButton      btnNext;

    // ── Process badge tracking ────────────────────────────────
    private boolean firstAttemptShown = false;  // true once First Attempt badge has fired

    //Constructor
    public QuizManager(UserProfile userProfile) {
        this.userProfile = userProfile;
        questions        = new ArrayList<>();
        loadQuestions();
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buildChrome();
        buildCard();
        buildNav();
        showQuestion();

        // \uD83C\uDFAF First Attempt badge — fires once when the quiz panel is first shown
        if (!firstAttemptShown) {
            firstAttemptShown = true;
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            // Small delay so the quiz UI finishes rendering before the popup appears
            new Timer(400, e -> {
                ((Timer) e.getSource()).stop();
                BadgePopup.show(
                    (JFrame) SwingUtilities.getWindowAncestor(QuizManager.this),
                    BadgePopup.FIRST_ATTEMPT);
            }) {{ setRepeats(false); start(); }};
        }
    }

    //Question Loading (20 questions: 10 MCQ + 5 True/False + 5 Fill-Blank)
    private void loadQuestions() {

        //MCQ (10 questions)
        questions.add(new MCQQuestion(
            "How many minutes of moderate exercise are recommended for adults per week?",
            "B",
            new String[]{"A. 30 minutes", "B. 150 minutes", "C. 300 minutes", "D. 500 minutes"}
        ));
        questions.add(new MCQQuestion(
            "Which nutrient is primarily responsible for building and repairing muscles?",
            "C",
            new String[]{"A. Carbohydrates", "B. Fat", "C. Protein", "D. Fibre"}
        ));
        questions.add(new MCQQuestion(
            "Which of these drinks is the healthiest choice for daily hydration?",
            "A",
            new String[]{"A. Water", "B. Soda", "C. Energy Drink", "D. Alcohol"}
        ));
        questions.add(new MCQQuestion(
            "Which vitamin is naturally produced when skin is exposed to sunlight?",
            "B",
            new String[]{"A. Vitamin A", "B. Vitamin D", "C. Vitamin C", "D. Vitamin K"}
        ));
        questions.add(new MCQQuestion(
            "SDG 3 aims to ensure good health and well-being for all by which year?",
            "C",
            new String[]{"A. 2020", "B. 2025", "C. 2030", "D. 2050"}
        ));
        questions.add(new MCQQuestion(
            "How many servings of fruits and vegetables are recommended daily?",
            "B",
            new String[]{"A. 2", "B. 5", "C. 8", "D. 10"}
        ));
        questions.add(new MCQQuestion(
            "How many hours of sleep are recommended for adults aged 18–64?",
            "B",
            new String[]{"A. 5–6 hours", "B. 7–9 hours", "C. 10–12 hours", "D. 4–5 hours"}
        ));
        questions.add(new MCQQuestion(
            "Which disease was completely eradicated through widespread vaccination?",
            "A",
            new String[]{"A. Smallpox", "B. Malaria", "C. Tuberculosis", "D. Cholera"}
        ));
        questions.add(new MCQQuestion(
            "Approximately how many people worldwide lack access to safe drinking water?",
            "C",
            new String[]{"A. 500 million", "B. 1 billion", "C. 2 billion", "D. 3 billion"}
        ));
        questions.add(new MCQQuestion(
            "Which habit has the GREATEST positive impact on mental health?",
            "D",
            new String[]{"A. Sleeping 3 hours", "B. Skipping meals", "C. Excessive screen time", "D. Regular exercise"}
        ));

        //True / False (5 questions)
        questions.add(new TrueFalseQuestion(
            "Vaccines work by training the immune system to fight specific diseases without causing the disease itself.",
            "A" // True
        ));
        questions.add(new TrueFalseQuestion(
            "Mental health issues are rare and only affect 1 in 100 people worldwide.",
            "B" // False
        ));
        questions.add(new TrueFalseQuestion(
            "Eating breakfast can improve concentration and memory.",
            "A" // True
        ));
        questions.add(new TrueFalseQuestion(
            "Alcohol consumption is linked to fewer than 10 diseases and injuries.",
            "B" // False
        ));
        questions.add(new TrueFalseQuestion(
            "Regular physical activity reduces the risk of chronic diseases such as diabetes and heart disease.",
            "A" // True
        ));

        // Fill-in-the-blank (5 questions)
        questions.add(new FillBlankQuestion(
            "SDG stands for Sustainable Development ________.",
            "goal"
        ));
        questions.add(new FillBlankQuestion(
            "Handwashing alone can reduce diarrheal diseases by up to ________%.",
            "50"
        ));
        questions.add(new FillBlankQuestion(
            "The recommended daily water intake is ________ glasses.",
            "8"
        ));
        questions.add(new FillBlankQuestion(
            "Smoking kills over ________ million people per year worldwide.",
            "8"
        ));
        questions.add(new FillBlankQuestion(
            "Children aged 14–17 should get ________ to 10 hours of sleep per night.",
            "8"
        ));
    }

    //  GUI CONSTRUCTION
    private void buildChrome() {
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        lblModuleTitle = new JLabel("SDG 3: Good Health & Well-being  |  Quiz");
        lblModuleTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblModuleTitle.setForeground(Color.WHITE);

        lblPageCounter = new JLabel("Question 1 / " + questions.size());
        lblPageCounter.setFont(SMALL_FONT);
        lblPageCounter.setForeground(new Color(200, 255, 220));
        lblPageCounter.setHorizontalAlignment(SwingConstants.RIGHT);

        // \u23F1 = ⏱  Timer label — starts green, turns red in last 10 seconds
        lblTimer = new JLabel("\u23F1 30s", SwingConstants.CENTER);
        lblTimer.setFont(new Font("Dialog", Font.BOLD, 14));
        lblTimer.setForeground(new Color(200, 255, 200));
        lblTimer.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(PRIMARY_COLOR);
        rightPanel.add(lblTimer);
        rightPanel.add(lblPageCounter);

        topPanel.add(lblModuleTitle, BorderLayout.WEST);
        topPanel.add(rightPanel,     BorderLayout.EAST);

        progressBar = new JProgressBar(0, questions.size());
        progressBar.setValue(1);
        progressBar.setStringPainted(true);
        progressBar.setForeground(ACCENT_COLOR);
        progressBar.setBackground(new Color(180, 220, 200));
        progressBar.setString("Progress: 1 / " + questions.size());
        progressBar.setFont(SMALL_FONT);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 5));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.add(topPanel,     BorderLayout.NORTH);
        headerPanel.add(progressBar,  BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
    }

    // Central white card
    private void buildCard() {
        cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new CompoundBorder(
            new LineBorder(CARD_BORDER, 2, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        //Question type badge + emoji
        lblEmoji = new JLabel("❓", SwingConstants.CENTER);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblEmoji.setPreferredSize(new Dimension(80, 60));

        lblQuestionType = new JLabel("", SwingConstants.CENTER);
        lblQuestionType.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblQuestionType.setOpaque(true);
        lblQuestionType.setBackground(ACCENT_COLOR);
        lblQuestionType.setForeground(new Color(60, 40, 0));
        lblQuestionType.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
        badgePanel.setBackground(Color.WHITE);
        badgePanel.add(lblQuestionType);

        JPanel headerRow = new JPanel(new BorderLayout(5, 4));
        headerRow.setBackground(Color.WHITE);
        headerRow.add(lblEmoji,    BorderLayout.WEST);
        headerRow.add(badgePanel,  BorderLayout.CENTER);
        headerRow.add(new JSeparator(), BorderLayout.SOUTH);

        //Question text
        txtQuestion = new JTextArea();
        txtQuestion.setFont(TITLE_FONT);
        txtQuestion.setForeground(TEXT_COLOR);
        txtQuestion.setBackground(Color.WHITE);
        txtQuestion.setEditable(false);
        txtQuestion.setLineWrap(true);
        txtQuestion.setWrapStyleWord(true);
        txtQuestion.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        //MCQ radio options (4 buttons)
        buttonGroup  = new ButtonGroup();
        radioOptions = new JRadioButton[4];
        JPanel radioPanel = new JPanel(new GridLayout(4, 1, 0, 6));
        radioPanel.setBackground(Color.WHITE);
        for (int i = 0; i < 4; i++) {
            radioOptions[i] = new JRadioButton();
            radioOptions[i].setFont(BODY_FONT);
            radioOptions[i].setBackground(Color.WHITE);
            radioOptions[i].setForeground(TEXT_COLOR);
            radioOptions[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            buttonGroup.add(radioOptions[i]);
            radioPanel.add(radioOptions[i]);
        }

        // Fill-in-the-blank field
        txtFillBlank = new JTextField();
        txtFillBlank.setFont(BODY_FONT);
        txtFillBlank.setForeground(TEXT_COLOR);
        txtFillBlank.setBorder(new CompoundBorder(
            new LineBorder(CARD_BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        txtFillBlank.setVisible(false);

        JPanel inputWrapper = new JPanel(new BorderLayout(0, 8));
        inputWrapper.setBackground(Color.WHITE);
        inputWrapper.add(radioPanel,   BorderLayout.NORTH);
        inputWrapper.add(txtFillBlank, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 6));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(txtQuestion,   BorderLayout.NORTH);
        centerPanel.add(inputWrapper,  BorderLayout.CENTER);

        // Welcome strip at top
        JLabel welcomeLabel = new JLabel(
            "Welcome, " + userProfile.getUsername() + "  🎯",
            SwingConstants.RIGHT);
        welcomeLabel.setFont(SMALL_FONT);
        welcomeLabel.setForeground(new Color(80, 130, 100));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        cardPanel.add(headerRow,    BorderLayout.NORTH);
        cardPanel.add(centerPanel,  BorderLayout.CENTER);
        cardPanel.add(welcomeLabel, BorderLayout.SOUTH);

        // wrap in scroll pane for small windows
        JScrollPane scroll = new JScrollPane(cardPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        scroll.setBackground(BG_COLOR);
        scroll.getViewport().setBackground(BG_COLOR);
        add(scroll, BorderLayout.CENTER);
    }

    // Bottom navigation bar
    private void buildNav() {
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        navPanel.setBackground(BG_COLOR);

        btnNext = createNavButton("Submit Answer");
        btnNext.addActionListener(e -> processAnswer());
        navPanel.add(btnNext);
        add(navPanel, BorderLayout.SOUTH);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 22));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(20, 110, 65)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(PRIMARY_COLOR); }
        });
        return btn;
    }

    // Display logic
    private void showQuestion() {
        Question q = questions.get(currentQuestionIndex);

        // Update chrome
        lblPageCounter.setText("Question " + (currentQuestionIndex + 1) + " / " + questions.size());
        progressBar.setValue(currentQuestionIndex + 1);
        progressBar.setString("Progress: " + (currentQuestionIndex + 1) + " / " + questions.size());

        // Question text
        txtQuestion.setText(q.getQuestionText());

        // Hide all input widgets first
        for (JRadioButton rb : radioOptions) rb.setVisible(false);
        txtFillBlank.setVisible(false);
        buttonGroup.clearSelection();
        txtFillBlank.setText("");

        // Populate the correct widget
        if (q instanceof MCQQuestion) {
            lblEmoji.setText("📋");
            lblQuestionType.setText("MULTIPLE CHOICE");
            MCQQuestion mcq = (MCQQuestion) q;
            String[] opts = mcq.getOptions();
            for (int i = 0; i < opts.length; i++) {
                radioOptions[i].setText(opts[i]);
                radioOptions[i].setVisible(true);
            }

        } else if (q instanceof TrueFalseQuestion) {
            lblEmoji.setText("✅");
            lblQuestionType.setText("TRUE / FALSE");
            radioOptions[0].setText("A.  True");
            radioOptions[1].setText("B.  False");
            radioOptions[0].setVisible(true);
            radioOptions[1].setVisible(true);

        } else if (q instanceof FillBlankQuestion) {
            lblEmoji.setText("✏️");
            lblQuestionType.setText("FILL IN THE BLANK");
            txtFillBlank.setVisible(true);
        }

        cardPanel.revalidate();
        cardPanel.repaint();

        // Start the 30-second countdown for this question
        startQuestionTimer();
    }

    // Validates user input and processes answer
    private void processAnswer() {
        // Stop the timer and bank remaining seconds as time bonus
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
            totalTimeBonusSeconds += secondsLeft;
        }

        try {
            Question q = questions.get(currentQuestionIndex);
            String answer = null;

            if (q instanceof MCQQuestion || q instanceof TrueFalseQuestion) {
                if (radioOptions[0].isSelected()) answer = "A";
                else if (radioOptions[1].isSelected()) answer = "B";
                else if (radioOptions[2].isSelected()) answer = "C";
                else if (radioOptions[3].isSelected()) answer = "D";

                if (answer == null) throw new Exception("Please select an answer before continuing.");

            } else if (q instanceof FillBlankQuestion) {
                answer = txtFillBlank.getText().trim().toLowerCase();
                if (answer.isEmpty()) throw new Exception("Please type your answer in the blank.");
            }

            if (q.checkAnswer(answer)) score++;

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                showQuestion();
                if (currentQuestionIndex == questions.size() - 1)
                    btnNext.setText("Finish Quiz");
            } else {
                showResult();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Input Required", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── Timer Logic ───────────────────────────────────────────

    /**
     * Starts a 30-second countdown for the current question.
     * Stops any existing timer first to prevent overlap.
     * When time runs out, auto-advances without adding a bonus.
     */
    private void startQuestionTimer() {
        // Stop previous timer if still running (e.g. user clicked Next very fast)
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }

        secondsLeft = SECONDS_PER_QUESTION;
        updateTimerLabel();

        questionTimer = new Timer(1000, e -> {
            secondsLeft--;
            updateTimerLabel();

            if (secondsLeft <= 0) {
                questionTimer.stop();
                handleTimeout();
            }
        });
        questionTimer.start();
    }

    /**
     * Updates the timer label text and colour.
     * Green when time is plentiful, red in the last 10 seconds.
     */
    private void updateTimerLabel() {
        // \u23F1 = ⏱
        lblTimer.setText("\u23F1 " + secondsLeft + "s  ");
        if (secondsLeft <= 10) {
            lblTimer.setForeground(new Color(255, 160, 160));   // light red warning
        } else {
            lblTimer.setForeground(new Color(200, 255, 200));   // normal light green
        }
    }

    /**
     * Called when the 30-second limit expires.
     * No time bonus is added (secondsLeft is 0).
     * Automatically advances to the next question or shows results.
     */
    private void handleTimeout() {
        // No bonus — time ran out (totalTimeBonusSeconds += 0 implicitly)
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            showQuestion();
            if (currentQuestionIndex == questions.size() - 1)
                btnNext.setText("Finish Quiz");
        } else {
            showResult();
        }
    }

    // Displays the final quiz result using RewardSystem (Member 3)
    private void showResult() {
        // Ensure timer is stopped before leaving quiz screen
        if (questionTimer != null && questionTimer.isRunning()) {
            questionTimer.stop();
        }

        removeAll();
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // ── Hand off to Member 3's RewardSystem ──────────────
        // RewardSystem handles: badge, stars, points, leaderboard, file save
        // totalTimeBonusSeconds = sum of remaining seconds across all questions
        RewardSystem rewardPanel = new RewardSystem(userProfile, score, questions.size(), totalTimeBonusSeconds);
        add(rewardPanel, BorderLayout.CENTER);

        revalidate();
        repaint();

        // \u2B50 Perfect Score badge — fires only when user answers all questions correctly
        if (score == questions.size()) {
            new Timer(600, e -> {
                ((Timer) e.getSource()).stop();
                BadgePopup.show(
                    (JFrame) SwingUtilities.getWindowAncestor(QuizManager.this),
                    BadgePopup.PERFECT_SCORE);
            }) {{ setRepeats(false); start(); }};
        }
    }

}