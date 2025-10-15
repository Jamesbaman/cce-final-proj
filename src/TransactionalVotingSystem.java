import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


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

    // Cast vote castVote()
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

    // Try to create MixNet so we can decrypt stored encrypted voter IDs.
    MixNet mix = null;
    try {
        mix = new MixNet();
    } catch (Exception e) {
        // If MixNet can't be initialized, we'll treat stored IDs as plain text.
        mix = null;
    }

    try (Scanner sc = new Scanner(file)) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] data = line.split(",", 3);
            if (data.length < 2) continue;

            String storedId = data[0].trim(); // encrypted ID stored in votes.csv (or plain old username)
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

            // Try to decrypt the storedId into the real username. If decryption fails,
            // assume storedId is already the real username (backwards-compatible).
            String realVoter = storedId;
            if (mix != null) {
                try {
                    List<String> dec = mix.decryptVotes(Collections.singletonList(storedId));
                    if (dec != null && !dec.isEmpty() && dec.get(0) != null && !dec.get(0).isEmpty()) {
                        realVoter = dec.get(0);
                    }
                } catch (Exception ex) {
                    // not decryptable -> keep storedId as-is (plain username or hashed form)
                    realVoter = storedId;
                }
            }

            // Use the real username in-memory (do NOT change votes.csv)
            Ballot ballot = new Ballot(realVoter, candidate);
            ballot.timestamp = (timestamp != null) ? timestamp : new Date();
            ballotLedger.add(ballot);

            // Ensure the real user exists in registeredVoters, then mark as voted.
            for (Map.Entry<String, Voter> entry : registeredVoters.entrySet()) {
    if (entry.getKey().equals(realVoter)) {
        entry.getValue().hasVoted = true;
        break;
    }
}
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error loading votes.csv: " + e.getMessage());
    }
}
        public java.util.function.Function<String, String> hashVoterId = voterId -> {
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
}

