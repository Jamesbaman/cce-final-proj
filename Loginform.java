

import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.io.*;


public class Loginform extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    private TransactionalVotingSystem votingSystem;

// constructor for the login form
    public Loginform(TransactionalVotingSystem votingSystem) {
        this.votingSystem = votingSystem;
        setTitle("Sign In");
        setSize(400, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout()); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // padding lol
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        // Row 1 - Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(usernameLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);

        // Row 2 - Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);

        // Row 3 - Login button (centered)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        // Action listener
        loginButton.addActionListener(e -> performLogin());
        getRootPane().setDefaultButton(loginButton);
    }
    // performs the login by checking if the fields are empty and then calling the authenticate method
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (authenticate(username, password)) {
        // Ensure the voter exists in memory
        votingSystem.registeredVoters.putIfAbsent(username, new TransactionalVotingSystem.Voter(username));
        // Update hasVoted status from votes.csv
        updateVoterStatusFromVotes();

   JOptionPane.showMessageDialog(this, "Login successful!");
        dispose();
         new VotingForm(username, votingSystem).setVisible(true); 
          } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    private boolean authenticate(String username, String password) {
    File usersFile = new File("users.csv");
    if (!usersFile.exists()) {
        JOptionPane.showMessageDialog(this, "users.csv file not found!", "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    boolean authenticated = false;

    try (Scanner sc = new Scanner(usersFile)) {
        while (sc.hasNextLine()) {
            String[] data = sc.nextLine().split(",");
            if (data.length >= 5 && data[0].trim().equals(username) && data[4].trim().equals(password)) {
                authenticated = true;

                // Populate registeredVoters map for all users
                do {
                    String userId = data[0].trim();
                    if (!votingSystem.registeredVoters.containsKey(userId)) {
                        votingSystem.registerVoter(userId);
                    } else {
                        votingSystem.registeredVoters.get(userId).hasVoted = false; // reset before reading votes
                    }
                } while (sc.hasNextLine() && (data = sc.nextLine().split(",")) != null);

                break;
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error reading users.csv: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    if (!authenticated) return false;

    // Update hasVoted based on votes.csv
    File votesFile = new File("votes.csv");
    if (votesFile.exists()) {
        try (Scanner sc = new Scanner(votesFile)) {
            while (sc.hasNextLine()) {
                String[] voteData = sc.nextLine().split(",");
                if (voteData.length >= 2) {
                    String voterId = voteData[0].trim();
                    if (votingSystem.registeredVoters.containsKey(voterId)) {
                        votingSystem.registeredVoters.get(voterId).hasVoted = true;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading votes.csv: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    return true;
}
    private void updateVoterStatusFromVotes() {
    File file = new File("votes.csv");
    if (!file.exists()) return;

    try (Scanner sc = new Scanner(file)) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue; // skip empty lines
            String[] data = line.split(",");
            if (data.length >= 2) {
                String voterId = data[0].trim();
                TransactionalVotingSystem.Voter voter = votingSystem.registeredVoters.get(voterId);
                if (voter != null) {
                    voter.hasVoted = true;
                } else {
                    voter = new TransactionalVotingSystem.Voter(voterId);
                    voter.hasVoted = true;
                    votingSystem.registeredVoters.put(voterId, voter);
                }   
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error reading votes.csv: " + e.getMessage());
    }
}

   public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        TransactionalVotingSystem votingSystem = new TransactionalVotingSystem();
        new Loginform(votingSystem).setVisible(true);
    });
}
}