package healthapp;

// Interface: ContentDisplay
// Creator: Andrea (Member 1)
// Tester: Member 4

public interface ContentDisplay {

    void displayPage(int pageIndex);   // show a specific page
    int getTotalPages();               // get total number of pages
    void nextPage();                   // go to next page
    void previousPage();               // go to previous page
    String getPageTitle(int pageIndex);// get title of a page
}
