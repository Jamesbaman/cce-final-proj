
// thank god for these packages

import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@SuppressWarnings("unused")
public class MixNet {
    private SecretKey secretKey;
public MixNet() throws Exception {
    byte[] keyBytes = "MyKey".getBytes(); // 16 chars = 128-bit key
    secretKey = new SecretKeySpec(keyBytes, "AES");

}
    

    public List<String> anonymizeVotes(List<String> votes) throws Exception {
        Collections.shuffle(votes); // shuffle for anonymity

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        List<String> encryptedVotes = new ArrayList<>();
        for (String vote : votes) {
            byte[] encrypted = cipher.doFinal(vote.getBytes());
            encryptedVotes.add(Base64.getEncoder().encodeToString(encrypted));
        }

        return encryptedVotes;
    }

    public List<String> decryptVotes(List<String> encryptedVotes) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        List<String> decryptedVotes = new ArrayList<>();
        for (String encrypted : encryptedVotes) {
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            decryptedVotes.add(new String(cipher.doFinal(decoded)));
        }
        return decryptedVotes;
    }
}
