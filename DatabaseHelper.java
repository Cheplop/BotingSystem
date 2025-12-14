import java.sql.*;
import java.util.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/voting_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "ochiguefugnit";

    public static String authenticateUser(String username, String password) {
        if ("remiel".equals(username) && "123456".equals(password)) return "VOTER003";
        if ("jussy".equals(username) && "12345".equals(password)) return "VOTER004";
        if ("trix".equals(username) && "1234".equals(password)) return "VOTER005";


        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT voter_id FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("voter_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check if voter has already voted (for any office).
     */
    public static boolean hasVoterVoted(String voterId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT 1 FROM votes WHERE voter_id = ? LIMIT 1";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, voterId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get votes already cast by a voter for a specific office.
     */
    public static Set<String> getVoterVotesForOffice(String voterId, String office) {
        Set<String> votes = new HashSet<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT candidate FROM votes WHERE voter_id = ? AND office = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, voterId);
            stmt.setString(2, office);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String cand = rs.getString("candidate");
                if (cand != null) votes.add(cand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return votes;
    }

    /**
     * Get vote counts for a specific office.
     */
    public static Map<String, Integer> getVoteCounts(String office) {
        Map<String, Integer> counts = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT candidate, COUNT(*) AS cnt FROM votes WHERE office = ? AND candidate IS NOT NULL GROUP BY candidate";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, office);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                counts.put(rs.getString("candidate"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return counts;
    }

    /**
     * Record votes for an office. Returns a message: "OK" or error description.
     */
    public static String recordVotesForOffice(String voterId, String office, Set<String> selectedCandidates) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Delete old votes for this office by this voter
            String deleteQuery = "DELETE FROM votes WHERE voter_id = ? AND office = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setString(1, voterId);
            deleteStmt.setString(2, office);
            deleteStmt.executeUpdate();

            // Insert new votes
            String insertQuery = "INSERT INTO votes (voter_id, office, candidate) VALUES (?, ?, ?)";
            for (String candidate : selectedCandidates) {
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, voterId);
                insertStmt.setString(2, office);
                insertStmt.setString(3, candidate);
                insertStmt.executeUpdate();
            }
            return "OK";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}