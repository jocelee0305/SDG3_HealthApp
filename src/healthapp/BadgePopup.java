package healthapp;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

// ============================================================
// Class      : BadgePopup
// Creator    : Jocelyn (104561)
// Description: Reusable glass-overlay popup for process badges.
//              Shows a darkened background with a centred card
//              containing the badge icon, name, description, and
//              a close button (X) at the top-right corner.
//              Triggered by: LearningModule, QuizManager.
//
// Usage:
//   BadgePopup.show(parentFrame, BadgePopup.KNOWLEDGE_SEEKER);
//   BadgePopup.show(parentFrame, BadgePopup.FIRST_ATTEMPT);
//   BadgePopup.show(parentFrame, BadgePopup.PERFECT_SCORE);
// ============================================================

public class BadgePopup extends JDialog {

    // ── Badge type constants ──────────────────────────────────
    public static final int KNOWLEDGE_SEEKER = 0;
    public static final int FIRST_ATTEMPT    = 1;
    public static final int PERFECT_SCORE    = 2;

    // ── Badge data: {icon, name, description, hex accent colour} ─
    private static final String[][] BADGE_DATA = {
        // KNOWLEDGE_SEEKER
        {
            "\uD83D\uDCD6",                         // 📖
            "Knowledge Seeker",
            "You've read through all the health info.\nKnowledge is the first step to better well-being!",
            "#2E7D52"                               // green accent
        },
        // FIRST_ATTEMPT
        {
            "\uD83C\uDFAF",                         // 🎯
            "First Attempt",
            "You took the first step and started the quiz.\nEvery expert was once a beginner!",
            "#1565A8"                               // blue accent
        },
        // PERFECT_SCORE
        {
            "\u2B50",                               // ⭐  (BMP — no surrogate needed)
            "Perfect Score",
            "You answered every question correctly.\nThat's an outstanding achievement!",
            "#B07A00"                               // gold accent
        }
    };

    // ── Constructor (private — use static show()) ─────────────
    private BadgePopup(JFrame parent, int badgeType) {
        super(parent, true);   // modal = true so it blocks parent interaction

        String[] data   = BADGE_DATA[badgeType];
        String   icon   = data[0];
        String   name   = data[1];
        String   desc   = data[2];
        Color    accent = hexToColor(data[3]);
        Color    accentBg = new Color(
            Math.min(255, accent.getRed()   + 200),
            Math.min(255, accent.getGreen() + 200),
            Math.min(255, accent.getBlue()  + 200)
        );  // very light tint of the accent colour for the header bg

        setUndecorated(true);           // no OS title bar
        setBackground(new Color(0, 0, 0, 0));  // transparent window
        setSize(parent.getSize());
        setLocationRelativeTo(parent);

        // ── Root: semi-transparent dark overlay ───────────────
        JPanel overlay = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 150));   // 150/255 ≈ 59% opacity
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);

        // ── Card ──────────────────────────────────────────────
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        card.setPreferredSize(new Dimension(290, 300));

        // ── Header (coloured top section) ─────────────────────
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(accentBg);
        header.setBorder(BorderFactory.createEmptyBorder(28, 24, 18, 24));

        // "BADGE UNLOCKED" label
        JLabel lblUnlocked = new JLabel("BADGE UNLOCKED", SwingConstants.CENTER);
        lblUnlocked.setFont(new Font("Dialog", Font.BOLD, 10));
        lblUnlocked.setForeground(accent);
        lblUnlocked.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Icon
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 0));

        // Badge name
        JLabel lblName = new JLabel(name, SwingConstants.CENTER);
        lblName.setFont(new Font("Dialog", Font.BOLD, 16));
        lblName.setForeground(new Color(30, 40, 35));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblUnlocked);
        header.add(lblIcon);
        header.add(lblName);

        // ── Body (description) ────────────────────────────────
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 20, 24));

        JTextArea txtDesc = new JTextArea(desc);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDesc.setForeground(new Color(90, 100, 95));
        txtDesc.setBackground(Color.WHITE);
        txtDesc.setEditable(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtDesc.setOpaque(false);

        body.add(txtDesc);

        // ── Assemble card ─────────────────────────────────────
        card.add(header, BorderLayout.NORTH);
        card.add(body,   BorderLayout.CENTER);

        // ── X close button (absolute top-right of card) ───────
        // Wrap card in a layered pane so we can position X freely
        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(new Dimension(290, 300));
        layered.setOpaque(false);

        card.setBounds(0, 0, 290, 300);
        layered.add(card, JLayeredPane.DEFAULT_LAYER);

        JButton btnClose = new JButton("\u2715");   // ✕ (multiplication sign, safe BMP)
        btnClose.setFont(new Font("Dialog", Font.BOLD, 12));
        btnClose.setForeground(new Color(100, 100, 100));
        btnClose.setBackground(Color.WHITE);
        btnClose.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        btnClose.setFocusPainted(false);
        btnClose.setOpaque(true);
        btnClose.setContentAreaFilled(true);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setBounds(252, 10, 28, 28);   // top-right of card
        btnClose.addActionListener(e -> dispose());
        layered.add(btnClose, JLayeredPane.PALETTE_LAYER);

        // Close on overlay click (outside card)
        overlay.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dispose(); }
        });

        overlay.add(layered);
        setContentPane(overlay);
    }

    // ── Static factory — call this from anywhere ───────────────
    /**
     * Show a badge unlock popup centred over the given parent frame.
     *
     * @param parent    The JFrame to overlay (pass 'this' or SwingUtilities.getWindowAncestor)
     * @param badgeType One of BadgePopup.KNOWLEDGE_SEEKER / FIRST_ATTEMPT / PERFECT_SCORE
     */
    public static void show(JFrame parent, int badgeType) {
        if (parent == null) return;
        // Small delay so the caller's UI finishes painting first
        SwingUtilities.invokeLater(() -> {
            BadgePopup popup = new BadgePopup(parent, badgeType);
            popup.setVisible(true);
        });
    }

    // ── Helper: parse hex colour string ───────────────────────
    private static Color hexToColor(String hex) {
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            return Color.GRAY;
        }
    }
}