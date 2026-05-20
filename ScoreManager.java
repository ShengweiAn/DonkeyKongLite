import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreManager {
    private static final String SCORE_FILE = "highscores.txt";

    public void saveScore(String initials, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE, true))) {
            writer.write(initials + " - " + score);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    public ArrayList<String> getHighScores() {
        ArrayList<String> scores = new ArrayList<>();
        File file = new File(SCORE_FILE);

        if (!file.exists()) {
            return scores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && line.contains(" - ")) {
                    scores.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading high scores: " + e.getMessage());
        }

        // Force sorts the scores in descending order
        Collections.sort(scores, (s1, s2) -> {
            try {
                int score1 = Integer.parseInt(s1.substring(s1.lastIndexOf("-") + 1).trim());
                int score2 = Integer.parseInt(s2.substring(s2.lastIndexOf("-") + 1).trim());
                return Integer.compare(score2, score1); 
            } catch (Exception e) {
                return 0; 
            }
        });

        return scores;
    }
}
