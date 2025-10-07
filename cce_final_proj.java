//ignore sa ang package
    
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;

public class cce_final_proj {

  
    // MAIN FRAME
    
    public static class OnlineVotingSystem extends JFrame {
        private JPanel sidebarPanel, headerPanel, mainContentPanel;
        private JButton homeButton, signInButton, registerButton, devButton;
        private JLabel welcomeLabel, forgotPasswordLabel, profileLabel;
        // Create transactional voting object
        private 
TransactionalVotingSystem votingSystem = new 
TransactionalVotingSystem();
        // constructor
        public OnlineVotingSystem(
TransactionalVotingSystem votingSystem) {
            this.votingSystem = votingSystem;
            votingSystem.mainFrame = this; // pass reference
            setTitle("Online Voting System");
            setSize(1080, 600);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            initializeComponents();
            setupLayout();
            configureComponents();
        }

        private void initializeComponents() {
            sidebarPanel = new JPanel();
            headerPanel = new JPanel();
            mainContentPanel = new JPanel();

            homeButton = new JButton("Home Page");
            signInButton = new JButton("Sign in");
            registerButton = new JButton("Register");
            devButton = new JButton("Admin Panel");

            welcomeLabel = new JLabel("WELCOME!");
            forgotPasswordLabel = new JLabel("Forgot password?");
            profileLabel = new JLabel("PROFILE ▼");
        }

        private JPanel homePanel;
        
        private void setupLayout() {
            setLayout(new BorderLayout());

            // Sidebar
            sidebarPanel.setLayout(new GridLayout(5, 1, 10, 10));
            sidebarPanel.setBackground(Color.WHITE);
            sidebarPanel.add(homeButton);
            sidebarPanel.add(new JButton("Cast Vote"));
            sidebarPanel.add(new JButton("TBF"));
            sidebarPanel.add(new JButton("TBF"));
            sidebarPanel.add(devButton);
            add(sidebarPanel, BorderLayout.WEST);

            // Header
            headerPanel.setLayout(new BorderLayout());
            headerPanel.setBackground(new Color(66, 133, 244));
            JLabel titleLabel = new JLabel("Online Voting System");
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            headerPanel.add(titleLabel, BorderLayout.WEST);

            profileLabel.setForeground(Color.WHITE);
            profileLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            profileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            headerPanel.add(profileLabel, BorderLayout.EAST);

            add(headerPanel, BorderLayout.NORTH);

            // Main content area
        homePanel = new JPanel(new GridBagLayout());
        homePanel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(15, 10, 15, 10);

            // Welcome label
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 72));
            gbc.gridy = 0;
            homePanel.add(welcomeLabel, gbc);

            // Description text
            JLabel descriptionLabel = new JLabel("<html><div style='text-align: center; width:600px;'>"
                    + "This site provides a secure and transparent way to cast votes. "
                    + "Each vote is recorded as a transaction in a digital ledger."
                    + "</div></html>");
            descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            gbc.gridy = 1;
            homePanel.add(descriptionLabel, gbc);

            // Sign in button
            signInButton.setPreferredSize(new Dimension(200, 40));
            gbc.gridy = 2;
            homePanel.add(signInButton, gbc);

            // Register button
            registerButton.setPreferredSize(new Dimension(200, 40));
            gbc.gridy = 3;
            homePanel.add(registerButton, gbc);

            // Forgot password link
            forgotPasswordLabel.setForeground(Color.RED);
            forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            gbc.gridy = 4;
            homePanel.add(forgotPasswordLabel, gbc);

            // Wrap homePanel inside mainContentPanel
            mainContentPanel.setLayout(new BorderLayout());
            mainContentPanel.add(homePanel, BorderLayout.CENTER);
            add(mainContentPanel, BorderLayout.CENTER);
                    }

        private void configureComponents() {
            registerButton.addActionListener(e -> {
                RegisterForm registerForm = new RegisterForm();
                registerForm.setVisible(true);
            });
        
            signInButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        Loginform loginForm = new Loginform(OnlineVotingSystem.this.votingSystem);
        loginForm.setVisible(true);
    }
});
// admin panel access (pass kay 123)
devButton.addActionListener(e -> {
    String pswd = JOptionPane.showInputDialog("Enter admin password:");
    if ("123".equals(pswd)) { // simple password check
    votingSystem.loadCandidatesFromFile();
    votingSystem.loadRegisteredUsersFromFile();
    votingSystem.loadVotesFromFile();
    showAdminPanel(); // method to display panel
    } else {
        JOptionPane.showMessageDialog(null, "Incorrect password!");
    }
});

        }
    
    // admin panel
   private void showAdminPanel() {
    JDialog dialog = new JDialog(this, "Admin Panel", true);
    dialog.setSize(900, 600);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout(10, 10));

  
    // Hash function for voter ID (used only in vote ledger)
    java.util.function.Function<String, String> hashVoterId = voterId -> {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(voterId.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 8); // only first 8 chars
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "ERROR";
        }
    };

    // Voter status table 
   
    String[] voterCols = {"Voter Username", "Has Voted"};
    Object[][] voterData = new Object[votingSystem.registeredVoters.size()][2];
    int i = 0;
    for (Map.Entry<String, 
Voter> entry : votingSystem.registeredVoters.entrySet()) {
        voterData[i][0] = entry.getKey();           // actual username
        voterData[i][1] = entry.getValue().hasVoted; // true/false
        i++;
    }
    JTable voterTable = new JTable(voterData, voterCols);

    
    // Ballot ledger table (hashed voter ID + candidate + timestamp)
   
    String[] ballotCols = {"Voter ID (Hashed)", "Candidate", "Timestamp"};
    Object[][] ballotData = new Object[votingSystem.ballotLedger.size()][3];
    i = 0;
    for (
Ballot b : votingSystem.ballotLedger) {
        ballotData[i][0] = hashVoterId.apply(b.voterId); // hashed ID
        ballotData[i][1] = b.candidate;
        ballotData[i][2] = b.timestamp.toString();
        i++;
    }
    JTable ballotTable = new JTable(ballotData, ballotCols);

  
    // Add both tables to a panel
   
    JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    tablesPanel.add(new JScrollPane(voterTable));
    tablesPanel.add(new JScrollPane(ballotTable));

    dialog.add(tablesPanel, BorderLayout.CENTER);

    
    // Add Candidate Panel
    
    JPanel addCandidatePanel = new JPanel(new FlowLayout());
    JTextField candidateNameField = new JTextField(15);
    JButton selectImageButton = new JButton("Select Image");
    JLabel selectedImageLabel = new JLabel("No image selected");
    JButton addCandidateButton = new JButton("Add Candidate");

    addCandidatePanel.add(new JLabel("Candidate Name:"));
    addCandidatePanel.add(candidateNameField);
    addCandidatePanel.add(selectImageButton);
    addCandidatePanel.add(selectedImageLabel);
    addCandidatePanel.add(addCandidateButton);

    dialog.add(addCandidatePanel, BorderLayout.NORTH);

    // Image selection
    final String[] imagePath = {null};
    selectImageButton.addActionListener(ev -> {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(dialog);
        if (option == JFileChooser.APPROVE_OPTION) {
            imagePath[0] = fileChooser.getSelectedFile().getAbsolutePath();
            selectedImageLabel.setText(fileChooser.getSelectedFile().getName());
        }
    });

    // Add candidate action
    addCandidateButton.addActionListener(ev -> {
        String name = candidateNameField.getText().trim();
        if (name.isEmpty() || imagePath[0] == null) {
            JOptionPane.showMessageDialog(dialog, "Please enter a name and select an image.");
            return;
        }
        votingSystem.addCandidate(name, imagePath[0]);
        votingSystem.saveCandidatesToFile();
        JOptionPane.showMessageDialog(dialog, "Candidate added: " + name);
        candidateNameField.setText("");
        selectedImageLabel.setText("No image selected");
    });

    // Close button
    JButton closeBtn = new JButton("Close");
    closeBtn.addActionListener(ev -> dialog.dispose());
    dialog.add(closeBtn, BorderLayout.SOUTH);

    dialog.setVisible(true);
}

        // Classes for transactional voting system
        public static void main(String[] args) {
            System.out.println("Current working dir: " + new File(".").getAbsolutePath());
            SwingUtilities.invokeLater(() -> {
                
            TransactionalVotingSystem votingSystem = new 
            TransactionalVotingSystem();
                votingSystem.loadCandidatesFromFile();
                votingSystem.loadVotesFromFile(); 
                OnlineVotingSystem frame = new OnlineVotingSystem(votingSystem);
                frame.setVisible(true);
            });
        }
    }

  
    // Register Form
    public static class RegisterForm extends JFrame {
        private JTextField usernameField, emailField, firstNameField, lastNameField;
        private JPasswordField passwordField, confirmPasswordField;
        

        public RegisterForm() {
            setTitle("Register");
            setSize(600, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            getContentPane().setBackground(new Color(220, 220, 220));
            setLayout(new BorderLayout());

            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(new Color(220, 220, 220));
            JLabel titleLabel = new JLabel("REGISTER");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
            titlePanel.add(titleLabel);
            add(titlePanel, BorderLayout.NORTH);

            JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
            formPanel.setBackground(new Color(220, 220, 220));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

            usernameField = new JTextField();
            emailField = new JTextField();
            firstNameField = new JTextField();
            lastNameField = new JTextField();
            passwordField = new JPasswordField();
            confirmPasswordField = new JPasswordField();

            formPanel.add(new JLabel("USERNAME"));
            formPanel.add(usernameField);
            formPanel.add(new JLabel("EMAIL"));
            formPanel.add(emailField);
            formPanel.add(new JLabel("FIRST NAME"));
            formPanel.add(firstNameField);
            formPanel.add(new JLabel("LAST NAME"));
            formPanel.add(lastNameField);
            formPanel.add(new JLabel("CREATE PASSWORD"));
            formPanel.add(passwordField);
            formPanel.add(new JLabel("RE-ENTER PASSWORD"));
            formPanel.add(confirmPasswordField);

            JButton submitButton = new JButton("Submit");
            JButton backButton = new JButton("Back");
            formPanel.add(backButton);
            formPanel.add(submitButton);
            add(formPanel, BorderLayout.CENTER);

            submitButton.addActionListener(e -> {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (username.isEmpty() || email.isEmpty() || firstName.isEmpty() ||
                        lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                 File usersFile = new File("users.csv");
                boolean usernameExists = false;
             if (usersFile.exists()) {
             try (Scanner sc = new Scanner(usersFile)) {
                while (sc.hasNextLine()) {
                String[] data = sc.nextLine().split(",");
             if (data.length > 0 && data[0].trim().equalsIgnoreCase(username)) {
                usernameExists = true;
                break;
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading users.csv: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        }

    if (usernameExists) {
        JOptionPane.showMessageDialog(this, "Username already exists! Choose another.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

                try (FileWriter fw = new FileWriter("users.csv", true)) {
                    fw.write(username + "," + email + "," + firstName + "," + lastName + "," + password + "\n");
                    JOptionPane.showMessageDialog(this, "Registration successful! Data saved.");
                    dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving user data!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            backButton.addActionListener(e -> dispose());
        }
    }
    
    // Transactional Voting Classes
    
    static class Voter {
        String voterId;
        boolean hasVoted;

        public Voter(String voterId) {
            this.voterId = voterId;
            this.hasVoted = false;
        }
    }

    static class Ballot {
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
    static class Candidate {
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
    static class TransactionalVotingSystem {
        public final Map<String, Voter> registeredVoters = new HashMap<>(); // Registered voters
        public final List<Ballot> ballotLedger = new ArrayList<>(); // Ledger of all ballots
        public final List<Candidate> candidates = new ArrayList<>(); // List of candidates
        public JFrame mainFrame; // Main frame reference


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
        public void loadCandidatesFromFile() {
        File file = new File("candidates.csv");
             if (!file.exists()) return;

            candidates.clear();

             try (Scanner sc = new Scanner(file)) {
               while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",", 2); // only 2 fields: name and imagePath
            if (parts.length < 2) continue;

            candidates.add(new Candidate(parts[0].trim(), parts[1].trim()));
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error loading candidates: " + e.getMessage());
    }
}

        // Load candidates from file
        public void saveCandidatesToFile() {
    try (FileWriter fw = new FileWriter("candidates.csv")) {
        for (Candidate c : candidates) {
            fw.write(c.name + "," + c.imagePath + "\n");
     }
     } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error saving candidates: " + e.getMessage());
      }
        }

        
        // register voter
        public void registerVoter(String voterId) {
            registeredVoters.put(voterId, new Voter(voterId));
        }
        
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
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            try (FileWriter fw = new FileWriter("votes.csv", true)) {
             fw.write(voterId + "," + candidate + "," + ballot.timestamp + "\n");
             } catch (IOException e) {

         JOptionPane.showMessageDialog(null, "Error saving vote: " + e.getMessage());
         return false;
     }
            JOptionPane.showMessageDialog(null, "Vote successfully cast for " + candidate + "!");
            return true;
        }
         // For Admin to add a candidate
    public void addCandidate(String name, String imagePath) {
        candidates.add(new Candidate(name, imagePath));
    }
        public void showLedger() {
            System.out.println("\nTransaction Ledger:");
            for (Ballot ballot : this.ballotLedger) {
                System.out.println(ballot);
            }
        }
        public void loadVotesFromFile() {
    File file = new File("votes.csv");
    if (!file.exists()) return;

    ballotLedger.clear(); // avoids duplicates

    try (Scanner sc = new Scanner(file)) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] data = line.split(",", 3);
            if (data.length < 2) continue; // only require voterId and candidate

            String voterId = data[0].trim();
            String candidate = data[1].trim();
            Date timestamp = null;

            // Parse timestamp only if it exists
            if (data.length == 3 && !data[2].trim().isEmpty()) {
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    timestamp = sdf.parse(data[2].trim());
                } catch (Exception e) {
                    timestamp = null;
                }
            }

            // Create and add ballot
            Ballot ballot = new Ballot(voterId, candidate);
            ballot.timestamp = (timestamp != null) ? timestamp : new Date(); // fallback to now
            ballotLedger.add(ballot);

            // Mark voter as voted
            registeredVoters.putIfAbsent(voterId, new Voter(voterId));
            registeredVoters.get(voterId).hasVoted = true;
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error loading votes.csv: " + e.getMessage());
    }
}



    }
}
   
