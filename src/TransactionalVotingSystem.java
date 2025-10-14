package src;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class TransactionalVotingSystem {
    public static class Voter {
        String voterId;
        boolean hasVoted;

        public Voter(String voterId) {
            this.voterId = voterId;
            this.hasVoted = false;
        }
    }

    public static class Ballot {
        String voterId;
        String candidate;
        Date timestamp;

        public Ballot(String voterId, String candidate) {
            this.voterId = voterId;
            this.candidate = candidate;
            this.timestamp = new Date();
        }

        @Override
        public String toString() {
            return "Ballot{" +
                    "voterId='" + voterId + '\'' +
                    ", candidate='" + candidate + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
    // Candidate class
    public static class Candidate {
    String name;
    String imagePath; // store path to image

    public Candidate(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return name; // Shows the name
    }
}
    public final Map<String, Voter> registeredVoters = new HashMap<>();
    public final List<Ballot> ballotLedger = new ArrayList<>();
    public final List<Candidate> candidates = new ArrayList<>();
    public JFrame mainFrame;

    // Load registered users
    public void loadRegisteredUsersFromFile() {
        File usersFile = new File("users.csv");
        if (!usersFile.exists()) return;

        try (Scanner sc = new Scanner(usersFile)) {
            while (sc.hasNextLine()) {
                String[] data = sc.nextLine().split(",");
                if (data.length >= 1) {
                    String username = data[0].trim();
                    registeredVoters.putIfAbsent(username, new Voter(username));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading users.csv: " + e.getMessage());
        }
    }

    // Load candidates
    public void loadCandidatesFromFile() {
        File file = new File("candidates.csv");
        if (!file.exists()) return;

        candidates.clear();

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                candidates.add(new Candidate(parts[0].trim(), parts[1].trim()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading candidates: " + e.getMessage());
        }
    }

    public void saveCandidatesToFile() {
        try (FileWriter fw = new FileWriter("candidates.csv")) {
            for (Candidate c : candidates) {
                fw.write(c.name + "," + c.imagePath + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving candidates: " + e.getMessage());
        }
    }

    // Register voter
    public void registerVoter(String voterId) {
        registeredVoters.put(voterId, new Voter(voterId));
    }

    // Cast vote
    public boolean castVote(String voterId, String candidate) {
        Voter voter = registeredVoters.get(voterId);
        if (voter == null) {
            JOptionPane.showMessageDialog(null, "Invalid voter ID!");
            return false;
        }
        if (voter.hasVoted) {
            JOptionPane.showMessageDialog(null, "This voter has already voted!");
            return false;
        }

        Ballot ballot = new Ballot(voterId, candidate);
        this.ballotLedger.add(ballot);
        voter.hasVoted = true;
        try (FileWriter fw = new FileWriter("votes.csv", true)) {
            fw.write(voterId + "," + candidate + "," + ballot.timestamp + "\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving vote: " + e.getMessage());
            return false;
        }
        JOptionPane.showMessageDialog(null, "Vote successfully cast for " + candidate + "!");
        return true;
    }

    // Admin adds candidate
    public void addCandidate(String name, String imagePath) {
        candidates.add(new Candidate(name, imagePath));
    }

    public void loadVotesFromFile() {
        File file = new File("votes.csv");
        if (!file.exists()) return;

        ballotLedger.clear();

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] data = line.split(",", 3);
                if (data.length < 2) continue;

                String voterId = data[0].trim();
                String candidate = data[1].trim();
                Date timestamp = null;

                if (data.length == 3 && !data[2].trim().isEmpty()) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                        timestamp = sdf.parse(data[2].trim());
                    } catch (Exception e) {
                        timestamp = null;
                    }
                }

                Ballot ballot = new Ballot(voterId, candidate);
                ballot.timestamp = (timestamp != null) ? timestamp : new Date();
                ballotLedger.add(ballot);

                registeredVoters.putIfAbsent(voterId, new Voter(voterId));
                registeredVoters.get(voterId).hasVoted = true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading votes.csv: " + e.getMessage());
        }
    }
    // ====== SHOW SUMMARY PANEL ======
    public void showSummaryPanel() {
        if (mainFrame == null) return;

        JPanel summaryPanel = new JPanel(new BorderLayout());
        JLabel summaryLabel = new JLabel("Vote Summary", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 28));
        summaryPanel.add(summaryLabel, BorderLayout.NORTH);

        // Build table of candidates and vote counts
        String[] cols = {"Candidate", "Votes"};
        Object[][] data = new Object[candidates.size()][2];
        for (int i = 0; i < candidates.size(); i++) {
            data[i][0] = candidates.get(i).name;
            data[i][1] = countVotesFor(candidates.get(i).name);
        }

        JTable table = new JTable(data, cols);
        summaryPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Replace the current content of the main frame
        Container cp = mainFrame.getContentPane();
        cp.removeAll();
        cp.add(summaryPanel, BorderLayout.CENTER);
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }

    // Helper method to count votes for each candidate
    public int countVotesFor(String candidateName) {
        int count = 0;
        for (Ballot b : ballotLedger) {
            if (b.candidate.equals(candidateName)) count++;
        }
        return count;
    }
    // inside TransactionalVotingSystem.java, near the bottom
    public boolean validateLogin(String username, String password) {
        File file = new File("users.csv");
        if (!file.exists()) return false;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] data = sc.nextLine().split(",");
                if (data.length >= 2) {
                    String user = data[0].trim();
                    String pass = data[1].trim();
                    if (user.equals(username) && pass.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error reading users.csv: " + e.getMessage());
        }
        return false;
    }

    public boolean hasUserVoted(String username) {
        Voter voter = registeredVoters.get(username);
        return voter != null && voter.hasVoted;
    }

}

