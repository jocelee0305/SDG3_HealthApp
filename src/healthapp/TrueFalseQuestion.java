package healthapp;

// Class: TrueFalseQuestion
// Creator: Lee Xing Ying
// Tester: Member 4
// OOP: Inheritance, Overriding

public class TrueFalseQuestion extends Question {

    // Constructor
    // Initializes True/False question
    public TrueFalseQuestion(String questionText, String correctAnswer) {
        super(questionText, correctAnswer);
    }

    // Checks whether the user's answer is correct
    @Override
    public boolean checkAnswer(String answer) {
        return answer.equalsIgnoreCase(correctAnswer);
    }
}