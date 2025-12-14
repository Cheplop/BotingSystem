import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class BallotPanel extends JPanel {
    private final String voterId;
    private final VotingSystem parentFrame;

    private Map<String, Set<JCheckBox>> officeCheckboxes = new HashMap<>();
    private Map<String, JCheckBox> abstainCheckboxes = new HashMap<>();
    private Map<String, Integer> officeLimits = new HashMap<>();

    private JButton submitButton;
    private boolean submitted = false;

    public BallotPanel(String voterId, VotingSystem parentFrame) {
        this.voterId = voterId;
        this.parentFrame = parentFrame;

        // Vote limits
        officeLimits.put("Senator", 12);
        officeLimits.put("Councilor", 6);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("OFFICIAL BALLOT FORM - PHILIPPINES 2022", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(25, 118, 210));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(createBallotContent());
        add(scrollPane, BorderLayout.CENTER);

        add(createBottomButtons(), BorderLayout.SOUTH);
    }

    private JPanel createBallotContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(createOfficeSection("President", 2));
        mainPanel.add(createOfficeSection("Vice President", 2));
        mainPanel.add(createOfficeSection("Senator", 3));
        mainPanel.add(createOfficeSection("Partylist", 2));
        mainPanel.add(createLocalOffices());

        return wrapWithPadding(mainPanel);
    }

    private JPanel createOfficeSection(String office, int columns) {
        JPanel section = buildOfficePanel(office, columns, getCandidatesForOffice(office));
        return wrapWithPadding(section);
    }

    private JPanel createLocalOffices() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (String office : new String[]{"Congressman", "Mayor", "Vice Mayor", "Councilor"}) {
            JPanel section = buildOfficePanel(office, 2, getCandidatesForOffice(office));
            panel.add(wrapWithPadding(section));
        }
        return panel;
    }

    private JPanel buildOfficePanel(String office, int columns, String[] candidates) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 100, 200), 2),
                office + " / Vote for " + officeLimits.getOrDefault(office, 1),
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                new Color(25, 118, 210)
            )
        );
        section.setBackground(Color.WHITE);

        JPanel gridPanel = new JPanel(new GridLayout(0, columns, 10, 5));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Set<JCheckBox> checkboxes = new HashSet<>();
        officeCheckboxes.put(office, checkboxes);

        JCheckBox abstainCheckbox = new JCheckBox("ABSTAIN");
        abstainCheckbox.setFont(new Font("Arial", Font.BOLD, 11));
        abstainCheckbox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                checkboxes.forEach(cb -> { if (cb != abstainCheckbox) cb.setSelected(false); });
            }
        });
        abstainCheckboxes.put(office, abstainCheckbox);

        gridPanel.add(abstainCheckbox);
        checkboxes.add(abstainCheckbox);

        int limit = officeLimits.getOrDefault(office, 1);
        boolean isMultiSelect = limit > 1;

        Set<String> existingVotes = DatabaseHelper.getVoterVotesForOffice(voterId, office);

        for (String candidate : candidates) {
            JCheckBox cb = new JCheckBox(candidate);
            cb.setFont(new Font("Arial", Font.PLAIN, 10));
            cb.setSelected(existingVotes.contains(candidate));

            cb.addItemListener(e -> handleCheckChange(office, cb));

            gridPanel.add(cb);
            checkboxes.add(cb);
        }

        section.add(gridPanel, BorderLayout.CENTER);
        return section;
    }

    private void handleCheckChange(String office, JCheckBox selectedBox) {
        JCheckBox abstain = abstainCheckboxes.get(office);
        Set<JCheckBox> checkboxes = officeCheckboxes.get(office);

        int limit = officeLimits.getOrDefault(office, 1);

        if (selectedBox == abstain) return;

        if (selectedBox.isSelected()) abstain.setSelected(false);

        if (limit == 1) {
            // single-select logic
            checkboxes.forEach(cb -> {
                if (cb != selectedBox && cb != abstain) cb.setSelected(false);
            });
            return;
        }

        // multi-select limit
        long count = checkboxes.stream()
                .filter(cb -> cb != abstain && cb.isSelected())
                .count();

        if (count > limit) {
            selectedBox.setSelected(false);
            JOptionPane.showMessageDialog(this,
                "You can only vote for a maximum of " + limit + " " + office);
        }
    }

    private JPanel createBottomButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton summaryButton = new JButton("View Your Summary");
        summaryButton.addActionListener(e -> showVoterSummary());
        panel.add(summaryButton);

        JButton voteCountButton = new JButton("View Vote Counts");
        voteCountButton.addActionListener(e -> showVoteCountSummary());
        panel.add(voteCountButton);

        submitButton = new JButton("Submit Ballot");
        submitButton.addActionListener(new SubmitButtonListener());
        submitButton.setBackground(new Color(34, 139, 34));
        submitButton.setForeground(Color.WHITE);
        panel.add(submitButton);

        return panel;
    }

    private JPanel wrapWithPadding(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        return wrapper;
    }

    /* ---------------- UTILITY METHODS ---------------- */

    private String[] getCandidatesForOffice(String office) {
        switch (office) {
            case "President":
                return new String[]{"Marcos, BongBong", "Robredo, Leni"};
            case "Vice President":
                return new String[]{"Duterte, Sara", "Pangalinan, Kiko"};
            case "Senator":
                return new String[]{"Padilla, Robin", "Legarda, Loren", "Tulfo, Raffy", "Gatchalian, Win",
                        "Escudero, Chiz", "Villar, Mark", "Cayetanno, Alan", "Zubiri, Migz",
                        "Villanueva, Joel", "Ejercito, JV", "Hontiveros, Risa", "Estrada, Jinggoy",
                        "Binay, Jojo", "Bautista, Herbert", "Teodoro, Gibo"};
            case "Partylist":
                return new String[]{"Kabataan", "Akbayan", "Duterte Youth", "Ang Probinsyano", "IT-Warriors"};
            case "Congressman":
                return new String[]{"Rodriguez, Rufus", "Casino, Patrick"};
            case "Mayor":
                return new String[]{"Moreno, Oscar", "Uy, Klarex"};
            case "Vice Mayor":
                return new String[]{"Rodriguez, Bebot", "Navarro, Vhong"};
            case "Councilor":
                return new String[]{"Abris, Rene", "Abejuela, Anthony", "Achas, Ian", "Atterviry, Kap Bossing",
                        "Balaba, Girlie GB", "Borja, Jasmin", "Cabanlas, Edgar", "Cagang, RC",
                        "CotiIamco, Elmer", "Emano, Yvy", "Gaane, Paolo", "Go, Gigi", "Jardin, Jess",
                        "Judith, James II", "Lapiz, Edison", "Lim, Alam", "Lucman, Unotan Jr.",
                        "Mulat, Maricris", "Oblimar, Rudy", "Olandesca, Ike", "Olandesca, Ram",
                        "Pitogo, BJ Raven", "Postrero, Domer", "Rodriguez, Jonjon", "Rollo, Sammrules",
                        "Sabal, Boboy", "Salcedo, Eric", "Sarenas, Kenneth", "Villazorda, Roger"};
        }
        return new String[]{};
    }

    private void showVoterSummary() {
        StringBuilder summary = new StringBuilder("YOUR BALLOT SUMMARY\n\n");

        for (String office : officeCheckboxes.keySet()) {
            summary.append(office).append(":\n");

            JCheckBox abstain = abstainCheckboxes.get(office);
            if (abstain.isSelected()) {
                summary.append("  ✓ ABSTAIN\n\n");
                continue;
            }

            boolean found = false;
            for (JCheckBox cb : officeCheckboxes.get(office)) {
                if (cb != abstain && cb.isSelected()) {
                    summary.append("  ✓ ").append(cb.getText()).append("\n");
                    found = true;
                }
            }

            if (!found) summary.append("  (No selection)\n");
            summary.append("\n");
        }

        JTextArea area = new JTextArea(summary.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Your Ballot Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showVoteCountSummary() {
        StringBuilder summary = new StringBuilder("REAL-TIME VOTE COUNTS\n\n");

        for (String office : officeCheckboxes.keySet()) {
            summary.append(office).append(":\n");

            Map<String, Integer> counts = DatabaseHelper.getVoteCounts(office);
            for (String name : getCandidatesForOffice(office)) {
                summary.append(String.format("  %-40s : %5d votes%n",
                        name, counts.getOrDefault(name, 0)));
            }
            summary.append("\n");
        }

        JTextArea area = new JTextArea(summary.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Vote Counts (Real-Time)", JOptionPane.INFORMATION_MESSAGE);
    }

    private class SubmitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (submitted) return;

            for (String office : officeCheckboxes.keySet()) {
                Set<String> selected = new HashSet<>();

                for (JCheckBox cb : officeCheckboxes.get(office)) {
                    if (!cb.getText().equals("ABSTAIN") && cb.isSelected()) {
                        selected.add(cb.getText());
                    }
                }

                String result = DatabaseHelper.recordVotesForOffice(voterId, office, selected);
                if (!"OK".equals(result)) {
                    JOptionPane.showMessageDialog(null, "Error submitting " + office + ": " + result);
                    return;
                }
            }

            submitted = true;
            submitButton.setEnabled(false);
            submitButton.setText("Ballot Submitted");
            submitButton.setBackground(new Color(150, 150, 150));

            JOptionPane.showMessageDialog(null, "Ballot submitted successfully!");
        }
    }
}
