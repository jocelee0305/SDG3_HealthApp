package healthapp;

// Class: FillBlankQuestion
// Creator: Lee Xing Ying
// Tester: Member 4
// OOP: Inheritance, Overriding

public class FillBlankQuestion extends Question {

    // Constructor
    // Initializes fill-in-the-blank question
    public FillBlankQuestion(String questionText, String correctAnswer) {
        super(questionText, correctAnswer);
    }

    // Checks whether the user's answer matches the correct answer
    @Override
    public boolean checkAnswer(String answer) {
        return answer.trim()
                     .equalsIgnoreCase(correctAnswer);
    }
}