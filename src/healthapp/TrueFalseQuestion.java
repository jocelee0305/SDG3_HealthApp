package healthapp;

// ============================================================
// Class       : TrueFalseQuestion
// Creator     : Lee Xing Ying （104731）
// Tester      : Member 4
// OOP         : Inheritance, Method Overriding
// Description : Represents a true/false question.
//               Inherits from Question and overrides
//               checkAnswer() to compare user responses.
// ============================================================

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