import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginPanel extends JPanel {
    private final VotingSystem parentFrame;

    public LoginPanel(VotingSystem frame) {
        this.parentFrame = frame;
        setBackground(new Color(245, 245, 247)); // Apple light gray
        setLayout(new BorderLayout());

        // ===== CENTER PANEL =====
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        add(center, BorderLayout.CENTER);

        // ===== CARD PANEL WITH SOFT SHADOW =====
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(6, 8, getWidth() - 12, getHeight() - 16, 22, 22);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(35, 50, 35, 50));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(600, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        center.add(card, gbc);

        // ===== TITLE =====
        JLabel title = new JLabel("WELCOME BACK, PILI-PINAS 2028");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(40, 40, 40));
        title.setAlignmentX(Component.RIGHT_ALIGNMENT);
        card.add(title);

        card.add(Box.createVerticalStrut(6));

        // ===== FLAG ACCENT LINE BELOW TITLE =====
        JPanel flagLine = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth();
                int h = getHeight();
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int lineHeight = h;
                g2.setColor(new Color(0, 56, 147));
                g2.fillRect(0, 0, w / 3, lineHeight);
                g2.setColor(new Color(206, 17, 38));
                g2.fillRect(w / 3, 0, w / 3, lineHeight);
                g2.setColor(new Color(255, 205, 0));
                g2.fillRect(2 * w / 3, 0, w / 3, lineHeight);
            }
        };
        flagLine.setPreferredSize(new Dimension(300, 6));
        flagLine.setAlignmentX(Component.RIGHT_ALIGNMENT);
        card.add(flagLine);

        card.add(Box.createVerticalStrut(12));

        // ===== SUBTITLE =====
        JLabel subtitle = new JLabel("Sign in with your credentials");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(110, 110, 110));
        subtitle.setAlignmentX(Component.RIGHT_ALIGNMENT);
        card.add(subtitle);

        card.add(Box.createVerticalStrut(24));

        // ===== USERNAME =====
        JLabel userLabel = appleLabel("Username");
        card.add(userLabel);

        JTextField usernameField = appleTextField();
        card.add(usernameField);

        card.add(Box.createVerticalStrut(16));

        // ===== PASSWORD =====
        JLabel passLabel = appleLabel("Password");
        card.add(passLabel);

        JPasswordField passwordField = applePasswordField();
        card.add(passwordField);

        card.add(Box.createVerticalStrut(24));

        // ===== LOGIN BUTTON =====
        JButton loginButton = appleGradientButton("Sign In");
        loginButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        loginButton.addActionListener(e -> {
            String u = usernameField.getText().trim();
            String p = new String(passwordField.getPassword());

            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both username and password!");
                return;
            }

            String voterId = DatabaseHelper.authenticateUser(u, p);
            if (voterId != null) {
                parentFrame.setLoggedInVoterId(voterId);
                parentFrame.showVotingScreen();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials!");
                passwordField.setText("");
            }
        });
        card.add(loginButton);
    }

    // ================= APPLE LABEL =================
    private JLabel appleLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(new Color(90, 90, 90));
        lbl.setAlignmentX(Component.RIGHT_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(10,0,10,0));
        return lbl;
    }

    // ================= APPLE TEXT FIELD =================
    private JTextField appleTextField() {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(250, 250, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                super.paintComponent(g);
            }
        };

        f.setFont(new Font("Arial", Font.PLAIN, 14));
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(8, 14, 8, 14));
        f.setPreferredSize(new Dimension(300, 40));
        f.setMinimumSize(new Dimension(300, 40));
        f.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return f;
    }

    // ================= APPLE PASSWORD FIELD =================
    private JPasswordField applePasswordField() {
        JPasswordField f = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(250, 250, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                super.paintComponent(g);
            }
        };

        f.setFont(new Font("Arial", Font.PLAIN, 14));
        f.setOpaque(false);
        f.setBorder(new EmptyBorder(8, 14, 8, 14));
        f.setPreferredSize(new Dimension(300, 40));
        f.setMinimumSize(new Dimension(300, 40));
        f.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return f;
    }

    // ================= APPLE SOFT GRADIENT BUTTON =================
    private JButton appleGradientButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color top = new Color(130, 170, 255);
                Color bottom = new Color(90, 140, 240);

                g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(300, 42));
        btn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return btn;
    }
}
