package healthapp;

// ============================================================
// Interface  : QuestionHandler
// Creator    : Lee Xing Ying (104731)
// Tester     : Andrea
// Description: Defines the contract for displaying quiz
//              questions and validating user answers in
//              the HealthApp quiz module.
// ============================================================

public interface QuestionHandler {

     // Displays the question to the user
    void displayQuestion();

    // Validates the user's answer
    boolean checkAnswer(String answer);
}
