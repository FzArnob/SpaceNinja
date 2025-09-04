package com.spaceninja;

import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Stick {
    
    private static final Color[] GAME_COLORS = {
        Color.web("#E42334"), // Red
        Color.web("#009c46"), // Green  
        Color.web("#0079c9")  // Blue
    };
    
    private static final double STICK_WIDTH = 90;  // Match original width
    private static final double STICK_HEIGHT = 362; // Match original height
    
    private Group stickNode;
    private Rectangle stickBody;
    private int colorIndex;
    private double x, y;
    private Random random = new Random();
    private Group effectsGroup;
    
    public Stick(double x, double y) {
        this.x = x;
        this.y = y;
        this.colorIndex = 0; // Start with red (inactive)
        
        createStick();
        createEffects();
    }
    
    private void createStick() {
        stickNode = new Group();
        
        // Main stick body with rounded corners like original
        stickBody = new Rectangle(STICK_WIDTH, STICK_HEIGHT);
        stickBody.setFill(Color.web("#CDB8E6").deriveColor(0, 1, 1, 0.8)); // Inactive color matching original
        stickBody.setStroke(Color.BLACK);
        stickBody.setStrokeWidth(1);
        stickBody.setArcWidth(14); // Match original border-radius
        stickBody.setArcHeight(14);
        
        // Add noise texture effect (simulate background-image: url(img/noise.png))
        stickBody.setStyle("-fx-fill: rgba(205, 184, 230, 0.8);");
        
        // Add shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3);
        shadow.setOffsetX(2);
        shadow.setColor(Color.BLACK.deriveColor(0, 1, 1, 0.3));
        stickBody.setEffect(shadow);
        
        stickNode.getChildren().add(stickBody);
        stickNode.setLayoutX(x);
        stickNode.setLayoutY(y);
    }
    
    private void createEffects() {
        effectsGroup = new Group();
        
        // Create particle effects for the stick
        int effectType = random.nextInt(3); // 0: bubbles, 1: triangles, 2: blocks
        
        switch (effectType) {
            case 0:
                createBubbleEffects();
                break;
            case 1:
                createTriangleEffects();
                break;
            case 2:
                createBlockEffects();
                break;
        }
        
        stickNode.getChildren().add(effectsGroup);
    }
    
    private void createBubbleEffects() {
        for (int i = 0; i < 12; i++) {
            Circle bubble = new Circle(random.nextDouble() * 15 + 5, 
                                     Color.WHITE.deriveColor(0, 1, 1, 0.3));
            bubble.setStroke(Color.WHITE.deriveColor(0, 1, 1, 0.5));
            bubble.setStrokeWidth(2);
            
            double bubbleX = random.nextDouble() * STICK_WIDTH;
            double bubbleY = random.nextDouble() * STICK_HEIGHT;
            
            bubble.setLayoutX(bubbleX);
            bubble.setLayoutY(bubbleY);
            
            // Animate bubbles
            Timeline bubbleAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(bubble.layoutYProperty(), bubbleY),
                    new KeyValue(bubble.scaleXProperty(), 0.1),
                    new KeyValue(bubble.scaleYProperty(), 0.1)),
                new KeyFrame(Duration.seconds(1 + random.nextDouble()), 
                    new KeyValue(bubble.layoutYProperty(), bubbleY - 60),
                    new KeyValue(bubble.scaleXProperty(), 1.0),
                    new KeyValue(bubble.scaleYProperty(), 1.0))
            );
            bubbleAnimation.setAutoReverse(true);
            bubbleAnimation.setCycleCount(Timeline.INDEFINITE);
            bubbleAnimation.play();
            
            effectsGroup.getChildren().add(bubble);
        }
    }
    
    private void createTriangleEffects() {
        for (int i = 0; i < 10; i++) {
            // Create triangle using polygon approximation with rectangles
            Rectangle triangle = new Rectangle(8, 8);
            triangle.setFill(Color.YELLOW.deriveColor(0, 1, 1, 0.6));
            triangle.setRotate(45);
            
            double triX = random.nextDouble() * STICK_WIDTH;
            double triY = random.nextDouble() * STICK_HEIGHT;
            
            triangle.setLayoutX(triX);
            triangle.setLayoutY(triY);
            
            // Animate triangles
            Timeline triangleAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(triangle.rotateProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(triangle.rotateProperty(), 360))
            );
            triangleAnimation.setCycleCount(Timeline.INDEFINITE);
            triangleAnimation.play();
            
            effectsGroup.getChildren().add(triangle);
        }
    }
    
    private void createBlockEffects() {
        for (int i = 0; i < 8; i++) {
            Rectangle block = new Rectangle(12, 12, Color.CYAN.deriveColor(0, 1, 1, 0.7));
            block.setStroke(Color.WHITE);
            block.setStrokeWidth(1);
            
            double blockX = random.nextDouble() * STICK_WIDTH;
            double blockY = random.nextDouble() * STICK_HEIGHT;
            
            block.setLayoutX(blockX);
            block.setLayoutY(blockY);
            
            // Animate blocks
            Timeline blockAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(block.layoutXProperty(), blockX)),
                new KeyFrame(Duration.seconds(1.5), 
                    new KeyValue(block.layoutXProperty(), blockX + (random.nextBoolean() ? 20 : -20)))
            );
            blockAnimation.setAutoReverse(true);
            blockAnimation.setCycleCount(Timeline.INDEFINITE);
            blockAnimation.play();
            
            effectsGroup.getChildren().add(block);
        }
    }
    
    public void switchColor() {
        // Cycle through colors: gray -> red -> green -> blue -> red -> ...
        if (stickBody.getFill() == Color.GRAY) {
            // First activation - go to red
            colorIndex = 0;
        } else {
            // Cycle through active colors
            colorIndex = (colorIndex + 1) % GAME_COLORS.length;
        }
        
        stickBody.setFill(GAME_COLORS[colorIndex]);
        
        // Add glow effect when active
        Glow glow = new Glow(0.8);
        stickBody.setEffect(glow);
        
        // Color change animation
        Timeline colorAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(stickNode.scaleXProperty(), 1.0)),
            new KeyFrame(Duration.millis(100), new KeyValue(stickNode.scaleXProperty(), 1.1)),
            new KeyFrame(Duration.millis(200), new KeyValue(stickNode.scaleXProperty(), 1.0))
        );
        colorAnimation.play();
        
        // Update effect colors based on stick color
        updateEffectColors();
    }
    
    private void updateEffectColors() {
        Color currentColor = GAME_COLORS[colorIndex];
        
        for (var node : effectsGroup.getChildren()) {
            if (node instanceof Circle) {
                Circle circle = (Circle) node;
                circle.setFill(currentColor.deriveColor(0, 1, 1, 0.3));
                circle.setStroke(currentColor.deriveColor(0, 1, 1, 0.6));
            } else if (node instanceof Rectangle) {
                Rectangle rect = (Rectangle) node;
                rect.setFill(currentColor.deriveColor(0, 1, 1, 0.6));
            }
        }
    }
    
    public void moveLeft(double speed) {
        x -= speed;
        stickNode.setLayoutX(x);
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public int getColorIndex() {
        // Return -1 if stick is inactive (purple-gray), otherwise return color index
        Color currentFill = (Color) stickBody.getFill();
        Color inactiveColor = Color.web("#CDB8E6").deriveColor(0, 1, 1, 0.8);
        
        // Check if current color matches any of the game colors
        for (int i = 0; i < GAME_COLORS.length; i++) {
            if (currentFill.equals(GAME_COLORS[i])) {
                return i;
            }
        }
        return -1; // Inactive
    }
    
    public Group getNode() {
        return stickNode;
    }
    
    public boolean isActive() {
        return getColorIndex() != -1;
    }
}
