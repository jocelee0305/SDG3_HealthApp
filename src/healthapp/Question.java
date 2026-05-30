package healthapp;

// Abstract Class: Question
// Creator: Lee Xing Ying
// Tester: Andrea
// OOP: Abstration, Interface implementation

public abstract class Question implements QuestionHandler {

    protected String questionText;
    protected String correctAnswer;

    // Constructor
    public Question(String questionText, String correctAnswer) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
    }

    // Returns the question text
    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    // Displays the question to the user
    @Override
    public void displayQuestion() {
        System.out.println(questionText);
    }

    // Checks whether the user's answer is correct
    @Override
    public abstract boolean checkAnswer(String answer);
}

