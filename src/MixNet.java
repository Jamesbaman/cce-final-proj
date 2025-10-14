package src;
// thank god for these packages
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class MixNet {
    private SecretKey secretKey;

    // GUI components (optional)
    private JTextArea consoleArea = null;
    private JLabel encryptLabel = null;
    private JLabel decryptLabel = null;

    // --- Old constructor (no GUI, backward compatible) ---
    public MixNet() throws Exception {
        byte[] keyBytes = "Secretivekeykeys".getBytes(); // 16 chars = 128-bit key
        secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    // --- New constructor for GUI integration ---
    public MixNet(JTextArea consoleArea, JLabel encryptLabel, JLabel decryptLabel) throws Exception {
        this.consoleArea = consoleArea;
        this.encryptLabel = encryptLabel;
        this.decryptLabel = decryptLabel;
        byte[] keyBytes = "Secretivekeykeys".getBytes();
        secretKey = new SecretKeySpec(keyBytes, "AES");
    }
    private void printStorage(String operation, long bytes, boolean isEncrypt) {
    String output = operation + " Storage Complexity: " + bytes + " bytes\n";

    if (consoleArea != null) {
        consoleArea.append(output);
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    } else {
        System.out.print(output);
    }
}

    // --- Encrypt and shuffle votes ---
    public List<String> anonymizeVotes(List<String> votes) throws Exception {
    long startTime = System.nanoTime();

    // Step counter
    long steps = 0;

    // Shuffle
    Collections.shuffle(votes);
    steps += votes.size(); // approximate O(n) for shuffle

    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    steps++; // cipher init counted as one step

    List<String> encryptedVotes = new ArrayList<>();
    for (String vote : votes) {
        steps++; // loop iteration
        byte[] encrypted = cipher.doFinal(vote.getBytes());
        encryptedVotes.add(Base64.getEncoder().encodeToString(encrypted));
        steps++; // encryption + encoding counted as one step
    }

    long endTime = System.nanoTime();
    printTime("Anonymize Votes", startTime, endTime, true);

    // Print estimated steps as "algorithmic complexity"
    printSteps("Anonymize Votes", steps);

    return encryptedVotes;
}

private void printSteps(String operation, long steps) {
    String output = operation + " Approx. Steps (for complexity estimate): " + steps + "\n";
    if (consoleArea != null) {
        consoleArea.append(output);
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    } else {
        System.out.print(output);
    }
}

// --- Decrypt votes ---
public List<String> decryptVotes(List<String> encryptedVotes) throws Exception {
    long startTime = System.nanoTime();

    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, secretKey);

    List<String> decryptedVotes = new ArrayList<>();
    for (String encrypted : encryptedVotes) {
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        decryptedVotes.add(new String(cipher.doFinal(decoded)));
    }

    long endTime = System.nanoTime();
    printTime("Decrypt Votes", startTime, endTime, false);

    // --- Calculate approximate storage ---
    long storageBytes = 0;
    for (String s : decryptedVotes) {
        storageBytes += s.getBytes().length;
    }
    printStorage("Decrypt Votes", storageBytes, false);

    return decryptedVotes;
}

    // ---display execution time ---
    private void printTime(String operation, long start, long end, boolean isEncrypt) {
        long nano = end - start;
        double micro = nano / 1_000.0;
        double milli = nano / 1_000_000.0;

        String output = operation + " Time: " + nano + " nano/s | " + micro + " micro/s | " + milli + " ms\n";

        
        if (consoleArea != null) {
            consoleArea.append(output);
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());

            String summary = nano + " nano/s | " + micro + " micro/s | " + milli + " m/s";
            if (isEncrypt && encryptLabel != null) {
                encryptLabel.setText("Last Encrypt Time: " + summary);
            } else if (!isEncrypt && decryptLabel != null) {
                decryptLabel.setText("Last Decrypt Time: " + summary);
            }
        } else {
            // Fallback: print to console
        }
    }
    
    // --- Time Complexity Info ---
    // Both anonymizeVotes() and decryptVotes() are O(n) where n = number of votes.
    // Shuffle is O(n), iterating + encrypt/decrypt each vote is O(n).
}

