import java.awt.*;
import java.util.*;
import javax.swing.*;

public class VotedSummaryPanel extends JPanel {
    private final String voterId;

    public VotedSummaryPanel(String voterId) {
        this.voterId = voterId;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("BALLOT REVIEW - ALREADY VOTED", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(210, 25, 25));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Your Votes", createSummaryPanel());
        tabbedPane.addTab("Vote Counts", createVoteCountPanel());

        add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        StringBuilder summary = new StringBuilder();
        summary.append("YOUR SUBMITTED BALLOT\n");
        summary.append("=".repeat(70)).append("\n\n");

        String[] offices = {"President", "Vice President", "Senator", "Partylist", "Congressman", "Mayor", "Vice Mayor", "Councilor"};

        for (String office : offices) {
            summary.append(office).append(":\n");
            Set<String> votes = DatabaseHelper.getVoterVotesForOffice(voterId, office);

            if (votes.isEmpty()) {
                summary.append("  ✓ ABSTAIN\n");
            } else {
                for (String vote : votes) {
                    summary.append("  ✓ ").append(vote).append("\n");
                }
            }
            summary.append("\n");
        }

        JTextArea textArea = new JTextArea(summary.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createVoteCountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        StringBuilder summary = new StringBuilder();
        summary.append("REAL-TIME VOTE COUNTS\n");
        summary.append("=".repeat(70)).append("\n\n");

        String[] offices = {"President", "Vice President", "Senator", "Partylist", "Congressman", "Mayor", "Vice Mayor", "Councilor"};

        for (String office : offices) {
            summary.append(office).append(":\n");
            summary.append("-".repeat(70)).append("\n");

            String[] candidates = getCandidatesForOffice(office);
            Map<String, Integer> voteCounts = DatabaseHelper.getVoteCounts(office);

            for (String candidate : candidates) {
                int count = voteCounts.getOrDefault(candidate, 0);
                summary.append(String.format("  %-40s : %6d votes%n", candidate, count));
            }

            summary.append("\n");
        }

        JTextArea textArea = new JTextArea(summary.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        textArea.setLineWrap(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private String[] getCandidatesForOffice(String office) {
        switch (office) {
            case "President":
                return new String[]{"Marcos, BongBong", "Robredo, Leni"};
            case "Vice President":
                return new String[]{"Duterte, Sara", "Pangalinan, Kiko"};
            case "Senator":
                return new String[]{
                        "Padilla, Robin", "Legarda, Loren", "Tulfo, Raffy", "Gatchalian, Win",
                        "Escudero, Chiz", "Villar, Mark", "Cayetanno, Alan", "Zubiri, Migz",
                        "Villanueva, Joel", "Ejercito, JV", "Hontiveros, Risa", "Estrada, Jinggoy",
                        "Binay, Jojo", "Bautista, Herbert", "Teodoro, Gibo"
                };
            case "Partylist":
                return new String[]{"Kabataan", "Akbayan", "Duterte Youth", "Ang Probinsyano", "IT-Warriors"};
            case "Congressman":
                return new String[]{"Rodriguez, Rufus", "Casino, Patrick"};
            case "Mayor":
                return new String[]{"Moreno, Oscar", "Uy, Klarex"};
            case "Vice Mayor":
                return new String[]{"Rodriguez, Bebot", "Navarro, Vhong"};
            case "Councilor":
                return new String[]{
                        "Abris, Rene", "Abejuela, Anthony", "Achas, Ian", "Atterviry, Kap Bossing",
                        "Balaba, Girlie GB", "Borja, Jasmin", "Cabanlas, Edgar", "Cagang, RC",
                        "CotiIamco, Elmer", "Emano, Yvy", "Gaane, Paolo", "Go, Gigi", "Jardin, Jess",
                        "Judith, James II", "Lapiz, Edison", "Lim, Alam", "Lucman, Unotan Jr.",
                        "Mulat, Maricris", "Oblimar, Rudy", "Olandesca, Ike", "Olandesca, Ram",
                        "Pitogo, BJ Raven", "Postrero, Domer", "Rodriguez, Jonjon", "Rollo, Sammrules",
                        "Sabal, Boboy", "Salcedo, Eric", "Sarenas, Kenneth", "Villazorda, Roger"
                };
            default:
                return new String[]{};
        }
    }
}
