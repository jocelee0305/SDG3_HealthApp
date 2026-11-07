# SDG3 HealthApp

This is a Java-based desktop application developed for the TMF2954 Java Programming course.

## Features

| Feature | Description |
|---|---|
| Learning module | Educational pages with text and images |
| Quiz module | Multiple-choice, true/false, and fill-in-the-blank questions |
| Score feedback | The score is shown with a motivational message |
| Gamification | Points and badges to encourage learning |
| Score storage | Scores are saved to a text file, not hardcoded |

## How to Compile and Run

- Requires JDK 17 or later.
- As required by the course project, this application is developed to be **run from the terminal**.

### Option 1: Manual commands

Starting from the project root folder, run:

```
cd src\healthapp
javac -d . *.java
java healthapp.MainApplication
```

### Option 2: Using run.bat (Windows)

Open a terminal in the **project root folder** (the folder containing `run.bat` and `src`), then run:

```
.\run.bat
```

(In Command Prompt, use `run.bat`.)
