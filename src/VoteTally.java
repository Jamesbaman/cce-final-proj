<<<<<<< HEAD:src/VoteTally.java
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class VoteTally extends JFrame {

    private TransactionalVotingSystem votingSystem;
    private JPanel mainPanel;
    

    public VoteTally(TransactionalVotingSystem votingSystem) {
        this.votingSystem = votingSystem;
        setTitle("Vote Tally");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smooth scroll
        add(scrollPane, BorderLayout.CENTER);

        buildVoteTally();

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(ev -> {
        mainPanel.removeAll();
        votingSystem.loadVotesFromFile(); // reload votes from CSV
        buildVoteTally();
    });
        add(refreshBtn, BorderLayout.NORTH);
    }

    private void buildVoteTally() {
        List<TransactionalVotingSystem.Candidate> candidates = votingSystem.candidates;

        if (candidates.isEmpty()) {
            JLabel noCandidates = new JLabel("No candidates found.");
            noCandidates.setFont(new Font("Arial", Font.BOLD, 16));
            noCandidates.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(noCandidates);
            return;
        }

        // Count votes per candidate
        Map<String, Integer> voteCounts = votingSystem.ballotLedger.stream()
                .collect(java.util.stream.Collectors.groupingBy(b -> b.candidate, java.util.stream.Collectors.summingInt(b -> 1)));

        int maxVotes = voteCounts.values().stream().max(Integer::compareTo).orElse(1);
int maxBarWidth = 500; // maximum width of any bar

for (TransactionalVotingSystem.Candidate c : candidates) {
    JPanel rowPanel = new JPanel(new BorderLayout(10, 10));
    rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
    rowPanel.setBackground(Color.LIGHT_GRAY);

    // Candidate photo
    ImageIcon icon;
if (c.imagePath != null && !c.imagePath.isEmpty()) {
    Image img = new ImageIcon(c.imagePath).getImage();

    // scale image while keeping aspect ratio
    int width = 100;
    int height = (int) ((double) img.getHeight(null) / img.getWidth(null) * width);
    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    icon = new ImageIcon(scaledImg);

} else {

    Image img = new ImageIcon("default.png").getImage();
    int width = 100;
    int height = (int) ((double) img.getHeight(null) / img.getWidth(null) * width);
    Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    icon = new ImageIcon(scaledImg);
}
    JLabel photoLabel = new JLabel(icon);
    photoLabel.setVerticalAlignment(SwingConstants.CENTER); // center vertically
    photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    rowPanel.add(photoLabel, BorderLayout.WEST);

    // Candidate name
    JLabel nameLabel = new JLabel(c.name);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
    nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    rowPanel.add(nameLabel, BorderLayout.CENTER);

    // Votes
    int votes = voteCounts.getOrDefault(c.name, 0);
    double ratio = votes / (double) maxVotes;
    int barWidth = (int) (ratio * maxBarWidth);

    // Container panel for bar
    JPanel barContainer = new JPanel();
    barContainer.setBackground(Color.WHITE);
    barContainer.setPreferredSize(new Dimension(maxBarWidth, 35));
    barContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

    // Bar itself
    JPanel voteBar = new JPanel();
    voteBar.setBackground(new Color(66, 133, 244));
    voteBar.setPreferredSize(new Dimension(barWidth, 35));

    JLabel voteLabel = new JLabel(votes + " vote(s)");
    voteLabel.setForeground(Color.BLACK);
    voteLabel.setFont(new Font("Arial", Font.BOLD, 14));
    voteBar.add(voteLabel);

    barContainer.add(voteBar);

    rowPanel.add(barContainer, BorderLayout.SOUTH);
    mainPanel.add(rowPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
}

        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
=======
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;


public class VoteTally extends JFrame {

    private TransactionalVotingSystem votingSystem;
    private JPanel mainPanel;
    

    public VoteTally(TransactionalVotingSystem votingSystem) {
        this.votingSystem = votingSystem;
        setTitle("Vote Tally");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smooth scroll
        add(scrollPane, BorderLayout.CENTER);

        buildVoteTally();

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(ev -> {
        mainPanel.removeAll();
        votingSystem.loadVotesFromFile(); // reload votes from CSV
        buildVoteTally();
    });
        add(refreshBtn, BorderLayout.NORTH);
    }

    private void buildVoteTally() {
        List<TransactionalVotingSystem.Candidate> candidates = votingSystem.candidates;

        if (candidates.isEmpty()) {
            JLabel noCandidates = new JLabel("No candidates found.");
            noCandidates.setFont(new Font("Arial", Font.BOLD, 16));
            noCandidates.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(noCandidates);
            return;
        }

        // Count votes per candidate
        Map<String, Integer> voteCounts = votingSystem.ballotLedger.stream()
                .collect(java.util.stream.Collectors.groupingBy(b -> b.candidate, java.util.stream.Collectors.summingInt(b -> 1)));

        int maxVotes = voteCounts.values().stream().max(Integer::compareTo).orElse(1);
int maxBarWidth = 500; // maximum width of any bar

for (TransactionalVotingSystem.Candidate c : candidates) {
    JPanel rowPanel = new JPanel(new BorderLayout(10, 10));
    rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
    rowPanel.setBackground(Color.LIGHT_GRAY);

    // Candidate photo
    ImageIcon icon;
    int photoWidth = 120;
    int photoHeight = 120;

    if (c.imagePath != null && !c.imagePath.isEmpty()) {
        icon = getScaledImage(c.imagePath, photoWidth, photoHeight);
    } else {
        icon = getScaledImage("default.png", photoWidth, photoHeight);
    }

    JLabel photoLabel = new JLabel(icon);
    photoLabel.setPreferredSize(new Dimension(photoWidth, photoHeight));
    photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    photoLabel.setVerticalAlignment(SwingConstants.CENTER);
    rowPanel.add(photoLabel, BorderLayout.WEST);

    // Candidate name
    JLabel nameLabel = new JLabel(c.name);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
    nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    rowPanel.add(nameLabel, BorderLayout.CENTER);

    // Votes
    int votes = voteCounts.getOrDefault(c.name, 0);
    double ratio = votes / (double) maxVotes;
    int barWidth = (int) (ratio * maxBarWidth);

    // Container panel for bar
    JPanel barContainer = new JPanel();
    barContainer.setBackground(Color.WHITE);
    barContainer.setPreferredSize(new Dimension(maxBarWidth, 35));
    barContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

    // Bar itself
    JPanel voteBar = new JPanel();
    voteBar.setBackground(new Color(66, 133, 244));
    voteBar.setPreferredSize(new Dimension(barWidth, 35));

    JLabel voteLabel = new JLabel(votes + " vote(s)");
    voteLabel.setForeground(Color.BLACK);
    voteLabel.setFont(new Font("Arial", Font.BOLD, 14));
    voteBar.add(voteLabel);

    barContainer.add(voteBar);

    rowPanel.add(barContainer, BorderLayout.SOUTH);
    mainPanel.add(rowPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
}

        mainPanel.revalidate();
        mainPanel.repaint();
    }

private ImageIcon getScaledImage(String path, int maxWidth, int maxHeight) {
    try {
        ImageIcon originalIcon = new ImageIcon(path);
        Image originalImage = originalIcon.getImage();

        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);

        if (originalWidth <= 0 || originalHeight <= 0) {
            return new ImageIcon("default.png");
        }

        
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
        return new ImageIcon("default.png");
    }
}

}
>>>>>>> pr-5:VoteTally.java
