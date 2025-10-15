import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

@SuppressWarnings("unused")
public class VotingForm extends JFrame {
    private TwoPhaseCommit tpc; // safe vote writing
    private MixNet mixNet;
    private JComboBox<String> candidateBox; // hidden, for compatibility
    private JButton castVoteButton, backButton, backdashButton, summaryButton;
    private JLabel welcomeLabel;
    private String voterUsername;
    private TransactionalVotingSystem votingSystem;

    // new: track selected candidate
    private String selectedCandidate = null;
    private JPanel candidatesContainer; // where candidate cards live
    
    public VotingForm(String username, TransactionalVotingSystem votingSystem   ) {
        this.votingSystem = votingSystem;
        votingSystem.mainFrame.setVisible(false);

        tpc = new TwoPhaseCommit(); // (2PC) reads existing votes

        
        setTitle("Cast Your Vote");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout(10,10));
        headerPanel.setBackground(new Color(100, 100, 244));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel titleLabel = new JLabel("Online Voting System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // simple small hint on the right (quickie tip)
        JLabel hint = new JLabel("Select a candidate card and click Cast Vote");
        hint.setForeground(Color.WHITE);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        headerPanel.add(hint, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN CONTENT --- //
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setBackground(Color.white);

        // Top: welcome + small controls
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(44, 62, 80));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        // keep old dropdown for compatibility but hide it (so other code still finds it)
        String[] candidateNames = votingSystem.candidates.stream().map(c -> c.name).toArray(String[]::new);
        candidateBox = new JComboBox<>(candidateNames);
        candidateBox.setVisible(false); // not shown, but still available
        topPanel.add(candidateBox, BorderLayout.EAST);

        centerPanel.add(topPanel, BorderLayout.NORTH);

        // Center left: candidate cards (scrollable)
        candidatesContainer = new JPanel();
        candidatesContainer.setBackground(Color.WHITE);
        candidatesContainer.setLayout(new BoxLayout(candidatesContainer, BoxLayout.Y_AXIS)); // changed

        JScrollPane candidateScroll = new JScrollPane(candidatesContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        candidateScroll.getVerticalScrollBar().setUnitIncrement(16);
        candidateScroll.setPreferredSize(new Dimension(640, 420));
        candidateScroll.setBorder(BorderFactory.createTitledBorder("Candidates"));

        // Right side: status panel + log
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BorderLayout(8, 8));
        rightPanel.setPreferredSize(new Dimension(320, 0));

        // Status panel
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 6, 6));
        statusPanel.setBorder(BorderFactory.createTitledBorder("System Status"));
        statusPanel.setBackground(Color.WHITE);

        JLabel lastActionLabel = new JLabel("Last action: —");
        JLabel encryptTimeLabel = new JLabel("Last Encrypt Time: N/A");
        JLabel decryptTimeLabel = new JLabel("Last Decrypt Time: N/A");
        JLabel selectedLabel = new JLabel("Selected: —");

        lastActionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        encryptTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        decryptTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        selectedLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        statusPanel.add(lastActionLabel);
        statusPanel.add(selectedLabel);
        statusPanel.add(encryptTimeLabel);
        statusPanel.add(decryptTimeLabel);

        rightPanel.add(statusPanel, BorderLayout.NORTH);

        // Log area (compact)
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("System Feed (log)"));
        logScroll.setPreferredSize(new Dimension(300, 300));
        rightPanel.add(logScroll, BorderLayout.CENTER);

        // Bottom of right panel: action buttons (duplicate of bottom so user can act without scrolling)
        JPanel rightButtons = new JPanel(new GridLayout(3, 1, 8, 8));
        rightButtons.setBackground(Color.WHITE);
        JButton castButtonSmall = new JButton("Cast Vote");
        JButton summaryButtonSmall = new JButton("Show Vote Summary");
        JButton backButtonSmall = new JButton("Back to Dashboard");

        stylePrimaryButton(castButtonSmall);
        stylePrimaryButton(summaryButtonSmall);
        styleDangerButton(backButtonSmall);

        rightButtons.add(castButtonSmall);
        rightButtons.add(summaryButtonSmall);
        rightButtons.add(backButtonSmall);
        rightPanel.add(rightButtons, BorderLayout.SOUTH);

        // Compose center: left and right
        centerPanel.add(candidateScroll, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);

        // Bottom: big action bar (keeps your original buttons visible)
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 12));
        bottomBar.setBackground(Color.WHITE);

        castVoteButton = new JButton("Cast Vote");
        summaryButton = new JButton("Summary");
        backButton = new JButton("Back to Home");

        stylePrimaryButton(castVoteButton);
        stylePrimaryButton(summaryButton);
        styleDangerButton(backButton);

        bottomBar.add(castVoteButton);
        bottomBar.add(summaryButton);
        bottomBar.add(backButton);

        add(centerPanel, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);

        // Initialize MixNet with log and labels (so it can update performance labels)
        try {
            // MixNet constructor we used previously expected consoleArea etc.
            // We'll pass logArea and the encrypt/decrypt label references
            mixNet = new MixNet(logArea, encryptTimeLabel, decryptTimeLabel);
        } catch (Exception ex) {
            mixNet = null;
            logArea.append("[WARN] MixNet unavailable: " + ex.getMessage() + "\n");
        }

        this.voterUsername = username;

        // Build candidate cards on-screen
        rebuildCandidateCards(selectedLabel, logArea);



       
        castVoteButton.addActionListener(e -> {
            if (selectedCandidate == null) {
                JOptionPane.showMessageDialog(null, "Please select a candidate first.");
                return;
            }
            // reuse logic but update status labels/log
            if (!votingSystem.registeredVoters.containsKey(voterUsername)) {
                JOptionPane.showMessageDialog(null, "Error: voter not registered in memory.");
                return;
            }
            if (votingSystem.registeredVoters.get(voterUsername).hasVoted) {
                JOptionPane.showMessageDialog(null, "Error: You have already voted.");
                return;
            }

            try {
                List<String> encryptedList;
                if (mixNet != null) {
                    encryptedList = mixNet.anonymizeVotes(Collections.singletonList(voterUsername));
                } else {
                    // fall back: store plaintext (not recommended for real system)
                    encryptedList = Collections.singletonList(voterUsername);
                }
                String encryptedVoter = encryptedList.get(0);

                boolean success = tpc.castVote(encryptedVoter, selectedCandidate);

                if (success) {
                    votingSystem.registeredVoters.get(voterUsername).hasVoted = true;
                    castVoteButton.setEnabled(false);
                    castButtonSmall.setEnabled(false); // small button too
                    // visually mark selected card as voted (refresh)
                    rebuildCandidateCards(selectedLabel, logArea);

                    String msg = "\u2713 Vote successfully cast for " + selectedCandidate + "!";
                    JOptionPane.showMessageDialog(null, msg);
                    lastActionLabel.setText("Last action: Cast vote for " + selectedCandidate);
                    logArea.append("[INFO] " + msg + "\n");
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Cannot cast vote.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Encryption error: " + ex.getMessage());
                logArea.append("[ERROR] Encryption error: " + ex.getMessage() + "\n");
            }
        });

        // duplicate small button behavior
        castButtonSmall.addActionListener(e -> castVoteButton.doClick());


        summaryButton.addActionListener(e -> showVoteSummary());
        summaryButtonSmall.addActionListener(e -> showVoteSummary());

        // back
        backButton.addActionListener(e -> {
            dispose();
            votingSystem.mainFrame.setVisible(true);
        });

        backButtonSmall.addActionListener(e -> {
             dispose(); 
        new Dashboard(voterUsername, votingSystem).setVisible(true);
        });

       
        
        // selection update from candidateBox kept for backwards compatibility
        candidateBox.addActionListener(e -> {
            String sel = (String) candidateBox.getSelectedItem();
            if (sel != null) {
                selectedCandidate = sel;
                // update selection label if needed
                // but we primarily rely on card clicks
            }
        });

        
        if (votingSystem.registeredVoters.containsKey(voterUsername) &&
                votingSystem.registeredVoters.get(voterUsername).hasVoted) {
            castVoteButton.setEnabled(false);
            castButtonSmall.setEnabled(false);
        }
    }

    private void rebuildCandidateCards(JLabel selectedLabel, JTextArea logArea) {
    candidatesContainer.removeAll();

    int cardWidth = 600;
    int cardHeight = 180;

    for (TransactionalVotingSystem.Candidate c : votingSystem.candidates) {
        JPanel card = new JPanel(new BorderLayout(15, 10)); // spacing between image and info
        card.setPreferredSize(new Dimension(cardWidth, cardHeight));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // LEFT SIDE: Candidate Image 
        ImageIcon icon = getScaledImage(c.imagePath, 160, 160);
        JLabel imgLabel = new JLabel(icon);
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(imgLabel, BorderLayout.WEST);

        //  RIGHT SIDE: Candidate Info
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(c.name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // ✅ larger font
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

        JButton selectBtn = new JButton("Select");
        stylePrimarySmall(selectBtn);
        selectBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectBtn.setPreferredSize(new Dimension(120, 35));
        selectBtn.setMaximumSize(new Dimension(120, 35));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(selectBtn);
        infoPanel.add(Box.createVerticalGlue());

        card.add(infoPanel, BorderLayout.CENTER);

        // If voter already voted, gray out ze cards
        if (votingSystem.registeredVoters.getOrDefault(voterUsername, new TransactionalVotingSystem.Voter(voterUsername)).hasVoted) {
            card.setEnabled(false);
            selectBtn.setEnabled(false);
            card.setBorder(BorderFactory.createLineBorder(new Color(210,210,210), 1));
        }

        // Action listeners
        ActionListener selectAction = ev -> {
            selectedCandidate = c.name;
            selectedLabel.setText("Selected: " + selectedCandidate);
            highlightSelectedCard(card);
            logArea.append("[UI] Selected candidate: " + selectedCandidate + "\n");
        };

        selectBtn.addActionListener(selectAction);
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectAction.actionPerformed(null);
            }
        });

        candidatesContainer.add(card);
        candidatesContainer.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    candidatesContainer.revalidate();
    candidatesContainer.repaint();
}

    // highlights the clicked card by changing its border, resets others
    private void highlightSelectedCard(JPanel chosen) {
        for (Component comp : candidatesContainer.getComponents()) {
            if (!(comp instanceof JPanel)) continue;
            JPanel p = (JPanel) comp;
            if (p == chosen) {
                p.setBorder(BorderFactory.createLineBorder(new Color(66, 133, 244), 3));
            } else {
                p.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
            }
        }
    }
    // Reads votes.csv and shows a summary dialog
    public void showVoteSummary() {
        File file = new File("votes.csv");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "No votes recorded yet.");
            return;
        }

        Map<String, Integer> counts = new HashMap<>();
        for (TransactionalVotingSystem.Candidate c : votingSystem.candidates) {
            counts.put(c.name, 0);
        }

        try (Scanner sc = new Scanner(file)) {
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

   
    private void stylePrimaryButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(66, 133, 244));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(160, 48));
    }

    private void stylePrimarySmall(JButton b) {
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(66, 133, 244));
        b.setFocusPainted(false);
    }

    private void styleDangerButton(JButton b) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(219, 68, 55));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(160, 48));
    }

    // ----------------- Image scaling helper (keeps images uncropped, high quality) -----------------
    private ImageIcon getScaledImage(String path, int maxWidth, int maxHeight) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                // fallback default
                return defaultIcon(maxWidth, maxHeight);
            }
            ImageIcon originalIcon = new ImageIcon(path);
            Image originalImage = originalIcon.getImage();

            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);

            if (originalWidth <= 0 || originalHeight <= 0) return defaultIcon(maxWidth, maxHeight);

            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double scale = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * scale);
            int newHeight = (int) (originalHeight * scale);

            BufferedImage resizedImage = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = (maxWidth - newWidth) / 2;
            int y = (maxHeight - newHeight) / 2;

            g2d.drawImage(originalImage, x, y, newWidth, newHeight, null);
            g2d.dispose();

            return new ImageIcon(resizedImage);
        } catch (Exception e) {
            return defaultIcon(maxWidth, maxHeight);
        }
    }

    private ImageIcon defaultIcon(int w, int h) {
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(new Color(230, 230, 230));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(170, 170, 170));
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        FontMetrics fm = g.getFontMetrics();
        String text = "No Image";
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, tx, ty);
        g.dispose();
        return new ImageIcon(bi);
    }
}
