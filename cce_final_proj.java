//ignore sa ang package

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class cce_final_proj {

    // MAIN FRAME
    public static class OnlineVotingSystem extends JFrame {
        private JPanel sidebarPanel, headerPanel, mainContentPanel;
        private JButton homeButton, signInButton, registerButton, devButton, voteTallyButton;  
        private JLabel welcomeLabel, forgotPasswordLabel, profileLabel;
        // Create transactional voting object
        private TransactionalVotingSystem votingSystem;
        // constructor
        public OnlineVotingSystem(TransactionalVotingSystem votingSystem) {
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

            homeButton = new JButton("Terms & Services"); // ← This will now show T&S
            signInButton = new JButton("Sign in");
            registerButton = new JButton("Register");
            devButton = new JButton("Admin Panel");
            voteTallyButton = new JButton("Vote Tally");

            welcomeLabel = new JLabel("WELCOME!");
            forgotPasswordLabel = new JLabel("Forgot password?");
            profileLabel = new JLabel("PROFILE ▼");
        }

        private JPanel homePanel;
        
        private void setupLayout() {
            setLayout(new BorderLayout());

            // === SIDEBAR ===
            sidebarPanel.setLayout(new GridLayout(5, 1, 0, 5)); // Keep GridLayout for uniform button sizes
            sidebarPanel.setBackground(Color.WHITE);
            sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5)); 

            // Style all buttons to be same size
            for (JButton btn : new JButton[]{homeButton, voteTallyButton, devButton}) {
                btn.setPreferredSize(new Dimension(120, 40));
                btn.setFont(new Font("Arial", Font.PLAIN, 12));
                btn.setHorizontalAlignment(SwingConstants.CENTER);
                btn.setBorder(BorderFactory.createRaisedBevelBorder());
            }

            // Create the About Us button with same styling
            JButton aboutUsButton = new JButton("About Us");
            aboutUsButton.setPreferredSize(new Dimension(120, 40));
            aboutUsButton.setFont(new Font("Arial", Font.PLAIN, 12));
            aboutUsButton.setHorizontalAlignment(SwingConstants.CENTER);
            aboutUsButton.setBorder(BorderFactory.createRaisedBevelBorder());

            // Create a dummy panel to act as spacer (empty space before Admin)
            JPanel spacer = new JPanel();
            spacer.setOpaque(false); // Transparent
            spacer.setPreferredSize(new Dimension(120, 80)); // Height = empty space

            // Add buttons + spacer
            sidebarPanel.add(homeButton);
            sidebarPanel.add(aboutUsButton); // ← Replaced "Cast Vote" with styled "About Us"
            sidebarPanel.add(voteTallyButton);
            sidebarPanel.add(spacer); // ← Empty space here
            sidebarPanel.add(devButton); // Admin Panel at bottom

            add(sidebarPanel, BorderLayout.WEST);
            
            // === HEADER ===
            headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(66, 133, 244));
            headerPanel.setPreferredSize(new Dimension(0, 45));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

            JLabel titleLabel = new JLabel("Online Voting System");
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
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
            // === Terms & Services Button ===
            homeButton.addActionListener(e -> {
                String termsText = "<html><div style='padding: 15px; line-height: 1.5; max-width: 600px;'>"
                    + "<h2>Terms and Services</h2>"
                    + "<p><b>Last Updated:</b> June 2024</p>"
                    + "<p>By using this Online Voting System, you agree to the following terms:</p>"
                    + "<ul>"
                    + "<li>You must be a registered and verified voter to participate.</li>"
                    + "<li>Each voter is allowed to cast <b>only one vote</b> per election cycle.</li>"
                    + "<li>Attempting to manipulate, duplicate, or falsify votes is strictly prohibited and may result in disqualification.</li>"
                    + "<li>All votes are recorded immutably in a transactional ledger for audit purposes.</li>"
                    + "<li>Your personal data (name, email, username) is stored securely and will not be shared with third parties.</li>"
                    + "<li>The system administrators reserve the right to suspend accounts violating these terms.</li>"
                    + "<li>This platform is for educational and demonstration purposes only.</li>"
                    + "</ul>"
                    + "<p>Use of this system constitutes your acceptance of these terms.</p>"
                    + "</div></html>";

                JOptionPane.showMessageDialog(
                    OnlineVotingSystem.this,
                    new JScrollPane(new JLabel(termsText)),
                    "Terms and Services",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });

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

            voteTallyButton.addActionListener(e -> {
                VoteTally tallyFrame = new VoteTally(votingSystem);
                tallyFrame.setVisible(true);
            });

            // admin panel access (pass kay 123) — STILL WORKS!
            devButton.addActionListener(e -> {
                String pswd = JOptionPane.showInputDialog("Enter admin password:");
                if ("123".equals(pswd)) {
                    votingSystem.loadCandidatesFromFile();
                    votingSystem.loadRegisteredUsersFromFile();
                    votingSystem.loadVotesFromFile();
                    showAdminPanel();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect password!");
                }
            });

            // ✅ Handle About Us button (now the 2nd button in sidebar)
            JButton aboutUsButton = (JButton) sidebarPanel.getComponent(1); // Index 1 = About Us
            aboutUsButton.addActionListener(e -> {
                String aboutText = "<html><div style='padding: 15px; line-height: 1.5;'>"
                    + "<h2>About Our Online Voting System</h2>"
                    + "<p>This secure and transparent digital voting platform was developed to ensure fair, "
                    + "tamper-proof elections using a transactional ledger model.</p>"
                    + "<p>Every vote is recorded as an immutable entry, protecting voter privacy while maintaining "
                    + "auditability through cryptographic hashing.</p>"
                    + "<p>Developed with Java Swing and file-based persistence for educational purposes in "
                    + "CCE Final Project 2024.</p>"
                    + "<p><i>\"Democracy powered by technology.\"</i></p>"
                    + "</div></html>";

                JOptionPane.showMessageDialog(
                    OnlineVotingSystem.this,
                    aboutText,
                    "About Us",
                    JOptionPane.INFORMATION_MESSAGE
                );
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
                    return hexString.toString().substring(0, 8);
                } catch (java.security.NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return "ERROR";
                }
            };

            // Voter status table 
            String[] voterCols = {"Voter Username", "Has Voted"};
            Object[][] voterData = new Object[votingSystem.registeredVoters.size()][2];
            int i = 0;
            for (Map.Entry<String, TransactionalVotingSystem.Voter> entry : votingSystem.registeredVoters.entrySet()) {
                voterData[i][0] = entry.getKey();
                voterData[i][1] = entry.getValue().hasVoted;
                i++;
            }
            JTable voterTable = new JTable(voterData, voterCols);

            // Ballot ledger table (hashed voter ID + candidate + timestamp)
            String[] ballotCols = {"Voter ID (Hashed)", "Candidate", "Timestamp"};
            Object[][] ballotData = new Object[votingSystem.ballotLedger.size()][3];
            i = 0;
            for (TransactionalVotingSystem.Ballot b : votingSystem.ballotLedger) {
                ballotData[i][0] = hashVoterId.apply(b.voterId);
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
                TransactionalVotingSystem votingSystem = new TransactionalVotingSystem();
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
}
