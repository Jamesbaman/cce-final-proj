import javax.swing.JOptionPane;

public class ComplexityS {
     public static void showComplexity(String operation, int n, int m) {
        String message = "";

        switch (operation) {
            case "Cast Vote":
                message = "Operation: Cast Vote\nTime Complexity: O(1)\nSpace Complexity: O(1)";
                break;
            case "Load Votes":
                message = String.format("Operation: Load Votes\nTime Complexity: O(n) where n=%d votes\nSpace Complexity: O(n)", n);
                break;
            case "Vote Tally":
                message = String.format("Operation: Vote Tally\nTime Complexity: O(n + m) where n=%d votes, m=%d candidates\nSpace Complexity: O(m)", n, m);
                break;
            case "Encrypt Votes":
                message = String.format("Operation: Encrypt Votes\nTime Complexity: O(n) where n=%d votes\nSpace Complexity: O(n)", n);
                break;
            default:
                message = "Operation: " + operation;
        }

        JOptionPane.showMessageDialog(null, message, "Complexity Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
                                                                            