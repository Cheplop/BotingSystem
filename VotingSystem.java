
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class VotingSystem extends JFrame {
    private String loggedInVoterId;

    public VotingSystem() {
        setTitle("Online Voting System - Login");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        LoginPanel login = new LoginPanel(this);
        add(login, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setLoggedInVoterId(String id) {
        this.loggedInVoterId = id;
    }

    public void showVotingScreen() {
        // Check if voter has already voted
        if (DatabaseHelper.hasVoterVoted(loggedInVoterId)) {
            // Show already-voted panel (view-only)
            getContentPane().removeAll();
            setTitle("Online Voting System - Ballot Review");
            setSize(1000, 800);

            VotedSummaryPanel votedPanel = new VotedSummaryPanel(loggedInVoterId);
            add(votedPanel, BorderLayout.CENTER);

            revalidate();
            repaint();
            setLocationRelativeTo(null);
            setVisible(true);
            return;
        }

        getContentPane().removeAll();
        setTitle("Online Voting System - Official Ballot");
        setSize(1000, 900);

        BallotPanel ballot = new BallotPanel(loggedInVoterId, this);
        add(ballot, BorderLayout.CENTER);

        revalidate();
        repaint();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VotingSystem::new);
    }
}