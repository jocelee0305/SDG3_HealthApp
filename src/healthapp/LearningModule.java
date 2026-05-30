package healthapp;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
 
public class LearningModule extends JPanel implements ContentDisplay {
 
    // colours and fonts
    private static final Color BG_COLOR      = new Color(240, 248, 245);
    private static final Color PRIMARY_COLOR = new Color(34, 139, 87);
    private static final Color ACCENT_COLOR  = new Color(255, 193, 7);
    private static final Color TEXT_COLOR    = new Color(30, 40, 35);
    private static final Font  TITLE_FONT    = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font  BODY_FONT     = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font  SMALL_FONT    = new Font("Segoe UI", Font.ITALIC, 11);
 
    // fields
    private int currentPage = 0;
    private UserProfile userProfile;

    private Runnable startQuizAction;

    public LearningModule(UserProfile userProfile,
                        Runnable startQuizAction) {

        this.userProfile = userProfile;
        this.startQuizAction = startQuizAction;

        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        initComponents();
        displayPage(0);
    }
 
    // GUI components
    private JLabel       lblPageTitle;
    private JLabel       lblEmoji;
    private JTextArea    txtContent;
    private JLabel       lblFact;
    private JLabel       lblPageCounter;
    private JButton      btnPrev;
    private JButton      btnNext;
    private JProgressBar progressBar;
 
    // page titles
    private static final String[] PAGE_TITLES = {
        "What is SDG 3?",
        "Why Health Matters",
        "Healthy Eating Habits",
        "Physical Activity",
        "Importance of Sleep",
        "Mental Health Awareness",
        "Clean Water and Hygiene",
        "Vaccinations and Prevention",
        "Avoiding Harmful Substances",
        "Access to Healthcare",
        "Community and Family Health",
        "Building a Healthier Future"
    };
 
    // page emojis
    private static final String[] PAGE_EMOJIS = {
        "🌍", "❤️", "🍎", "🏃", "😴", "🧠", "💧", "💉", "🚭", "🏥", "👨‍👩‍👧", "🌱"
    };
 
    // page content
    private static final String[] PAGE_CONTENT = {
        "SDG 3 stands for Sustainable Development Goal 3: Good Health and Well-being.\n\n"
        + "Adopted by the United Nations in 2015, it aims to ensure healthy lives and "
        + "promote well-being for ALL people at ALL ages by 2030.\n\n"
        + "Key targets include:\n"
        + "  - Reduce maternal and child mortality\n"
        + "  - End epidemics like AIDS, malaria, and tuberculosis\n"
        + "  - Promote mental health and well-being\n"
        + "  - Achieve universal health coverage",
 
        "Good health is the foundation of a productive and happy life.\n\n"
        + "When people are healthy, they can:\n"
        + "  - Learn and study more effectively\n"
        + "  - Work and contribute to their communities\n"
        + "  - Enjoy better relationships and happiness\n\n"
        + "Unfortunately, millions of people worldwide still lack access to basic "
        + "healthcare. Understanding health empowers us to make better choices every day.",
 
        "A balanced diet is essential for maintaining good health.\n\n"
        + "Key principles of healthy eating:\n"
        + "  - Eat plenty of fruits and vegetables (5 servings/day)\n"
        + "  - Choose whole grains over refined foods\n"
        + "  - Include lean proteins: fish, chicken, legumes, eggs\n"
        + "  - Limit sugar, salt, and processed foods\n"
        + "  - Stay hydrated: drink 8 glasses of water daily\n\n"
        + "Remember: food is medicine. What you eat directly affects your energy, "
        + "mood, and long-term health.",
 
        "Regular physical activity is one of the best things you can do for your health.\n\n"
        + "WHO recommends:\n"
        + "  - Adults: at least 150 minutes of moderate activity per week\n"
        + "  - Children: at least 60 minutes of active play per day\n\n"
        + "Benefits of exercise:\n"
        + "  - Strengthens heart and lungs\n"
        + "  - Builds strong muscles and bones\n"
        + "  - Reduces risk of chronic diseases\n"
        + "  - Boosts mood and reduces stress\n"
        + "  - Improves sleep quality",
 
        "Sleep is not a luxury — it is a biological necessity.\n\n"
        + "Recommended sleep hours:\n"
        + "  - Teenagers (14-17): 8-10 hours per night\n"
        + "  - Adults (18-64): 7-9 hours per night\n"
        + "  - Older adults (65+): 7-8 hours per night\n\n"
        + "Poor sleep leads to:\n"
        + "  - Weakened immune system\n"
        + "  - Difficulty concentrating and learning\n"
        + "  - Increased risk of obesity and diabetes\n"
        + "  - Mood swings and emotional problems\n\n"
        + "Tips: Avoid screens 1 hour before bed. Keep a regular sleep schedule!",
 
        "Mental health is just as important as physical health.\n\n"
        + "Common mental health conditions:\n"
        + "  - Depression: persistent sadness and loss of interest\n"
        + "  - Anxiety: excessive worry and fear\n"
        + "  - Stress: overwhelm from life pressures\n\n"
        + "How to protect your mental health:\n"
        + "  - Talk to someone you trust\n"
        + "  - Practice mindfulness and deep breathing\n"
        + "  - Exercise regularly\n"
        + "  - Limit social media use\n"
        + "  - Seek professional help when needed\n\n"
        + "Remember: It is okay to ask for help. You are not alone!",
 
        "Access to clean water and good hygiene prevents millions of deaths every year.\n\n"
        + "Essential hygiene practices:\n"
        + "  - Wash hands with soap for at least 20 seconds\n"
        + "  - Brush teeth twice daily\n"
        + "  - Drink only clean, safe water\n"
        + "  - Keep living spaces clean and ventilated\n\n"
        + "Globally, 2 billion people still lack access to safe drinking water. "
        + "Simple hygiene habits can stop the spread of diseases like cholera, "
        + "typhoid, and diarrhea.",
 
        "Vaccines are one of the greatest achievements in medical history.\n\n"
        + "How vaccines work:\n"
        + "  - They train your immune system to fight specific diseases\n"
        + "  - Provide protection without causing the disease\n\n"
        + "Vaccines have eliminated or nearly eliminated:\n"
        + "  - Smallpox (completely eradicated!)\n"
        + "  - Polio\n"
        + "  - Measles outbreaks\n\n"
        + "Regular health check-ups and screenings are also important for "
        + "catching problems early, when they are easiest to treat.",
 
        "Avoiding harmful substances is critical for long-term health.\n\n"
        + "Dangerous substances to avoid:\n"
        + "  - Tobacco/cigarettes: causes lung cancer, heart disease\n"
        + "  - Alcohol (excessive): damages liver, affects brain\n"
        + "  - Illegal drugs: destroys health and relationships\n\n"
        + "Did you know?\n"
        + "  - Smoking kills over 8 million people per year worldwide\n"
        + "  - Alcohol is linked to over 200 diseases and injuries\n\n"
        + "If you or someone you know needs help, reach out to a healthcare professional.",
 
        "Universal health coverage means ALL people can access quality healthcare "
        + "without financial hardship.\n\n"
        + "Barriers to healthcare include:\n"
        + "  - High costs of treatment\n"
        + "  - Living in rural or remote areas\n"
        + "  - Lack of trained healthcare workers\n\n"
        + "What YOU can do:\n"
        + "  - Know your nearest clinic or hospital\n"
        + "  - Get regular health screenings\n"
        + "  - Support community health programs\n"
        + "  - Advocate for accessible healthcare in your community",
 
        "Health is not just an individual responsibility — it is a community effort.\n\n"
        + "Healthy family habits:\n"
        + "  - Cook and eat meals together\n"
        + "  - Exercise as a family\n"
        + "  - Talk openly about health concerns\n"
        + "  - Support each other's mental well-being\n\n"
        + "Community actions:\n"
        + "  - Participate in health awareness campaigns\n"
        + "  - Keep public spaces clean\n"
        + "  - Support local health initiatives\n"
        + "  - Educate others about SDG 3",
 
        "By learning about Good Health and Well-being, YOU are part of the solution!\n\n"
        + "Small daily actions make a big difference:\n"
        + "  - Eat nutritious food\n"
        + "  - Exercise regularly\n"
        + "  - Sleep enough\n"
        + "  - Practice good hygiene\n"
        + "  - Take care of your mental health\n"
        + "  - Get vaccinated\n"
        + "  - Avoid harmful substances\n"
        + "  - Support others in your community\n\n"
        + "Together, we can achieve SDG 3 and build a healthier, happier world for everyone!"
    };
 
    // fun facts shown at the bottom of each page
    private static final String[] FUN_FACTS = {
        "Fun Fact: SDG 3 has 13 specific targets to achieve by 2030!",
        "Fun Fact: Over 400 million people worldwide have no access to basic healthcare.",
        "Fun Fact: Eating breakfast improves concentration and memory by up to 20%!",
        "Fun Fact: Just 30 minutes of walking per day reduces heart disease risk by 35%!",
        "Fun Fact: Lack of sleep costs the US economy $411 billion per year in lost productivity!",
        "Fun Fact: 1 in 4 people globally will be affected by mental health issues at some point.",
        "Fun Fact: Handwashing alone can reduce diarrheal diseases by up to 50%!",
        "Fun Fact: Vaccines prevent 2 to 3 million deaths every single year!",
        "Fun Fact: Every cigarette smoked reduces life expectancy by approximately 11 minutes.",
        "Fun Fact: Only 56% of the global population has access to essential health services.",
        "Fun Fact: Children who grow up in healthy households perform better academically!",
        "Fun Fact: Malaysia's life expectancy has risen from 62 years (1970) to 76 years (2023)!"
    };
 
    // constructor
    public LearningModule(UserProfile userProfile) {
        this.userProfile = userProfile;
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
        displayPage(0);
    }
 
    // build and arrange all GUI components
    private void initComponents() {
 
        // --- TOP HEADER ---
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
 
        JLabel lblModuleTitle = new JLabel("SDG 3: Good Health & Well-being  |  Learning Module");
        lblModuleTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblModuleTitle.setForeground(Color.WHITE);
 
        lblPageCounter = new JLabel("Page 1 / " + getTotalPages());
        lblPageCounter.setFont(SMALL_FONT);
        lblPageCounter.setForeground(new Color(200, 255, 220));
        lblPageCounter.setHorizontalAlignment(SwingConstants.RIGHT);
 
        topPanel.add(lblModuleTitle, BorderLayout.WEST);
        topPanel.add(lblPageCounter, BorderLayout.EAST);
 
        // progress bar
        progressBar = new JProgressBar(0, getTotalPages());
        progressBar.setValue(1);
        progressBar.setStringPainted(true);
        progressBar.setForeground(ACCENT_COLOR);
        progressBar.setBackground(new Color(180, 220, 200));
        progressBar.setString("Progress: 1 / " + getTotalPages());
        progressBar.setFont(SMALL_FONT);
 
        JPanel headerPanel = new JPanel(new BorderLayout(0, 5));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(progressBar, BorderLayout.SOUTH);
 
        // --- CENTER CONTENT CARD ---
        JPanel cardPanel = new JPanel(new BorderLayout(10, 10));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 230, 210), 2, true),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
 
        // emoji icon
        lblEmoji = new JLabel("", SwingConstants.CENTER);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblEmoji.setPreferredSize(new Dimension(100, 80));
        lblEmoji.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
 
        // page title
        lblPageTitle = new JLabel("Loading...", SwingConstants.CENTER);
        lblPageTitle.setFont(TITLE_FONT);
        lblPageTitle.setForeground(PRIMARY_COLOR);
 
        JPanel titlePanel = new JPanel(new BorderLayout(5, 5));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(lblEmoji, BorderLayout.NORTH);
        titlePanel.add(lblPageTitle, BorderLayout.CENTER);
        titlePanel.add(new JSeparator(), BorderLayout.SOUTH);
 
        // content text area (scrollable)
        txtContent = new JTextArea();
        txtContent.setFont(BODY_FONT);
        txtContent.setForeground(TEXT_COLOR);
        txtContent.setBackground(Color.WHITE);
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
 
        JScrollPane scrollPane = new JScrollPane(txtContent);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
 
        // fun fact label at bottom
        lblFact = new JLabel("", SwingConstants.CENTER);
        lblFact.setFont(SMALL_FONT);
        lblFact.setForeground(new Color(80, 80, 130));
        lblFact.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 250), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
 
        cardPanel.add(titlePanel,  BorderLayout.NORTH);
        cardPanel.add(scrollPane,  BorderLayout.CENTER);
        cardPanel.add(lblFact,     BorderLayout.SOUTH);
 
        // --- BOTTOM NAVIGATION ---
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        navPanel.setBackground(BG_COLOR);
 
        btnPrev = createNavButton("< Previous");
        btnNext = createNavButton("Next >");
 
        btnPrev.addActionListener(e -> previousPage());
        btnNext.addActionListener(e -> nextPage());
 
        navPanel.add(btnPrev);
        navPanel.add(btnNext);
 
        // assemble all panels
        add(headerPanel, BorderLayout.NORTH);
        add(cardPanel,   BorderLayout.CENTER);
        add(navPanel,    BorderLayout.SOUTH);
    }
 
    // helper: create a styled navigation button
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(20, 110, 65)); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(PRIMARY_COLOR); }
        });
        return btn;
    }
 
    // --- ContentDisplay interface methods ---
 
    @Override
    public void displayPage(int pageIndex) {
        if (pageIndex < 0 || pageIndex >= getTotalPages()) return;
 
        currentPage = pageIndex;
 
        // update content
        lblEmoji.setText(PAGE_EMOJIS[pageIndex]);
        lblPageTitle.setText(PAGE_TITLES[pageIndex]);
        txtContent.setText(PAGE_CONTENT[pageIndex]);
        txtContent.setCaretPosition(0);
        lblFact.setText("<html><center>" + FUN_FACTS[pageIndex] + "</center></html>");
 
        // update counter and progress bar
        lblPageCounter.setText("Page " + (currentPage + 1) + " / " + getTotalPages());
        progressBar.setValue(currentPage + 1);
        progressBar.setString("Progress: " + (currentPage + 1) + " / " + getTotalPages());
 
        // enable buttons
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(true);
 
        // track user progress
        userProfile.markPageVisited();
 
        // check if last page
        if (currentPage == getTotalPages() - 1) {
            userProfile.completeLearning(getTotalPages());
            btnNext.setText("Start Quiz");
        } else {
            btnNext.setText("Next >");
        }
    }
 
    @Override
    public int getTotalPages() {
        return PAGE_TITLES.length; // returns 12
    }
 
    @Override
    public void nextPage() {

        if (currentPage < getTotalPages() - 1) {

            displayPage(currentPage + 1);

        } else {

            if (startQuizAction != null) {
                startQuizAction.run();
            }
        }
    }

    @Override
    public void previousPage() {
        if (currentPage > 0) {
            displayPage(currentPage - 1);
        }
    }
 
    @Override
    public String getPageTitle(int pageIndex) {
        if (pageIndex < 0 || pageIndex >= getTotalPages()) return "";
        return PAGE_TITLES[pageIndex];
    }
}