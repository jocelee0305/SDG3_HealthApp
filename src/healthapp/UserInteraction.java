package healthapp;

// ============================================================
// Interface   : UserInteraction
// Creator     : Muhammad Izzat (105244)
// Tester      : Jocelyn (104561)
// OOP         : Abstraction
// Description : Defines the standard contract for application
//               navigation and state control mechanisms.
// ============================================================

public interface UserInteraction {
    void showMainMenu();
    void navigateTo(String module);
    void showWelcomeScreen();
    void exitApplication();
}
