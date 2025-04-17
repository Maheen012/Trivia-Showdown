package org.example.server;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * The GameLogic class manages the core game flow and question management.
 * It loads trivia questions from a CSV file and provides access to the current
 * and next questions during the game.
 */

public class GameLogic {
    private List<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = -1;
    private boolean gameRunning = false;
    private Question currentQuestion;

    /**
     * Constructs a GameLogic instance and loads questions from the CSV resource file.
     */
    public GameLogic() {
        loadQuestionsFromCSV("/QuesAns.csv");
    }

    // Loads questions from CSV file using OpenCSV
    /**
     * Loads trivia questions from the specified CSV file using OpenCSV.
     * Each row should contain at least 6 columns: question text, 4 options (A-D), and the correct answer.
     *
     * @param filename the path to the CSV file (must be in the resources folder)
     */
    private void loadQuestionsFromCSV(String filename) {
        try (InputStream is = getClass().getResourceAsStream(filename);
             InputStreamReader isr = new InputStreamReader(is);
             CSVReader reader = new CSVReader(isr)) {

            // Skip header row
            reader.readNext();

            List<String[]> lines = reader.readAll();
            for (String[] parts : lines) {
                if (parts.length >= 6) {
                    questions.add(new Question(
                            parts[0].trim(),        // Question text
                            parts[1].trim(),        // Option 1
                            parts[2].trim(),        // Option 2
                            parts[3].trim(),        // Option 3
                            parts[4].trim(),        // Option 4
                            parts[5].trim()         // Correct Answer
                    ));
                }
            }
            System.out.println("Loaded " + questions.size() + " questions from CSV");
        } catch (Exception e) {
            System.err.println("Error loading questions: " + e.getMessage());
        }
    }

    /**
     * Returns the current question.
     *
     * @return the current {@link Question}, or null if none has been fetched yet
     */
    public Question getCurrentQuestion() { return currentQuestion; }

    /**
     * Returns the index of the current question.
     *
     * @return the index of the current question (starting from 0)
     */
    public int getCurrentQuestionIndex() { return currentQuestionIndex; }

    // starts the game
    /**
     * Starts a new game session.
     * Resets the question index and clears the current question.
     */
    public void startGame() {
        gameRunning = true;
        currentQuestionIndex = -1;
        currentQuestion = null;
    }

    // fetches the next question
    /**
     * Fetches the next question in the list.
     * If no more questions are available, returns null.
     *
     * @return the next {@link Question}, or null if all questions have been used
     */
    public Question getNextQuestion() {
        if (++currentQuestionIndex < questions.size()) {
            currentQuestion = questions.get(currentQuestionIndex);
            return currentQuestion;
        }
        return null;
    }

    // prints questions onto the terminal
    /**
     * Prints all loaded questions to the terminal for debugging.
     */
    public void printAllQuestions() {
        for (Question q : questions) {
            System.out.println(q);
        }
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    // just to check if all questions are being loaded
    /**
     * A test main method to verify that questions are loading correctly.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        GameLogic gameLogic = new GameLogic();
        gameLogic.printAllQuestions();
    }
}