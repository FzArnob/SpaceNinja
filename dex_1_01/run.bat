@echo off
echo Building and running Space Ninja JavaFX Game...

REM Create target directories
if not exist target mkdir target
if not exist target\classes mkdir target\classes
if not exist target\dependency mkdir target\dependency

REM Download JavaFX if not exists (simplified approach)
echo Checking JavaFX dependencies...

REM Compile the Java files
echo Compiling Java sources...
javac -cp "." -d target\classes src\main\java\com\spaceninja\*.java

if %ERRORLEVEL% neq 0 (
    echo Compilation failed! Make sure JavaFX is properly installed.
    echo You may need to download JavaFX SDK and set the module path.
    pause
    exit /b 1
)

echo Compilation successful!

REM Copy resources
echo Copying resources...
if not exist target\classes\images mkdir target\classes\images
if exist src\main\resources\images\* copy src\main\resources\images\* target\classes\images\

REM Run the application
echo Running Space Ninja Game...
echo Note: If this fails, you need to install JavaFX and set the module path properly.
java -cp target\classes com.spaceninja.SpaceNinjaApplication

if %ERRORLEVEL% neq 0 (
    echo.
    echo Failed to run the application. 
    echo Make sure JavaFX runtime is installed or use:
    echo java --module-path "path\to\javafx\lib" --add-modules javafx.controls,javafx.fxml -cp target\classes com.spaceninja.SpaceNinjaApplication
)

pause
