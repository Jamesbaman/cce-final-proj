// ignore sa ang package

import javax.swing.*;
import java.awt.*;
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
        private JButton signInButton, registerButton, devButton, voteTallyButton;
        private JLabel welcomeLabel, forgotPasswordLabel, profileLabel;
        private TransactionalVotingSystem votingSystem;

        public OnlineVotingSystem(TransactionalVotingSystem votingSystem) {
            this.votingSystem = votingSystem;
            votingSystem.mainFrame = this;

            setTitle("Online Voting System");
            setSize(1200, 800);
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

            // Sidebar
            sidebarPanel.setLayout(new GridLayout(5, 1, 10, 10));
            sidebarPanel.setBackground(Color.WHITE);
            sidebarPanel.add(new JButton("Cast Vote"));
            sidebarPanel.add(voteTallyButton);
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

            // Main content
            homePanel = new JPanel(new GridBagLayout());
            homePanel.setBackground(Color.LIGHT_GRAY);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(15, 10, 15, 10);

            // Welcome text
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 72));
            gbc.gridy = 0;
            homePanel.add(welcomeLabel, gbc);

            // Description
            JLabel descriptionLabel = new JLabel("<html><div style='text-align: center; width:600px;'>"
                    + "This site ensures a secure and transparent way to cast votes. "
                    + "Each vote is recorded as a transaction in a digital ledger protected using a Mixnet-based Algorithm."
                    + "</div></html>");
            descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            gbc.gridy = 1;
            homePanel.add(descriptionLabel, gbc);

            // Buttons
            signInButton.setPreferredSize(new Dimension(400, 60));
            gbc.gridy = 2;
            homePanel.add(signInButton, gbc);

            registerButton.setPreferredSize(new Dimension(400, 60));
            gbc.gridy = 3;
            homePanel.add(registerButton, gbc);

            forgotPasswordLabel.setForeground(Color.RED);
            forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            gbc.gridy = 4;
            homePanel.add(forgotPasswordLabel, gbc);

            mainContentPanel.setLayout(new BorderLayout());
            mainContentPanel.add(homePanel, BorderLayout.CENTER);
            add(mainContentPanel, BorderLayout.CENTER);
        }

        private void configureComponents() {
            registerButton.addActionListener(e -> {
                RegisterForm registerForm = new RegisterForm();
                registerForm.setVisible(true);
            });

            signInButton.addActionListener(e -> {
                Loginform loginForm = new Loginform(votingSystem);
                loginForm.setVisible(true);
            });

            voteTallyButton.addActionListener(e -> {
                VoteTally tallyFrame = new VoteTally(votingSystem);
                tallyFrame.setVisible(true);
            });

            // Admin access
            devButton.addActionListener(e -> {
                String pswd = JOptionPane.showInputDialog("Enter admin password:");
                if ("123".equals(pswd)) {
                    votingSystem.loadCandidatesFromFile();
                    votingSystem.loadRegisteredUsersFromFile();
                    votingSystem.loadVotesFromFile();
                    showAdminPanel();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect password!");
                }
            });
        }

        private void showAdminPanel() {
            JDialog dialog = new JDialog(this, "Admin Panel", true);
            dialog.setSize(900, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));

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

            // Ballot ledger table
            String[] ballotCols = {"Voter ID (Hashed)", "Candidate", "Timestamp"};
            Object[][] ballotData = new Object[votingSystem.ballotLedger.size()][3];
            i = 0;
            for (TransactionalVotingSystem.Ballot b : votingSystem.ballotLedger) {
                ballotData[i][0] = b.voterId;
                ballotData[i][1] = b.candidate;
                ballotData[i][2] = b.timestamp.toString();
                i++;
            }
            JTable ballotTable = new JTable(ballotData, ballotCols);

            JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            tablesPanel.add(new JScrollPane(voterTable));
            tablesPanel.add(new JScrollPane(ballotTable));
            dialog.add(tablesPanel, BorderLayout.CENTER);

            // Add candidate section
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

            final String[] imagePath = {null};
            selectImageButton.addActionListener(ev -> {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showOpenDialog(dialog);
                if (option == JFileChooser.APPROVE_OPTION) {
                    imagePath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                    selectedImageLabel.setText(fileChooser.getSelectedFile().getName());
                }
            });

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

            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(ev -> dialog.dispose());
            dialog.add(closeBtn, BorderLayout.SOUTH);

            dialog.setVisible(true);
        }

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

            JButton backButton = new JButton("Back");
            JButton submitButton = new JButton("Submit");
            formPanel.add(backButton);
            formPanel.add(submitButton);
            add(formPanel, BorderLayout.CENTER);

            submitButton.addActionListener(e -> handleRegistration());
            backButton.addActionListener(e -> dispose());
        }

        private void handleRegistration() {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || email.isEmpty() || firstName.isEmpty()
                    || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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
        }
    }
}
