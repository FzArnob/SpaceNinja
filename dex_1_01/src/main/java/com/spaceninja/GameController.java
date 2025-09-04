package com.spaceninja;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameController {
    
    // Game constants
    private static final Color[] GAME_COLORS = {
        Color.web("#E42334"), // Red
        Color.web("#009c46"), // Green  
        Color.web("#0079c9")  // Blue
    };
    private static final double STICK_WIDTH = 90; // Match original design
    private static final double NINJA_SIZE = 64;
    private static final double SCENE_WIDTH = 1200;
    private static final double SCENE_HEIGHT = 800;
    
    // Game state
    private int score = 0;
    private boolean isGameRunning = false;
    private int currentColorIndex = 0;
    private Random random = new Random();
    private Timeline gameLoop;
    private Timeline ninjaAnimation;
    
    // UI Components
    private StackPane root;
    private Group gameScene;
    private Group sticksContainer;
    private Circle ninja;
    private Group ninjaContainer;
    private Label scoreLabel;
    private VBox startScreen;
    private VBox gameOverScreen;
    private Text hintText;
    
    // Game objects
    private List<Stick> sticks = new ArrayList<>();
    private ConcurrentLinkedQueue<Stick> sticksToRemove = new ConcurrentLinkedQueue<>();
    private double stickSpeed = 2.0;
    private final double ninjaY = SCENE_HEIGHT - 200;
    
    public GameController() {
        initializeUI();
    }
    
    private void initializeUI() {
        root = new StackPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e);");
        
        createBackground();
        createGameScene();
        createStartScreen();
        createGameOverScreen();
        
        showStartScreen();
    }
    
    private void createBackground() {
        // Create animated background elements matching the original design
        Group background = new Group();
        
        // Main background gradient
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1a1a2e 0%, #16213e 50%, #b99bb7 100%);");
        
        // Add animated stars
        for (int i = 0; i < 100; i++) {
            Circle star = new Circle(random.nextDouble() * 2 + 1, Color.WHITE);
            star.setLayoutX(random.nextDouble() * SCENE_WIDTH);
            star.setLayoutY(random.nextDouble() * SCENE_HEIGHT * 0.7); // Keep stars in upper area
            star.setOpacity(random.nextDouble() * 0.8 + 0.2);
            
            // Animate star twinkling
            Timeline starTwinkle = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(star.opacityProperty(), star.getOpacity())),
                new KeyFrame(Duration.seconds(1 + random.nextDouble() * 3), 
                    new KeyValue(star.opacityProperty(), random.nextDouble() * 0.5 + 0.2))
            );
            starTwinkle.setAutoReverse(true);
            starTwinkle.setCycleCount(Timeline.INDEFINITE);
            starTwinkle.play();
            
            background.getChildren().add(star);
        }
        
        // Add glow effect (like in original)
        Circle glow = new Circle(200, Color.web("#51EDC8"));
        glow.setLayoutX(SCENE_WIDTH * 0.3);
        glow.setLayoutY(SCENE_HEIGHT * 0.3);
        glow.setOpacity(0.1);
        glow.setEffect(new Glow(0.8));
        background.getChildren().add(glow);
        
        // Add sun image
        try {
            ImageView sun = new ImageView(new javafx.scene.image.Image(
                getClass().getResourceAsStream("/images/sun.png")));
            sun.setFitWidth(150);
            sun.setFitHeight(150);
            sun.setLayoutX(SCENE_WIDTH * 0.5 + 100);
            sun.setLayoutY(SCENE_HEIGHT * 0.1);
            
            // Rotate sun continuously
            RotateTransition sunRotation = new RotateTransition(Duration.seconds(100), sun);
            sunRotation.setByAngle(360);
            sunRotation.setCycleCount(Timeline.INDEFINITE);
            sunRotation.play();
            
            background.getChildren().add(sun);
        } catch (Exception e) {
            // Fallback sun if image not found
            Circle sunFallback = new Circle(75, Color.web("#FFD700"));
            sunFallback.setLayoutX(SCENE_WIDTH * 0.5 + 100);
            sunFallback.setLayoutY(SCENE_HEIGHT * 0.1);
            sunFallback.setEffect(new Glow(0.8));
            background.getChildren().add(sunFallback);
        }
        
        // Add earth image
        try {
            ImageView earth = new ImageView(new javafx.scene.image.Image(
                getClass().getResourceAsStream("/images/earth.svg")));
            earth.setFitWidth(150);
            earth.setFitHeight(150);
            earth.setLayoutX(SCENE_WIDTH - 200);
            earth.setLayoutY(50);
            
            // Rotate earth continuously
            RotateTransition earthRotation = new RotateTransition(Duration.seconds(100), earth);
            earthRotation.setByAngle(360);
            earthRotation.setCycleCount(Timeline.INDEFINITE);
            earthRotation.play();
            
            background.getChildren().add(earth);
        } catch (Exception e) {
            // Fallback earth if image not found
            Circle earthFallback = new Circle(75, Color.web("#4169E1"));
            earthFallback.setLayoutX(SCENE_WIDTH - 200);
            earthFallback.setLayoutY(50);
            earthFallback.setOpacity(0.7);
            background.getChildren().add(earthFallback);
        }
        
        // Add animated waves at the bottom
        createAnimatedWaves(background);
        
        root.getChildren().add(background);
    }
    
    private void createGameScene() {
        gameScene = new Group();
        
        // Create sticks container
        sticksContainer = new Group();
        gameScene.getChildren().add(sticksContainer);
        
        // Create ninja
        createNinja();
        
        // Create score label
        scoreLabel = new Label("0");
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        scoreLabel.setLayoutX(SCENE_WIDTH / 2 - 25);
        scoreLabel.setLayoutY(50);
        gameScene.getChildren().add(scoreLabel);
        
        // Create hint text
        hintText = new Text("Click or press SPACE to change color!");
        hintText.setFill(Color.WHITE);
        hintText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        hintText.setLayoutX(SCENE_WIDTH / 2 - 150);
        hintText.setLayoutY(150);
        hintText.setOpacity(0);
        gameScene.getChildren().add(hintText);
        
        root.getChildren().add(gameScene);
        gameScene.setVisible(false);
    }
    
    private void createNinja() {
        ninjaContainer = new Group();
        
        try {
            // Try to load ninja image
            ImageView ninjaImage = new ImageView(new Image(
                getClass().getResourceAsStream("/images/n-stand.svg")));
            ninjaImage.setFitWidth(NINJA_SIZE);
            ninjaImage.setFitHeight(NINJA_SIZE);
            
            // Create colored circles that represent the ninja's color parts
            Circle body = new Circle(NINJA_SIZE / 2 - 7, GAME_COLORS[currentColorIndex]);
            body.setLayoutX(NINJA_SIZE / 2);
            body.setLayoutY(NINJA_SIZE / 2 - 5);
            body.setOpacity(0.8);
            
            Circle head = new Circle(12, GAME_COLORS[currentColorIndex]);
            head.setLayoutX(NINJA_SIZE / 2 + 6);
            head.setLayoutY(NINJA_SIZE / 2 + 10);
            head.setOpacity(0.8);
            
            Circle leftArm = new Circle(4, GAME_COLORS[currentColorIndex]);
            leftArm.setLayoutX(NINJA_SIZE / 2 - 22);
            leftArm.setLayoutY(NINJA_SIZE / 2 - 15);
            leftArm.setOpacity(0.8);
            
            Circle rightArm = new Circle(3, GAME_COLORS[currentColorIndex]);
            rightArm.setLayoutX(NINJA_SIZE / 2 - 21);
            rightArm.setLayoutY(NINJA_SIZE / 2 - 8);
            rightArm.setOpacity(0.8);
            
            Circle leftLeg = new Circle(5, GAME_COLORS[currentColorIndex]);
            leftLeg.setLayoutX(NINJA_SIZE / 2 - 15);
            leftLeg.setLayoutY(NINJA_SIZE / 2 + 20);
            leftLeg.setOpacity(0.8);
            
            Circle rightLeg = new Circle(6, GAME_COLORS[currentColorIndex]);
            rightLeg.setLayoutX(NINJA_SIZE / 2 + 5);
            rightLeg.setLayoutY(NINJA_SIZE / 2 + 19);
            rightLeg.setOpacity(0.8);
            
            ninja = body; // Reference to main body for color changes
            
            ninjaContainer.getChildren().addAll(body, head, leftArm, rightArm, leftLeg, rightLeg, ninjaImage);
            
        } catch (Exception e) {
            // Fallback ninja if image not found
            ninja = new Circle(NINJA_SIZE / 2, GAME_COLORS[currentColorIndex]);
            ninja.setStroke(Color.BLACK);
            ninja.setStrokeWidth(2);
            ninja.setEffect(new DropShadow(10, Color.BLACK));
            
            // Ninja eyes
            Circle leftEye = new Circle(8, Color.BLACK);
            leftEye.setLayoutX(-12);
            leftEye.setLayoutY(-10);
            
            Circle rightEye = new Circle(8, Color.BLACK);
            rightEye.setLayoutX(12);
            rightEye.setLayoutY(-10);
            
            ninjaContainer.getChildren().addAll(ninja, leftEye, rightEye);
        }
        
        ninjaContainer.setLayoutX(SCENE_WIDTH / 2);
        ninjaContainer.setLayoutY(ninjaY);
        
        gameScene.getChildren().add(ninjaContainer);
    }
    
    private void createStartScreen() {
        startScreen = new VBox(30);
        startScreen.setAlignment(Pos.CENTER);
        startScreen.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");
        
        // Title
        Text title = new Text("SPACE NINJA");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setEffect(new Glow(0.5));
        
        // Subtitle
        Text subtitle = new Text("Navigate through space by matching colors!");
        subtitle.setFill(Color.LIGHTGRAY);
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 24));
        
        // Instructions
        VBox instructions = new VBox(10);
        instructions.setAlignment(Pos.CENTER);
        
        Text instr1 = new Text("• Ninja changes color while jumping");
        instr1.setFill(Color.WHITE);
        instr1.setFont(Font.font("Arial", 18));
        
        Text instr2 = new Text("• Click or press SPACE to change stick colors");
        instr2.setFill(Color.WHITE);
        instr2.setFont(Font.font("Arial", 18));
        
        Text instr3 = new Text("• Match ninja color with stick color to score");
        instr3.setFill(Color.WHITE);
        instr3.setFont(Font.font("Arial", 18));
        
        Text hint = new Text("Hint: Red color always comes first");
        hint.setFill(Color.web("#E42334"));
        hint.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        instructions.getChildren().addAll(instr1, instr2, instr3, hint);
        
        // Play button
        Button playButton = new Button("PLAY");
        playButton.setStyle("-fx-background-color: #E42334; -fx-text-fill: white; " +
                           "-fx-font-size: 24px; -fx-font-weight: bold; " +
                           "-fx-padding: 15 30; -fx-background-radius: 10;");
        playButton.setOnAction(e -> startGame());
        
        Text clickToPlay = new Text("Click anywhere to start!");
        clickToPlay.setFill(Color.LIGHTGRAY);
        clickToPlay.setFont(Font.font("Arial", 16));
        
        startScreen.getChildren().addAll(title, subtitle, instructions, playButton, clickToPlay);
        root.getChildren().add(startScreen);
    }
    
    private void createGameOverScreen() {
        gameOverScreen = new VBox(30);
        gameOverScreen.setAlignment(Pos.CENTER);
        gameOverScreen.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9);");
        
        Text gameOverTitle = new Text("GAME OVER");
        gameOverTitle.setFill(Color.web("#E42334"));
        gameOverTitle.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        
        Label finalScore = new Label();
        finalScore.setTextFill(Color.WHITE);
        finalScore.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        
        Label gradeLabel = new Label();
        gradeLabel.setTextFill(Color.YELLOW);
        gradeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Button playAgainButton = new Button("PLAY AGAIN");
        playAgainButton.setStyle("-fx-background-color: #009c46; -fx-text-fill: white; " +
                               "-fx-font-size: 20px; -fx-font-weight: bold; " +
                               "-fx-padding: 10 20; -fx-background-radius: 10;");
        playAgainButton.setOnAction(e -> startGame());
        
        Button menuButton = new Button("MAIN MENU");
        menuButton.setStyle("-fx-background-color: #0079c9; -fx-text-fill: white; " +
                          "-fx-font-size: 20px; -fx-font-weight: bold; " +
                          "-fx-padding: 10 20; -fx-background-radius: 10;");
        menuButton.setOnAction(e -> showStartScreen());
        
        gameOverScreen.getChildren().addAll(gameOverTitle, finalScore, gradeLabel, 
                                          playAgainButton, menuButton);
        
        // Store references for updating
        gameOverScreen.setUserData(new Node[]{finalScore, gradeLabel});
        
        root.getChildren().add(gameOverScreen);
        gameOverScreen.setVisible(false);
    }
    
    public void initializeGame() {
        // Game is initialized when start screen is shown
        showStartScreen();
    }
    
    public void startGame() {
        hideAllScreens();
        gameScene.setVisible(true);
        
        score = 0;
        currentColorIndex = 0; // Always start with red
        isGameRunning = true;
        stickSpeed = 2.0;
        
        // Reset ninja position and color
        ninjaContainer.setLayoutY(ninjaY);
        for (Node node : ninjaContainer.getChildren()) {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                if (circle.getFill() != Color.BLACK) { // Don't change eye color
                    circle.setFill(GAME_COLORS[currentColorIndex]);
                }
            }
        }
        
        // Clear existing sticks
        sticksContainer.getChildren().clear();
        sticks.clear();
        sticksToRemove.clear();
        
        // Generate initial sticks
        generateInitialSticks();
        
        updateScore();
        
        // Show hint briefly
        showHint();
        
        // Start game loop
        startGameLoop();
        startNinjaAnimation();
    }
    
    private void generateInitialSticks() {
        for (int i = 0; i < 10; i++) { // More sticks for original spacing
            // Position sticks with proper spacing (90px stick + 90px margin = 180px total)
            Stick stick = new Stick(SCENE_WIDTH + i * (STICK_WIDTH + 90), SCENE_HEIGHT - 252);
            sticks.add(stick);
            sticksContainer.getChildren().add(stick.getNode());
        }
    }
    
    private void showHint() {
        Timeline hintTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(hintText.opacityProperty(), 0)),
            new KeyFrame(Duration.seconds(0.5), new KeyValue(hintText.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(3), new KeyValue(hintText.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(4), new KeyValue(hintText.opacityProperty(), 0))
        );
        hintTimeline.play();
    }
    
    private void startGameLoop() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), e -> updateGame()));
        gameLoop.setCycleCount(Timeline.INDEFINITE);
        gameLoop.play();
    }
    
    private void startNinjaAnimation() {
        // Ninja bouncing animation
        ninjaAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(ninjaContainer.layoutYProperty(), ninjaY),
                new KeyValue(ninjaContainer.scaleYProperty(), 1.0)),
            new KeyFrame(Duration.seconds(0.5), 
                new KeyValue(ninjaContainer.layoutYProperty(), ninjaY - 100),
                new KeyValue(ninjaContainer.scaleYProperty(), 1.1)),
            new KeyFrame(Duration.seconds(1.0), 
                new KeyValue(ninjaContainer.layoutYProperty(), ninjaY),
                new KeyValue(ninjaContainer.scaleYProperty(), 0.8))
        );
        
        ninjaAnimation.setOnFinished(e -> {
            if (isGameRunning) {
                // Change ninja color for next jump
                changeNinjaColor();
                checkCollision();
                ninjaAnimation.play();
            }
        });
        
        ninjaAnimation.play();
    }
    
    private void updateGame() {
        if (!isGameRunning) return;
        
        // Move sticks
        for (Stick stick : sticks) {
            stick.moveLeft(stickSpeed);
            
            // Mark sticks for removal if they're off screen
            if (stick.getX() < -(STICK_WIDTH + 90)) {
                sticksToRemove.add(stick);
            }
        }
        
        // Remove off-screen sticks
        while (!sticksToRemove.isEmpty()) {
            Stick stick = sticksToRemove.poll();
            sticks.remove(stick);
            sticksContainer.getChildren().remove(stick.getNode());
        }
        
        // Add new sticks
        if (sticks.size() < 10) {
            double lastStickX = sticks.isEmpty() ? SCENE_WIDTH : 
                               sticks.get(sticks.size() - 1).getX();
            Stick newStick = new Stick(lastStickX + (STICK_WIDTH + 90), SCENE_HEIGHT - 362);
            sticks.add(newStick);
            sticksContainer.getChildren().add(newStick.getNode());
        }
        
        // Increase speed based on score
        if (score > 0 && score % 10 == 0) {
            stickSpeed = Math.min(4.0, 2.0 + score * 0.02);
        }
    }
    
    private void changeNinjaColor() {
        // Change to next color in sequence
        currentColorIndex = (currentColorIndex + 1) % GAME_COLORS.length;
        Color newColor = GAME_COLORS[currentColorIndex];
        
        // Update all colored ninja parts
        for (Node node : ninjaContainer.getChildren()) {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                if (circle.getFill() != Color.BLACK) { // Don't change eye color
                    circle.setFill(newColor);
                }
            }
        }
        
        // Add color change effect
        Timeline colorEffect = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(ninjaContainer.scaleXProperty(), 1.0)),
            new KeyFrame(Duration.millis(100), new KeyValue(ninjaContainer.scaleXProperty(), 1.2)),
            new KeyFrame(Duration.millis(200), new KeyValue(ninjaContainer.scaleXProperty(), 1.0))
        );
        colorEffect.play();
    }
    
    private void checkCollision() {
        double ninjaX = ninjaContainer.getLayoutX();
        
        for (Stick stick : sticks) {
            double stickLeft = stick.getX();
            double stickRight = stick.getX() + STICK_WIDTH;
            
            // Check if ninja is over this stick
            if (ninjaX >= stickLeft && ninjaX <= stickRight) {
                if (stick.getColorIndex() == currentColorIndex) {
                    // Correct color match
                    score++;
                    updateScore();
                    animateScoreIncrease();
                } else {
                    // Wrong color - game over
//                    gameOver();
                }
                break;
            }
        }
    }
    
    private void animateScoreIncrease() {
        Timeline scoreAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(scoreLabel.scaleXProperty(), 1.0)),
            new KeyFrame(Duration.millis(100), new KeyValue(scoreLabel.scaleXProperty(), 1.3)),
            new KeyFrame(Duration.millis(200), new KeyValue(scoreLabel.scaleXProperty(), 1.0))
        );
        scoreAnimation.play();
    }
    
    public void switchColor() {
        if (!isGameRunning) return;
        
        // Find the stick that's closest to the ninja
        double ninjaX = ninjaContainer.getLayoutX();
        Stick closestStick = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Stick stick : sticks) {
            double distance = Math.abs(stick.getX() + STICK_WIDTH / 2 - ninjaX);
            if (distance < minDistance && stick.getX() + STICK_WIDTH > ninjaX - 150) {
                minDistance = distance;
                closestStick = stick;
            }
        }
        
        if (closestStick != null) {
            closestStick.switchColor();
        }
    }
    
    private void updateScore() {
        scoreLabel.setText(String.valueOf(score));
    }
    
    private void gameOver() {
        isGameRunning = false;
        
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (ninjaAnimation != null) {
            ninjaAnimation.stop();
        }
        
        showGameOverScreen();
    }
    
    private void showStartScreen() {
        hideAllScreens();
        startScreen.setVisible(true);
    }
    
    private void showGameOverScreen() {
        hideAllScreens();
        
        // Update game over screen with final score
        Node[] components = (Node[]) gameOverScreen.getUserData();
        Label finalScore = (Label) components[0];
        Label gradeLabel = (Label) components[1];
        
        finalScore.setText("Final Score: " + score);
        gradeLabel.setText(getScoreGrade(score));
        
        gameOverScreen.setVisible(true);
    }
    
    private String getScoreGrade(int score) {
        if (score > 30) return "Chuck Norris?";
        if (score > 25) return "You're da man";
        if (score > 20) return "Awesome";
        if (score > 15) return "Great!";
        if (score > 13) return "Nice!";
        if (score > 10) return "Good Job!";
        if (score > 5) return "Really?";
        return "Poor...";
    }
    
    private void hideAllScreens() {
        startScreen.setVisible(false);
        gameOverScreen.setVisible(false);
        gameScene.setVisible(false);
    }
    
    public StackPane getRoot() {
        return root;
    }
    
    public boolean isGameRunning() {
        return isGameRunning;
    }

    private void createAnimatedWaves(Group background) {
        // Create wave layers using actual wave images with infinite scrolling effect
        
        // Wave 1 (bottom layer)
        Group wave1Group = createWaveLayer("/images/wave1.svg", SCENE_HEIGHT - 150, 150, 0.8, 15);
        background.getChildren().add(wave1Group);
        
        // Wave 2
        Group wave2Group = createWaveLayer("/images/wave2.svg", SCENE_HEIGHT - 210, 180, 0.6, 18);
        background.getChildren().add(wave2Group);
        
        // Wave 3
        Group wave3Group = createWaveLayer("/images/wave3.svg", SCENE_HEIGHT - 270, 180, 0.4, 20);
        background.getChildren().add(wave3Group);
        
        // Wave 4 (top layer)
        Group wave4Group = createWaveLayer("/images/wave4.svg", SCENE_HEIGHT - 330, 180, 0.3, 22);
        background.getChildren().add(wave4Group);
        
        // Top wave (surface)
        Group topWaveGroup = createWaveLayer("/images/top_wave.svg", SCENE_HEIGHT - 35, 35, 0.9, 12);
        background.getChildren().add(topWaveGroup);
    }
    
    private Group createWaveLayer(String imagePath, double yPosition, double height, double opacity, double animationDuration) {
        Group waveGroup = new Group();
        
        try {
            // Try to load the wave image
            var imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                // Create multiple copies of the image to fill the screen width + extra for seamless scrolling
                double imageWidth = 200; // Estimated width, will be adjusted based on actual image
                int numberOfCopies = (int) Math.ceil(SCENE_WIDTH / imageWidth) + 3; // Extra copies for seamless scrolling
                
                for (int i = 0; i < numberOfCopies; i++) {
                    ImageView waveImage = new ImageView(new Image(imageStream));
                    waveImage.setFitHeight(height);
                    waveImage.setPreserveRatio(true);
                    waveImage.setLayoutX(i * waveImage.getBoundsInLocal().getWidth());
                    waveImage.setLayoutY(yPosition);
                    waveImage.setOpacity(opacity);
                    
                    waveGroup.getChildren().add(waveImage);
                    
                    // Reset stream for next iteration
                    imageStream = getClass().getResourceAsStream(imagePath);
                }
                
                // Create infinite scrolling animation
                Timeline waveAnimation = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(waveGroup.layoutXProperty(), 0)),
                    new KeyFrame(Duration.seconds(animationDuration), new KeyValue(waveGroup.layoutXProperty(), -imageWidth))
                );
                waveAnimation.setCycleCount(Timeline.INDEFINITE);
                waveAnimation.play();
                
            } else {
                // Fallback to colored rectangle if image not found
                createFallbackWave(waveGroup, yPosition, height, opacity, animationDuration);
            }
        } catch (Exception e) {
            // Fallback to colored rectangle if image loading fails
            createFallbackWave(waveGroup, yPosition, height, opacity, animationDuration);
        }
        
        return waveGroup;
    }
    
    private void createFallbackWave(Group waveGroup, double yPosition, double height, double opacity, double animationDuration) {
        // Create fallback wave using rectangles with gradient
        Rectangle wave1 = new Rectangle(SCENE_WIDTH + 100, height);
        wave1.setFill(Color.web("#b99bb7"));
        wave1.setLayoutY(yPosition);
        wave1.setOpacity(opacity);
        
        Rectangle wave2 = new Rectangle(SCENE_WIDTH + 100, height);
        wave2.setFill(Color.web("#b99bb7"));
        wave2.setLayoutY(yPosition);
        wave2.setLayoutX(SCENE_WIDTH + 100);
        wave2.setOpacity(opacity);
        
        waveGroup.getChildren().addAll(wave1, wave2);
        
        // Animate the fallback waves
        Timeline waveAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(waveGroup.layoutXProperty(), 0)),
            new KeyFrame(Duration.seconds(animationDuration), new KeyValue(waveGroup.layoutXProperty(), -(SCENE_WIDTH + 100)))
        );
        waveAnimation.setCycleCount(Timeline.INDEFINITE);
        waveAnimation.play();
    }
}
