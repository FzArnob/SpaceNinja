# Space Ninja - JavaFX Desktop Game

A desktop version of the Space Ninja game built with JavaFX 21 and Java 21.

## Prerequisites

- Java 21 or higher
- JavaFX 21 runtime (if not included with your Java installation)

## Game Description

Space Ninja is a color-matching jumping game where:
- The ninja automatically changes color while jumping
- Click or press SPACE to change the color of the sticks
- Match the ninja's color with the stick color to score points
- The game gets faster as your score increases

## Controls

- **Click anywhere or press SPACE**: Change stick colors during gameplay
- **Click anywhere or press ENTER**: Start the game when not playing
- **ESC**: Exit the game

## Running the Game

### Option 1: Using the batch file (Windows)
```bash
run.bat
```

### Option 2: Manual compilation and execution

1. Compile the sources:
```bash
javac -d target/classes src/main/java/com/spaceninja/*.java
```

2. Copy resources:
```bash
cp -r src/main/resources/* target/classes/
```

3. Run the application:
```bash
java -cp target/classes com.spaceninja.SpaceNinjaApplication
```

### Option 3: If you have JavaFX as a separate download

If JavaFX is not included with your Java installation:

```bash
java --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp target/classes com.spaceninja.SpaceNinjaApplication
```

## Game Features

- **Authentic Design**: Recreates the visual style of the original web version
- **Animated Background**: Moving waves, rotating planets, and twinkling stars
- **Color-Matching Gameplay**: Same mechanics as the original game
- **Particle Effects**: Animated bubbles, triangles, and blocks on sticks
- **Score System**: Same scoring and difficulty progression
- **Responsive UI**: Scales to different screen sizes

## Project Structure

```
dex_1_01/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── spaceninja/
│       │           ├── SpaceNinjaApplication.java  # Main application class
│       │           ├── GameController.java         # Game logic and UI
│       │           └── Stick.java                  # Stick objects with effects
│       └── resources/
│           └── images/                             # Game assets
├── pom.xml                                         # Maven build file
├── run.bat                                         # Windows run script
└── README.md                                       # This file
```

## Troubleshooting

1. **JavaFX not found**: Install JavaFX SDK and use the module path option
2. **Images not loading**: Ensure images are copied to target/classes/images/
3. **Compilation errors**: Check that Java 21+ is installed and in PATH

## Original Web Version

This desktop version faithfully recreates the gameplay and visual design of the original web-based Space Ninja game, maintaining the same color schemes, animations, and game mechanics while adapting them for desktop use with JavaFX.
