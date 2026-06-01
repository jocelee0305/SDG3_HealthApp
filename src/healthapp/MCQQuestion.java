package healthapp;

// ============================================================
// Class       : MCQQuestion
// Creator     : Lee Xing Ying （104731）
// Tester      : Member 4
// OOP Concept : Inheritance, Method Overriding
// Description : Represents a multiple-choice question.
//               Stores answer options and overrides the
//               checkAnswer() method to validate answers.
// ============================================================

public class MCQQuestion extends Question {

    private String[] options;

    // Constructor
    // Initializes MCQ question with answer options
    public MCQQuestion(String questionText, String correctAnswer, String[] options) {
        super(questionText, correctAnswer);
        this.options = options;
    }

     // Returns all answer options
    public String[] getOptions() {
        return options;
    }

    // Checks whether the selected answer is correct
    @Override
    public boolean checkAnswer(String answer) {
        return answer.equalsIgnoreCase(correctAnswer);
    }
}