
import java.io.*;
import java.util.*;

public class TwoPhaseCommit {

    private String voteFile = "votes.csv";
    private Set<String> votedUsers = new HashSet<>();

    public TwoPhaseCommit() {
        loadVotedUsers();
    }

    private void loadVotedUsers() {
        try (Scanner sc = new Scanner(new File(voteFile))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length > 0) {
                        votedUsers.add(parts[0]); // store voter ID
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // file may not exist yet, ignore
        }
    }

    public boolean castVote(String voterID, String candidate) {
        if (!prepare(voterID)) {
            System.out.println("Vote preparation failed. Voter already voted.");
            return false;
        }

        if (commit(voterID, candidate)) {
            System.out.println("Vote successfully committed!");
            return true;
        } else {
            rollback(voterID);
            System.out.println("Vote rolled back due to an error.");
            return false;
        }
    }

    private boolean prepare(String voterID) {
        // Phase 1: Check if voter has already voted
        return !votedUsers.contains(voterID);
    }

    private boolean commit(String voterID, String candidate) {
    try (FileWriter fw = new FileWriter(voteFile, true)) {
        String timestamp = new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(new Date());
        fw.write(voterID + "," + candidate + "," + timestamp + "\n");
        votedUsers.add(voterID);
        return true;
    } catch (IOException e) {
        return false;
    }
}

    private void rollback(String voterID) {
        // Optional rollback mechanism (e.g., remove invalid writes)
    }
}
// redundant - but just keep(too lazy to change all references)
