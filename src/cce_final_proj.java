package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;

public class cce_final_proj {

    public static class OnlineVotingSystem extends JFrame {
        private JPanel sidebarPanel, headerPanel, mainContentPanel, homePanel;
        private JLabel welcomeLabel, profileLabel;
        private JButton signInButton, registerButton, devButton, voteTallyButton, doweeButton;
        private TransactionalVotingSystem votingSystem;

        class GradientPanel extends JPanel {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth(), height = getHeight();
                Color color1 = new Color(56, 239, 125);
                Color color2 = new Color(203, 195, 227);
                GradientPaint gp = new GradientPaint(0, 0, color1, width, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(width / 3, height / 4, width / 2, height / 2);
            }
        }

        public OnlineVotingSystem(TransactionalVotingSystem votingSystem) {
            this.votingSystem = votingSystem;
            votingSystem.mainFrame = this;
            setTitle("Online Voting System");
            setSize(1200, 700);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            initComponents();
            layoutComponents();
            configureActions();
        }

        private void initComponents() {
            sidebarPanel = new JPanel();
            headerPanel = new JPanel();
            mainContentPanel = new JPanel();
            signInButton = new JButton("Sign in");
            registerButton = new JButton("Register");
            devButton = new JButton("Admin Panel");
            doweeButton = new JButton("Software Use");
            voteTallyButton = new JButton("Vote Tally");
            welcomeLabel = new JLabel("WELCOME!");
            profileLabel = new JLabel("PROFILE ▼");
        }

        private void layoutComponents() {
            setLayout(new BorderLayout());

            sidebarPanel.setLayout(new GridLayout(3, 1, 10, 10));
            sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
            sidebarPanel.setBackground(new Color(65, 237, 131));
            sidebarPanel.add(voteTallyButton);
            sidebarPanel.add(devButton);
            sidebarPanel.add(doweeButton);
            add(sidebarPanel, BorderLayout.WEST);

            headerPanel = new GradientHeaderPanel("Online Voting System", new Font("Inter", Font.BOLD, 25));
            add(headerPanel, BorderLayout.NORTH);

            homePanel = new GradientPanel();
            homePanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets(15, 10, 15, 10);

            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
            gbc.gridy = 0;
            homePanel.add(welcomeLabel, gbc);

            JLabel descriptionLabel = new JLabel("<html><div style='text-align: center; width:600px;'>"
                    + "This site ensures a secure and transparent way to cast votes. "
                    + "Each vote is recorded as a transaction in a digital ledger protected using a Mix-Net based Algorithm."
                    + "</div></html>");
            descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            gbc.gridy = 1;
            homePanel.add(descriptionLabel, gbc);

            signInButton.setPreferredSize(new Dimension(400, 60));
            gbc.gridy = 2;
            homePanel.add(signInButton, gbc);

            registerButton.setPreferredSize(new Dimension(400, 60));
            gbc.gridy = 3;
            homePanel.add(registerButton, gbc);

            mainContentPanel.setLayout(new BorderLayout());
            mainContentPanel.add(homePanel, BorderLayout.CENTER);
            add(mainContentPanel, BorderLayout.CENTER);
        }

        private void configureActions() {
            registerButton.addActionListener(e -> new RegisterForm().setVisible(true));

            signInButton.addActionListener(e -> {
                Loginform loginForm = new Loginform(votingSystem);
                loginForm.setVisible(true);
            });

            doweeButton.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "The Transactional Ballot Casting Voting System is a secure e-voting platform ensuring accuracy,\n"
                            + "transparency, and voter anonymity. It combines the Two-Phase Commit protocol with a Mix-Net–\n"
                            + "Based Voting Algorithm for privacy and reliability.",
                    "Software", JOptionPane.INFORMATION_MESSAGE));

            voteTallyButton.addActionListener(e -> new VoteTally(votingSystem).setVisible(true));

            devButton.addActionListener(e -> {
                String pswd = JOptionPane.showInputDialog("Enter admin password:");
                if ("123".equals(pswd)) {
                    votingSystem.loadCandidatesFromFile();
                    votingSystem.loadRegisteredUsersFromFile();
                    votingSystem.loadVotesFromFile();
                    showAdminPanel();
                    dispose();
                } else JOptionPane.showMessageDialog(null, "Incorrect password!");
            });
        }

        private void showAdminPanel() {
            JDialog dialog = new JDialog(this, "Admin Panel", true);
            dialog.setSize(900, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));

            java.util.function.Function<String, String> hashVoterId = voterId -> {
                try {
                    java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(voterId.getBytes());
                    StringBuilder hex = new StringBuilder();
                    for (byte b : hash) {
                        String h = Integer.toHexString(0xff & b);
                        if (h.length() == 1) hex.append('0');
                        hex.append(h);
                    }
                    return hex.toString().substring(0, 8);
                } catch (Exception e) {
                    return "ERROR";
                }
            };

            String[] voterCols = {"Voter Username", "Has Voted"};
            Object[][] voterData = new Object[votingSystem.registeredVoters.size()][2];
            int i = 0;
            for (Map.Entry<String, TransactionalVotingSystem.Voter> entry : votingSystem.registeredVoters.entrySet()) {
                voterData[i][0] = entry.getKey();
                voterData[i][1] = entry.getValue().hasVoted;
                i++;
            }
            JTable voterTable = new JTable(voterData, voterCols);

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

            JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            tablesPanel.add(new JScrollPane(voterTable));
            tablesPanel.add(new JScrollPane(ballotTable));
            dialog.add(tablesPanel, BorderLayout.CENTER);

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
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    imagePath[0] = fc.getSelectedFile().getAbsolutePath();
                    selectedImageLabel.setText(fc.getSelectedFile().getName());
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
            SwingUtilities.invokeLater(() -> {
                TransactionalVotingSystem votingSystem = new TransactionalVotingSystem();
                votingSystem.loadCandidatesFromFile();
                votingSystem.loadVotesFromFile();
                new OnlineVotingSystem(votingSystem).setVisible(true);
            });
        }
    }

    static class GradientHeaderPanel extends JPanel {
        private final String title;
        private final Font font;

        public GradientHeaderPanel(String title, Font font) {
            this.title = title;
            this.font = font;
            setPreferredSize(new Dimension(0, 60));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, new Color(0x40, 0xed, 0x82), w, h, new Color(0xa3, 0xce, 0xc7));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int x = 20, y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawString(title, x + 2, y + 2);
            g2d.setColor(Color.BLACK);
            g2d.drawString(title, x, y);
            g2d.dispose();
        }
    }

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

            submitButton.addActionListener(e -> handleSubmit());
            backButton.addActionListener(e -> dispose());
        }

        private void handleSubmit() {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File usersFile = new File("users.csv");
            boolean exists = false;
            if (usersFile.exists()) {
                try (Scanner sc = new Scanner(usersFile)) {
                    while (sc.hasNextLine()) {
                        String[] data = sc.nextLine().split(",");
                        if (data.length > 0 && data[0].equalsIgnoreCase(username)) {
                            exists = true;
                            break;
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading users.csv: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            if (exists) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (FileWriter fw = new FileWriter("users.csv", true)) {
                fw.write(username + "," + email + "," + firstName + "," + lastName + "," + password + "\n");
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving user data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
