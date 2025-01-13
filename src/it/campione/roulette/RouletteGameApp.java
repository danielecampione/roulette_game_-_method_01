package it.campione.roulette;

import java.util.Random;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RouletteGameApp extends Application {

    private TextArea outputTextArea;
    private TextArea statsTextArea;
    private ComboBox<Integer> attemptLimitComboBox;
    private Random random;

    // Numeri neri e prima riga della roulette
    private static final int[] BLACK_NUMBERS = { 2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35 };
    private static final int[] FIRST_ROW = { 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36 };

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Roulette Simulation - Black and First Row Bet");

        random = new Random();

        // TextArea per l'output
        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setWrapText(true);

        // TextArea per le statistiche
        statsTextArea = new TextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setWrapText(true);

        // ComboBox per il limite di tentativi
        attemptLimitComboBox = new ComboBox<>();
        attemptLimitComboBox.getItems().addAll(0, 10, 20, 30, 40, 50, 100);
        attemptLimitComboBox.getSelectionModel().selectFirst();

        // Pulsante per avviare la simulazione
        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(e -> startSimulation());

        // Layout
        VBox controlsBox = new VBox(10, new Label("Sum € up to:"), attemptLimitComboBox, startButton);
        controlsBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(outputTextArea);
        root.setRight(controlsBox);
        root.setBottom(statsTextArea);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startSimulation() {
        outputTextArea.clear();
        statsTextArea.clear();

        int attemptLimit = attemptLimitComboBox.getValue();
        int totalProfitLoss = 0;
        StringBuilder output = new StringBuilder();
        StringBuilder stats = new StringBuilder();

        for (int i = 0; i < 100; i++) {
            int number = spinRoulette();
            int result = calculateBetResult(number);

            totalProfitLoss += result;

            output.append(getSymbol(result)).append(" ").append(number).append("\n");

            if (attemptLimit > 0 && i + 1 >= attemptLimit) {
                stats.append("Profit/Loss up to attempt ").append(attemptLimit).append(": ").append(totalProfitLoss)
                        .append("€\n");
                break;
            }
        }

        stats.append("Total Profit/Loss: ").append(totalProfitLoss).append("€\n");
        outputTextArea.setText(output.toString());
        statsTextArea.setText(stats.toString());
    }

    private int spinRoulette() {
        return random.nextInt(37); // Numeri da 0 a 36
    }

    private int calculateBetResult(int number) {
        boolean isBlack = contains(BLACK_NUMBERS, number);
        boolean isFirstRow = contains(FIRST_ROW, number);

        if (isBlack && isFirstRow) {
            return 35; // Vinci tutto
        } else if (isBlack) {
            return 5; // Vinci in parte (colore nero)
        } else if (isFirstRow) {
            return 15; // Vinci in parte (prima riga)
        } else {
            return -25; // Perdi tutto
        }
    }

    private String getSymbol(int result) {
        if (result == 35) {
            return "."; // Vittoria completa
        } else if (result == 5 || result == 15) {
            return "."; // Vittoria parziale
        } else {
            return "X"; // Sconfitta
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

    public static void main(String[] args) {
        launch(args);
    }
}