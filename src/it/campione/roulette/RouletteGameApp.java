package it.campione.roulette;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Explanation of "Method 01"
 * Method 01 is a roulette strategy discovered by watching a YouTube video.
 * The method uses the rectangular view of the roulette table.
 *
 * How It Works
 * You bet EUR 15 on black, so that if you win, you get double the amount bet
 * (x2).
 * Simultaneously, you bet EUR 10 on the final square of the top row (using the
 * rectangular view of the roulette table), which corresponds to the row
 * containing the numbers 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36.
 * This is because there are only four black numbers in the first row, and the
 * strategy aims to exploit this anomaly.
 * Note: You are not betting EUR 10 on each number in the first row, but only on
 * the final square of the first row, so that if you win, you get **three times
 * the amount bet (x3) **.
 * At the same time, you do not bet anything on the number 0.
 * Key Points
 * The method is relatively safe but not 100% reliable, as evidenced by the fact
 * that the 0 is always uncovered.
 * This strategy is useful when you are already at a loss and want to try to
 * recover at least some of your losses, as it has proven effective in helping
 * to bounce back from critical situations.
 * Video Explanation
 * For a more detailed explanation, you can watch the following video:
 * https://www.youtube.com/watch?v=Pe1TskP2Awo
 * Feel free to explore and adapt these strategies for your own use. Good luck!
 * 
 * @author D. Campione
 */
public class RouletteGameApp extends Application {

    Roulette roulette;
    private WebView outputWebView;
    private TextArea statsTextArea;
    private ComboBox<Integer> numberOfSpinsComboBox;
    private ComboBox<Integer> sufficientCapitalComboBox; // ComboBox for the minimum win capital

    // Black numbers and first row of roulette
    private static final int[] BLACK_NUMBERS = { 2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35 };
    private static final int[] FIRST_ROW = { 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36 };

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Roulette Game - Black and First Row Bet");

        roulette = new Roulette();

        // WebView for output (supports HTML)
        outputWebView = new WebView();

        // TextArea for statistics
        statsTextArea = new TextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setWrapText(true);

        // Apply animations on startup
        applyStartupAnimations(statsTextArea);

        // ComboBox for the number of launches
        numberOfSpinsComboBox = new ComboBox<>();
        for (int i = 1; i <= 5500; i++) {
            numberOfSpinsComboBox.getItems().add(i);
        }
        numberOfSpinsComboBox.getSelectionModel().select(99); // Imposta 100 come valore predefinito

        // ComboBox for the minimum win capital
        sufficientCapitalComboBox = new ComboBox<>();
        sufficientCapitalComboBox.getItems().addAll(0, 25, 50, 60, 75, 90, 100, 150, 200); // Valori di esempio
        sufficientCapitalComboBox.getSelectionModel().selectFirst(); //  Set 0 as the default value

        // Button to start simulation
        Button startButton = new Button("Avvia Simulazione");
        startButton.getStyleClass().add("button"); // Apply CSS styling
        startButton.setOnAction(e -> startSimulation());
        applyButtonEffects(startButton); // Apply Graphic Effects

        // Layout
        VBox controlsBox = new VBox(10, new Label("Numero di lanci nella serie:"), numberOfSpinsComboBox,
                new Label("Capitale minimo di vittoria:"), sufficientCapitalComboBox, startButton);
        controlsBox.setPadding(new Insets(10));

        // Apply animation to ComboBoxes
        applyComboBoxAnimation(numberOfSpinsComboBox);
        applyComboBoxAnimation(sufficientCapitalComboBox);

        BorderPane root = new BorderPane();
        root.setCenter(outputWebView);
        root.setRight(controlsBox);
        root.setBottom(statsTextArea);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Carica il file CSS
        primaryStage.setScene(scene);

        // Closing event management
        primaryStage.setOnCloseRequest(event -> {
            event.consume(); // Consume the event to handle it manually
            applyExitAnimations(primaryStage); // Start exit animations
        });

        primaryStage.show();
    }

    private void applyStartupAnimations(TextArea textArea) {
        // Fade-in animation
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), textArea);
        fadeTransition.setFromValue(0.0); // Transparent
        fadeTransition.setToValue(1.0); // Visible

        // Scaling animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1000), textArea);
        scaleTransition.setFromX(0.8); // Initial scale
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.0); // Final scale
        scaleTransition.setToY(1.0);

        // Spin animation
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), textArea);
        rotateTransition.setByAngle(360); // Rotate 360 degrees

        // Run animations in parallel
        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition,
                rotateTransition);
        parallelTransition.play();
    }

    private void applyExitAnimations(Stage primaryStage) {
        // Fade-out animation
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), primaryStage.getScene().getRoot());
        fadeTransition.setFromValue(1.0); // Visible
        fadeTransition.setToValue(0.0); // Transparent

        // Scaling animation
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1000), primaryStage.getScene().getRoot());
        scaleTransition.setFromX(1.0); // Initial scale
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.5); // Final scale
        scaleTransition.setToY(0.5);

        // Spin animation
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000),
                primaryStage.getScene().getRoot());
        rotateTransition.setByAngle(360); // Rotate 360 degrees

        // Run animations in parallel
        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition,
                rotateTransition);
        parallelTransition.setOnFinished(e -> primaryStage.close()); // Close the application at the end
        parallelTransition.play();
    }

    private void startSimulation() {
        // Add neon effect to TextArea
        addNeonEffect(statsTextArea);

        outputWebView.getEngine().loadContent(""); // Clean up the previous output
        statsTextArea.clear();

        int numberOfSpins = numberOfSpinsComboBox.getValue();
        int sufficientCapital = sufficientCapitalComboBox.getValue();
        int totalProfitLoss = 0;
        int maxProfit = Integer.MIN_VALUE; // Variable for maximum gain
        StringBuilder output = new StringBuilder();
        StringBuilder stats = new StringBuilder();
        String maxProfitLine = ""; // Store the row with the highest gain
        int maxProfitIndex = -1; // Stores the index of the row with the highest gain

        // Open HTML content
        output.append("<html><body style='font-family: Courier New; font-size: 12px;'>");

        for (int i = 0; i < numberOfSpins; i++) {
            int number = roulette.spin();
            int result = calculateBetResult(number);
            totalProfitLoss += result;

            // Upgrade Maximum Gain
            if (totalProfitLoss > maxProfit) {
                maxProfit = totalProfitLoss;
                maxProfitIndex = i; // Stores the index of the row with the highest gain
            }

            // Shot details
            String color = getColor(number);
            String parity = getParity(number);
            String range = getRange(number);
            String situation = getSituation(result);
            String profitLoss = (result >= 0) ? "Guadagno: " + result + "€" : "Perdita: " + Math.abs(result) + "€";

            //  Create the output row
            String line = getSymbol(result) + " " + number + " | Colore: " + color + " | Parità: " + parity
                    + " | Range: " + range + " | Situazione: " + situation + " | " + profitLoss + " | Totale: "
                    + totalProfitLoss + "€<br>";

            // Store the row with the highest gain
            if (totalProfitLoss == maxProfit) {
                maxProfitLine = line;
            }

            // Add the line to the output with the appropriate color
            if (totalProfitLoss < 0) {
                output.append("<span style='color:red;'>").append(line).append("</span>"); // Red
            } else if (sufficientCapital > 0 && totalProfitLoss >= sufficientCapital) {
                output.append("<span style='color:blue;'>").append(line).append("</span>"); // Blue
            } else {
                output.append("<span style='color:black;'>").append(line).append("</span>"); // Black
            }
        }

        // Close HTML Content
        output.append("</body></html>");

        // Add maximum gain to stats
        stats.append("Massimo guadagno raggiunto: ").append(maxProfit).append("€\n");
        stats.append("Posizione del massimo guadagno: ").append(maxProfitIndex + 1).append("\n"); // +1 Human Index
        stats.append("Profitto/Perdita totale: ").append(totalProfitLoss).append("€\n");

        //  Highlight the row with the highest gain
        String highlightedLine = "<span style='background-color: #F0E68C; font-weight: bold; color: black;'>"
                + maxProfitLine + "</span>";
        String finalOutput = output.toString().replace(maxProfitLine, highlightedLine);

        outputWebView.getEngine().loadContent(finalOutput); // Load HTML content with the highlighted line
        statsTextArea.setText(stats.toString());

        // Remove the neon effect at the end of the simulation
        removeNeonEffect(statsTextArea);
    }

    private int calculateBetResult(int number) {
        boolean isBlack = contains(BLACK_NUMBERS, number);
        boolean isFirstRow = contains(FIRST_ROW, number);

        if (isBlack && isFirstRow) {
            return 35; // Win all
        } else if (isBlack) {
            return 5; // Win in part (black color)
        } else if (isFirstRow) {
            return 15; // Win in part (first row)
        } else {
            return -25; // Lose it all
        }
    }

    private String getSymbol(int result) {
        if (result == 35) {
            return "."; // Complete victory
        } else if (result == 5 || result == 15) {
            return "."; // Partial win
        } else {
            return "X"; // Defeat
        }
    }

    private String getColor(int number) {
        if (number == 0) {
            return "Verde";
        } else if (contains(BLACK_NUMBERS, number)) {
            return "Nero";
        } else {
            return "Rosso";
        }
    }

    private String getParity(int number) {
        if (number == 0) {
            return "N/A";
        } else if (number % 2 == 0) {
            return "Pari";
        } else {
            return "Dispari";
        }
    }

    private String getRange(int number) {
        if (number == 0) {
            return "N/A";
        } else if (number >= 1 && number <= 18) {
            return "Basso";
        } else {
            return "Alto";
        }
    }

    private String getSituation(int result) {
        if (result == 35) {
            return "Vittoria totale";
        } else if (result == 5) {
            return "Vittoria parziale (colore nero)";
        } else if (result == 15) {
            return "Vittoria parziale (prima riga)";
        } else {
            return "Perdita totale";
        }
    }

    private boolean contains(int[] array, int value) {
        for (int num : array) {
            if (num == value) {
                return true;
            }
        }
        return false;
    }

    private void applyButtonEffects(Button button) {
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: #45a049; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.2), 10, 0, 0, 5); -fx-cursor: hand;");
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.1);
            scaleTransition.setToY(1.1);
            scaleTransition.play();
        });
        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5px; -fx-padding: 10 20; -fx-font-size: 14px;");
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
        button.setOnMousePressed(e -> {
            RotateTransition rotateTransition = new RotateTransition(Duration.millis(100), button);
            rotateTransition.setByAngle(5);
            rotateTransition.setCycleCount(2);
            rotateTransition.setAutoReverse(true);
            rotateTransition.play();
        });
    }

    private void applyComboBoxAnimation(ComboBox<?> comboBox) {
        comboBox.setOnAction(e -> {
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), comboBox);
            scaleTransition.setFromX(1.0);
            scaleTransition.setFromY(1.0);
            scaleTransition.setToX(1.1); // Enlarges by 10%
            scaleTransition.setToY(1.1); // Enlarges by 10%
            scaleTransition.setAutoReverse(true); // Back to original size
            scaleTransition.setCycleCount(2); // Run the animation twice (forward and backward)
            scaleTransition.play();
        });
    }

    private void addNeonEffect(TextArea textArea) {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.TRANSPARENT); // Start with a transparent color

        textArea.setEffect(innerShadow);

        // Animation to make the neon effect appear
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(innerShadow.colorProperty(), Color.TRANSPARENT)),
                new KeyFrame(Duration.seconds(1), new KeyValue(innerShadow.colorProperty(), Color.BLUE)) // Neon color
        );
        timeline.play();
    }

    private void removeNeonEffect(TextArea textArea) {
        InnerShadow innerShadow = (InnerShadow) textArea.getEffect();

        if (innerShadow == null) {
            innerShadow = new InnerShadow();
            innerShadow.setColor(Color.BLUE); // Set the starting color
            textArea.setEffect(innerShadow);
        }

        // Animation to make the neon effect disappear
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(innerShadow.colorProperty(), Color.BLUE)),
                new KeyFrame(Duration.seconds(1), new KeyValue(innerShadow.colorProperty(), Color.TRANSPARENT)));
        timeline.setOnFinished(e -> {
            textArea.setEffect(null); // Remove the effect
            textArea.setStyle(""); // Restore the original style
        });
        timeline.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}