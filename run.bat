@echo off
cd src\healthapp
javac -d . *.java
java healthapp.MainApplication
pause