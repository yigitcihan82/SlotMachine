package com.example.slotmachineapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.transform.Rotate;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.InputStream;
import java.util.Random;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SlotMachineController implements Initializable {

    @FXML
    private HBox reelsPane;
    @FXML
    private ImageView reel1;
    @FXML
    private ImageView reel2;
    @FXML
    private ImageView reel3;

    @FXML
    private Label creditsLabel;
    @FXML
    private Label infoLabel;

    @FXML
    private Button betButton;
    @FXML
    private Button addCreditsButton;

    private UserManager userManager = UserManager.getInstance();
    private MediaPlayer winSound;
    private MediaPlayer bigWinSound;
    private MediaPlayer spinSound;
    private MediaPlayer addCreditsSound;

    private int credits = 0;
    private final int BET_AMOUNT = 10;
    private final int WIN_PAYOUT_THREE_OF_A_KIND = 100;
    private final int WIN_PAYOUT_TWO_SEVENS = 30;
    private final int WIN_PAYOUT_ONE_SEVEN = 5;

    private final String[] SYMBOL_NAMES = {"cherry.png", "lemon.png", "diamond.png", "seven.png"};
    private List<Image> symbolImages = new ArrayList<>();
    private String[] currentSymbols = new String[3];
    private Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (userManager.getCurrentUser() != null) {
            credits = userManager.getCurrentUser().getCredits();
            updateCreditsDisplay();
        }

        loadSymbolImages();
        setupReels();
        setupSounds();
        infoLabel.setText("Pull the lever to play!");
        betButton.setRotationAxis(Rotate.Z_AXIS);
    }

    private void setupSounds() {
        try {
            URL winSoundUrl = getClass().getResource("/sounds/win.mp3");
            if (winSoundUrl != null) {
                Media winMedia = new Media(winSoundUrl.toString());
                winSound = new MediaPlayer(winMedia);
                System.out.println("Win sound loaded successfully");
            } else {
                System.out.println("Win sound file not found");
            }

            URL bigWinSoundUrl = getClass().getResource("/sounds/bigwin.mp3");
            if (bigWinSoundUrl != null) {
                Media bigWinMedia = new Media(bigWinSoundUrl.toString());
                bigWinSound = new MediaPlayer(bigWinMedia);
                System.out.println("Big win sound loaded successfully");
            } else {
                System.out.println("Big win sound file not found");
            }

            URL spinSoundUrl = getClass().getResource("/sounds/spin.mp3");
            if (spinSoundUrl != null) {
                Media spinMedia = new Media(spinSoundUrl.toString());
                spinSound = new MediaPlayer(spinMedia);
                System.out.println("Spin sound loaded successfully");
            } else {
                System.out.println("Spin sound file not found");
            }

            URL addCreditsSoundUrl = getClass().getResource("/sounds/addcreditsound.mp3");
            if (addCreditsSoundUrl != null) {
                Media addCreditsMedia = new Media(addCreditsSoundUrl.toString());
                addCreditsSound = new MediaPlayer(addCreditsMedia);
                System.out.println("Add credits sound loaded successfully");
            } else {
                System.out.println("Add credits sound file not found");
            }
        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupReels() {
        reel1.setImage(symbolImages.get(0));
        reel2.setImage(symbolImages.get(1));
        reel3.setImage(symbolImages.get(2));
    }

    private void loadSymbolImages() {
        symbolImages.clear();
        try {
            for (String symbolName : SYMBOL_NAMES) {
                InputStream stream = getClass().getResourceAsStream("/images/" + symbolName);
                if (stream == null) {
                    System.err.println("Cannot load image: " + symbolName);
                    continue;
                }
                symbolImages.add(new Image(stream));
            }
        } catch (Exception e) {
            e.printStackTrace();
            infoLabel.setText("Error loading images!");
        }
    }

    @FXML
    private void spinReels() {
        if (credits < BET_AMOUNT) {
            infoLabel.setText("Not enough coins! Add more.");
            return;
        }

        credits -= BET_AMOUNT;
        updateCreditsDisplay();
        infoLabel.setText("Spinning...");
        betButton.setDisable(true);
        addCreditsButton.setDisable(true);

        if (spinSound != null) {
            spinSound.stop();
            spinSound.play();
        }

        currentSymbols = new String[3];

        Timeline timeline = new Timeline();
        Duration spinDuration = Duration.seconds(6);

        addSpinningKeyframes(timeline, reel1, 0, spinDuration);
        addSpinningKeyframes(timeline, reel2, 400, spinDuration);
        addSpinningKeyframes(timeline, reel3, 800, spinDuration);

        timeline.setOnFinished(event -> {
            betButton.setDisable(false);
            addCreditsButton.setDisable(false);
            checkWin();
        });

        timeline.play();
    }

    private void addSpinningKeyframes(Timeline timeline, ImageView reel, int delay, Duration duration) {
        KeyFrame[] frames = new KeyFrame[20];
        Duration frameGap = duration.divide(frames.length);
        
        for (int i = 0; i < frames.length; i++) {
            int frameIndex = i;
            frames[i] = new KeyFrame(
                frameGap.multiply(i + 1).add(Duration.millis(delay)),
                event -> {
                    Image randomSymbol = symbolImages.get(random.nextInt(symbolImages.size()));
                    reel.setImage(randomSymbol);
                    if (frameIndex == frames.length - 1) {
                        String symbolName = SYMBOL_NAMES[symbolImages.indexOf(randomSymbol)];
                        if (reel == reel1) currentSymbols[0] = symbolName;
                        else if (reel == reel2) currentSymbols[1] = symbolName;
                        else if (reel == reel3) currentSymbols[2] = symbolName;
                    }
                }
            );
        }
        
        timeline.getKeyFrames().addAll(frames);
    }

    private void checkWin() {
        String s1 = currentSymbols[0];
        String s2 = currentSymbols[1];
        String s3 = currentSymbols[2];

        boolean win = false;
        int payout = 0;

        if (s1.equals(s2) && s2.equals(s3)) {
            String symbolName = s1.replace(".png", "").toUpperCase();
            infoLabel.setText("MEGA WIN! 3x " + symbolName);
            payout = WIN_PAYOUT_THREE_OF_A_KIND;
            if (s1.equals("seven.png")) {
                payout *= 2;
            }
            win = true;
            if (bigWinSound != null) {
                bigWinSound.stop();
                bigWinSound.play();
            }
        } else {
            int sevenCount = 0;
            if (s1.equals("seven.png")) sevenCount++;
            if (s2.equals("seven.png")) sevenCount++;
            if (s3.equals("seven.png")) sevenCount++;

            if (sevenCount == 2) {
                infoLabel.setText("WIN! Two Sevens!");
                payout = WIN_PAYOUT_TWO_SEVENS;
                win = true;
            } else if (sevenCount == 1) {
                infoLabel.setText("Small Win! One Seven!");
                payout = WIN_PAYOUT_ONE_SEVEN;
                win = true;
            }
        }

        if (win) {
            credits += payout;
            infoLabel.setText(infoLabel.getText() + " You won " + payout + " coins!");
            if (winSound != null) {
                winSound.stop();
                winSound.play();
            }
        } else {
            infoLabel.setText("No win. Try again!");
        }
        updateCreditsDisplay();

        if (credits < BET_AMOUNT) {
            infoLabel.setText(infoLabel.getText() + " Game Over ");
            betButton.setDisable(true);
        }
    }

    private void updateCreditsDisplay() {
        if (userManager.getCurrentUser() != null) {
            userManager.getCurrentUser().setCredits(credits);
            creditsLabel.setText(String.valueOf(credits));
            betButton.setDisable(credits < BET_AMOUNT);
            userManager.updateCurrentUserCredits(credits);
        }
    }

    @FXML
    private void addCredits() {
        if (userManager.getCurrentUser() != null) {
            if (addCreditsSound != null) {
                System.out.println("Starting add credits sound");
                addCreditsSound.stop();
                addCreditsSound.play();
            } else {
                System.out.println("Add credits sound is null");
            }

            TextInputDialog dialog = new TextInputDialog("100");
            dialog.setTitle("Add Coins");
            dialog.setHeaderText("Enter amount of coins to add");
            dialog.setContentText("Amount:");

            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setStyle("-fx-background-color: black;");
            dialogPane.setGraphic(null);
            
            dialogPane.lookupAll(".label").forEach(node -> {
                node.setStyle("-fx-text-fill: white; -fx-background-color: black;");
            });
            
            dialogPane.lookupAll(".text-field").forEach(node -> {
                node.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-prompt-text-fill: #666666;");
            });
            
            dialogPane.lookupAll(".button").forEach(node -> {
                node.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            });

            dialogPane.lookup(".content").setStyle("-fx-background-color: black;");
            
            dialogPane.lookup(".header-panel").setStyle("-fx-background-color: black;");
            
            dialogPane.lookup(".button-bar").setStyle("-fx-background-color: black;");

            dialog.setOnHidden(event -> {
                if (addCreditsSound != null) {
                    System.out.println("Stopping add credits sound - dialog closed");
                    addCreditsSound.stop();
                }
            });

            dialog.showAndWait().ifPresent(amount -> {
                try {
                    int creditAmount = Integer.parseInt(amount);
                    if (creditAmount > 0) {
                        credits += creditAmount;
                        updateCreditsDisplay();
                        infoLabel.setText(creditAmount + " coins added!");
                        if (addCreditsSound != null) {
                            System.out.println("Stopping add credits sound");
                            addCreditsSound.stop();
                        }
                    } else {
                        showError("Please enter a positive number");
                    }
                } catch (NumberFormatException e) {
                    showError("Please enter a valid number");
                }
            });
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: black;");
        dialogPane.setGraphic(null);
        dialogPane.lookupAll(".label").forEach(node -> {
            node.setStyle("-fx-text-fill: white;");
        });
        dialogPane.lookupAll(".button").forEach(node -> {
            node.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        });

        alert.showAndWait();
    }

    @FXML
    private void backToLogin() {
        try {
            userManager.logout();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) reelsPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            infoLabel.setText("Error returning to login page");
        }
    }

    @FXML
    private void onLeverClicked() {
        if (credits >= BET_AMOUNT) {
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(500), betButton);
            rotateTransition.setAxis(Rotate.Z_AXIS);
            rotateTransition.setFromAngle(0);
            rotateTransition.setToAngle(30);
            rotateTransition.setCycleCount(2);
            rotateTransition.setAutoReverse(true);
            rotateTransition.setOnFinished(event -> spinReels());
            rotateTransition.play();
        }
    }
}
