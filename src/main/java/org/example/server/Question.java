package org.example.server;
/**
 * The Question class represents a multiple-choice trivia question.
 * It includes the question text, four answer options (A–D),
 * and the correct answer (either as text or as a letter code).
 */
public class Question {
    private final String text;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final String correctAnswer;

    /**
     * Constructs a new Question with the specified text and options.
     *
     * @param text          the question text
     * @param optionA       option A
     * @param optionB       option B
     * @param optionC       option C
     * @param optionD       option D
     * @param correctAnswer the correct answer (text or letter: A/B/C/D)
     */
    public Question(String text, String optionA, String optionB, String optionC, String optionD, String correctAnswer) {
        this.text = text;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
    }

    /**
     * Returns the question text.
     *
     * @return the question text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the text for option A.
     *
     * @return option A
     */
    public String getOptionA() {
        return optionA;
    }

    /**
     * Returns the text for option C.
     *
     * @return option C
     */
    public String getOptionB() {
        return optionB;
    }

    /**
     * Returns the text for option D.
     *
     * @return option D
     */
    public String getOptionC() {
        return optionC;
    }

    /**
     * Returns the correct answer (as a string or letter).
     *
     * @return the correct answer
     */
    public String getOptionD() {
        return optionD;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    // check if the answer is correct
    /**
     * Checks whether the given answer matches the correct answer.
     * Accepts both full text or letter-based input (A/B/C/D).
     *
     * @param answer the answer to check
     * @return true if the answer is correct, false otherwise
     */
    public boolean isCorrectAnswer(String answer) {
        String normalizedInput = answer.trim().toLowerCase();
        String normalizedCorrect = this.correctAnswer.trim().toLowerCase();

        // Check if answer matches full text or letter code
        return normalizedInput.equals(normalizedCorrect) ||
                (normalizedInput.length() == 1 &&
                        getOptionByLetter(normalizedInput.toUpperCase().charAt(0)).trim().toLowerCase()
                                .equals(normalizedCorrect));
    }

    /**
     * Returns the answer option corresponding to the given letter.
     *
     * @param letter the letter (A–D)
     * @return the corresponding option text, or empty string if invalid
     */
    private String getOptionByLetter(char letter) {
        return switch (letter) {
            case 'A' -> optionA;
            case 'B' -> optionB;
            case 'C' -> optionC;
            case 'D' -> optionD;
            default -> "";
        };
    }

    /**
     * Returns a string representation of the question and its options.
     *
     * @return formatted question and answer options
     */
    @Override
    public String toString() {
        return text + "\nA. " + optionA + "\nB. " + optionB + "\nC. " + optionC + "\nD. " + optionD;
    }
}