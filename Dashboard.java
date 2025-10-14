import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("unused")
public class Dashboard extends JFrame {

    private TransactionalVotingSystem votingSystem;
    private String username;

    public Dashboard(String username, TransactionalVotingSystem votingSystem) {
        this.username = username;
        this.votingSystem = votingSystem;

        setTitle("Voting Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));

        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(66, 133, 244));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Welcome to the Online Voting System!", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("WELCOME, " + username + "!", SwingConstants.RIGHT);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ---------- MAIN CONTENT ----------
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel dashboardLabel = new JLabel("Select an action below");
        dashboardLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        dashboardLabel.setForeground(new Color(44, 62, 80));
        gbc.gridy = 0;
        mainPanel.add(dashboardLabel, gbc);

        // ---------- BUTTON PANEL ----------
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        buttonPanel.setBackground(Color.WHITE);

        JButton voteButton = createStyledButton("Cast Your Vote", e -> openVotingForm());
        JButton tallyButton = createStyledButton("View Vote Tally", e -> openVoteTally());
        JButton summaryButton = createStyledButton("Show Vote Summary", e -> openSummary());
        JButton logoutButton = createStyledButton("Log Out", e -> logout());

        buttonPanel.add(voteButton);
        buttonPanel.add(tallyButton);
        buttonPanel.add(summaryButton);
        buttonPanel.add(logoutButton);

        gbc.gridy = 1;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    // ---------- Helper to create consistent button design ----------
    private JButton createStyledButton(String text, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(66, 133, 244));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 98, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(66, 133, 244));
            }
        });
        return btn;
    }

    // ---------- Button Actions ----------
    private void openVotingForm() {
        dispose();
        new VotingForm(username, votingSystem).setVisible(true);
    }

    private void openVoteTally() {
        new VoteTally(votingSystem).setVisible(true);
    }

    private void openSummary() {
        VotingForm temp = new VotingForm(username, votingSystem);
        temp.showVoteSummary();
    }

    private void logout() {
        dispose();
        new Loginform(votingSystem).setVisible(true);
    }
}
