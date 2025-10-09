

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class VotingForm extends JFrame {
    private TwoPhaseCommit tpc; // safe vote writing
    private MixNet mixNet; 
    private JComboBox<String> candidateBox;
    private JButton castVoteButton, backButton, summaryButton;
    private JLabel welcomeLabel;
    private String voterUsername;
  
    // constructor for the voting form
    private TransactionalVotingSystem votingSystem;
    public VotingForm(String username, TransactionalVotingSystem votingSystem) {
    this.votingSystem = votingSystem;
    votingSystem.mainFrame.setVisible(false);
    tpc = new TwoPhaseCommit(); // (2pc) reads existing votes
    try {
         mixNet = new MixNet();   // prepares anonymizer
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error initializing MixNet: " + ex.getMessage());
    }
        this.voterUsername = username;

        setTitle("Cast Your Vote");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // HEADER ( for the blue outline thingy )
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(66, 133, 244));
        JLabel titleLabel = new JLabel("Online Voting System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        
       // ===== MAIN CONTENT =====
JPanel centerPanel = new JPanel();
centerPanel.setLayout(new GridBagLayout());
centerPanel.setBackground(Color.WHITE);
GridBagConstraints gbc = new GridBagConstraints();
gbc.insets = new Insets(10, 10, 10, 10);
gbc.gridx = 0;
gbc.gridwidth = 2;
gbc.anchor = GridBagConstraints.NORTH;  // Align to top

// WELCOME LABEL
gbc.gridy = 0;
gbc.weighty = 0;  // No vertical stretching
welcomeLabel = new JLabel("Welcome, " + username + "!");
welcomeLabel.setFont(new Font("Arial", Font.BOLD, 35));
welcomeLabel.setForeground(new Color(44, 62, 80));
centerPanel.add(welcomeLabel, gbc);

// Candidate selection
gbc.gridy++;
JLabel selectLabel = new JLabel("Select your candidate:");
selectLabel.setFont(new Font("Arial", Font.PLAIN, 20));
gbc.insets = new Insets(30, 10, 10, 10); // top padding
centerPanel.add(selectLabel, gbc);

// Candidate dropdown
gbc.gridy++;
String[] candidates = votingSystem.candidates.stream()
    .map(c -> c.name)
    .toArray(String[]::new);
candidateBox = new JComboBox<>(candidates);
candidateBox.setPreferredSize(new Dimension(250, 30));
centerPanel.add(candidateBox, gbc);

// Cast vote button
gbc.gridy++;
castVoteButton = new JButton("Cast Vote");
castVoteButton.setPreferredSize(new Dimension(180, 35));
centerPanel.add(castVoteButton, gbc);

// Vote summary button
gbc.gridy++;
summaryButton = new JButton("Show Vote Summary");
summaryButton.setPreferredSize(new Dimension(180, 35));
centerPanel.add(summaryButton, gbc);

// Back button
gbc.gridy++;
backButton = new JButton("Back to Home");
backButton.setPreferredSize(new Dimension(180, 35));
centerPanel.add(backButton, gbc);

// FILLER to push content to top
gbc.gridy++;
gbc.weighty = 1;  
JPanel filler = new JPanel();
filler.setBackground(Color.WHITE);
centerPanel.add(filler, gbc);

//CONSOLE FEED PANEL 
gbc.gridy++;
gbc.weighty = 0;
JTextArea consoleArea = new JTextArea(5, 50);
consoleArea.setEditable(false);
consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
JScrollPane consoleScroll = new JScrollPane(consoleArea);
centerPanel.add(consoleScroll, gbc);

add(centerPanel, BorderLayout.CENTER);

        // Action listener for castVote na Button
        
       castVoteButton.addActionListener(e -> {
    String selectedCandidate = (String) candidateBox.getSelectedItem();

    try {
        // Encrypt voter ID using MixNet
        String encryptedVoter = mixNet.anonymizeVotes(Collections.singletonList(voterUsername)).get(0);
        System.out.println("Encrypted voter: " + encryptedVoter);

        // ✅ Store the ENCRYPTED username in votes.csv
        boolean success = tpc.castVote(encryptedVoter, selectedCandidate);

        if (success) {
            // ✅ Update in-memory status using REAL username
            votingSystem.registeredVoters.get(voterUsername).hasVoted = true;
            castVoteButton.setEnabled(false);
            candidateBox.setEnabled(false);
            JOptionPane.showMessageDialog(null, "Vote successfully cast for " + selectedCandidate + "!");
        } else {
            JOptionPane.showMessageDialog(null, "Error: You have already voted or cannot vote.");
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Encryption error: " + ex.getMessage());
    }
});


        summaryButton.addActionListener(e -> showVoteSummary());
        

        backButton.addActionListener(e -> {
            dispose();
             votingSystem.mainFrame.setVisible(true);
        });
        
    }

    private void showVoteSummary() {
    File file = new File("votes.csv");
    if (!file.exists()) {
        JOptionPane.showMessageDialog(null, "No votes recorded yet.");
        return;
    }

  Map<String, Integer> counts = new HashMap<>();
for (TransactionalVotingSystem.Candidate c : votingSystem.candidates) {
    counts.put(c.name, 0);
}

try (Scanner sc = new Scanner(new File("votes.csv"))) {
    while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        if (line.isEmpty()) continue;
        String[] data = line.split(",");
        if (data.length < 2) continue;
        String candidate = data[1].trim();
        counts.put(candidate, counts.getOrDefault(candidate, 0) + 1);
    }
} catch (Exception ex) {
    JOptionPane.showMessageDialog(null, "Error reading votes.csv: " + ex.getMessage());
}

StringBuilder summary = new StringBuilder("<html><body>");
for (Map.Entry<String, Integer> entry : counts.entrySet()) {
    summary.append(entry.getKey())
           .append(": ")
           .append(entry.getValue())
           .append(" votes<br>");
}
summary.append("</body></html>");

JOptionPane.showMessageDialog(null, summary.toString(), "Vote Summary", JOptionPane.INFORMATION_MESSAGE);
}
}


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.*;
import java.util.*;

@SuppressWarnings("unused")
public class VotingForm extends JFrame {
    private TwoPhaseCommit tpc; // safe vote writing
    private MixNet mixNet; 
    private JComboBox<String> candidateBox;
    private JButton castVoteButton, backButton, summaryButton;
    private JLabel welcomeLabel;
    private String voterUsername;
  
    // constructor for the voting form
    private TransactionalVotingSystem votingSystem;
    public VotingForm(String username, TransactionalVotingSystem votingSystem) {
    this.votingSystem = votingSystem;
    tpc = new TwoPhaseCommit(); // (2pc) reads existing votes
    try {
         mixNet = new MixNet();   // prepares anonymizer
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error initializing MixNet: " + ex.getMessage());
    }
        votingSystem.mainFrame.setVisible(true);
        this.voterUsername = username;

        setTitle("Cast Your Vote");
        setSize(550, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // HEADER 
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(66, 133, 244));
        JLabel titleLabel = new JLabel("Online Voting System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // ===== MAIN CONTENT =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(welcomeLabel, gbc);

        // Candidate selection
        gbc.gridy++;
        centerPanel.add(new JLabel("Select your candidate:"), gbc);
   
        // Candidate dropdown
        gbc.gridy++;
        String[] candidates = votingSystem.candidates.stream()
        .map(c -> c.name)
        .toArray(String[]::new);
        candidateBox = new JComboBox<>(candidates);
        candidateBox.setPreferredSize(new Dimension(250, 30));
        centerPanel.add(candidateBox, gbc);

        // Cast vote button
        gbc.gridy++;
        castVoteButton = new JButton("Cast Vote");
        castVoteButton.setPreferredSize(new Dimension(150, 35));
        centerPanel.add(castVoteButton, gbc);
        
        // Disable voting if already voted
     if (votingSystem.registeredVoters.containsKey(voterUsername) &&
    votingSystem.registeredVoters.get(voterUsername).hasVoted) {
    castVoteButton.setEnabled(false);
    candidateBox.setEnabled(false);
}
        // Vote summary button
        gbc.gridy++;
        summaryButton = new JButton("Show Vote Summary");
        summaryButton.setPreferredSize(new Dimension(180, 35));
        centerPanel.add(summaryButton, gbc);

        // Back button
        gbc.gridy++;
        backButton = new JButton("Back to Home");
        backButton.setPreferredSize(new Dimension(150, 35));
        centerPanel.add(backButton, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // ===== ACTIONS =====
        
        castVoteButton.addActionListener(e -> {
            String selectedCandidate = (String) candidateBox.getSelectedItem();
            // uses 2pc to safely write vote
            boolean success = tpc.castVote(voterUsername, selectedCandidate);

            if (success) {
            votingSystem.registeredVoters.get(voterUsername).hasVoted = true;
            castVoteButton.setEnabled(false);
            candidateBox.setEnabled(false);
            JOptionPane.showMessageDialog(null, "Vote successfully cast for " + selectedCandidate + "!");
            } else {
            JOptionPane.showMessageDialog(null, "Error: You have already voted or cannot vote.");
            }
        });

        summaryButton.addActionListener(e -> showVoteSummary());
        

        backButton.addActionListener(e -> {
            dispose();
            new cce_final_proj.OnlineVotingSystem(votingSystem).setVisible(true);
        });
        
    }

    private void showVoteSummary() {
    File file = new File("votes.csv");
    if (!file.exists()) {
        JOptionPane.showMessageDialog(null, "No votes recorded yet.");
        return;
    }

  Map<String, Integer> counts = new HashMap<>();
for (TransactionalVotingSystem.Candidate c : votingSystem.candidates) {
    counts.put(c.name, 0);
}

try (Scanner sc = new Scanner(new File("votes.csv"))) {
    while (sc.hasNextLine()) {
        String line = sc.nextLine().trim();
        if (line.isEmpty()) continue;
        String[] data = line.split(",");
        if (data.length < 2) continue;
        String candidate = data[1].trim();
        counts.put(candidate, counts.getOrDefault(candidate, 0) + 1);
    }
} catch (Exception ex) {
    JOptionPane.showMessageDialog(null, "Error reading votes.csv: " + ex.getMessage());
}

StringBuilder summary = new StringBuilder("<html><body>");
for (Map.Entry<String, Integer> entry : counts.entrySet()) {
    summary.append(entry.getKey())
           .append(": ")
           .append(entry.getValue())
           .append(" votes<br>");
}
summary.append("</body></html>");

JOptionPane.showMessageDialog(null, summary.toString(), "Vote Summary", JOptionPane.INFORMATION_MESSAGE);
}
}
