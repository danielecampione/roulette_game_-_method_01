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
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class RouletteGameApp extends Application {

    private WebView outputWebView;
    private TextArea statsTextArea;
    private ComboBox<Integer> numberOfSpinsComboBox;
    private ComboBox<Integer> sufficientCapitalComboBox; // ComboBox per il capitale minimo di vittoria
    private Random random;

    // Numeri neri e prima riga della roulette
    private static final int[] BLACK_NUMBERS = { 2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35 };
    private static final int[] FIRST_ROW = { 3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36 };

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Roulette Game - Black and First Row Bet");

        random = new Random();

        // WebView per l'output (supporta HTML)
        outputWebView = new WebView();

        // TextArea per le statistiche
        statsTextArea = new TextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setWrapText(true);

        // ComboBox per il numero di lanci
        numberOfSpinsComboBox = new ComboBox<>();
        for (int i = 1; i <= 5500; i++) {
            numberOfSpinsComboBox.getItems().add(i);
        }
        numberOfSpinsComboBox.getSelectionModel().select(99); // Imposta 100 come valore predefinito

        // ComboBox per il capitale minimo di vittoria
        sufficientCapitalComboBox = new ComboBox<>();
        sufficientCapitalComboBox.getItems().addAll(0, 25, 50, 60, 75, 90, 100, 150, 200); // Valori di esempio
        sufficientCapitalComboBox.getSelectionModel().selectFirst(); // Imposta 0 come valore predefinito

        // Pulsante per avviare la simulazione
        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(e -> startSimulation());

        // Layout
        VBox controlsBox = new VBox(10, new Label("Numero di lanci nella serie:"), numberOfSpinsComboBox,
                new Label("Capitale minimo di vittoria:"), sufficientCapitalComboBox, startButton);
        controlsBox.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setCenter(outputWebView);
        root.setRight(controlsBox);
        root.setBottom(statsTextArea);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startSimulation() {
        outputWebView.getEngine().loadContent(""); // Pulisci l'output precedente
        statsTextArea.clear();

        int numberOfSpins = numberOfSpinsComboBox.getValue();
        int sufficientCapital = sufficientCapitalComboBox.getValue();
        int totalProfitLoss = 0;
        int maxProfit = Integer.MIN_VALUE; // Variabile per il massimo guadagno
        StringBuilder output = new StringBuilder();
        StringBuilder stats = new StringBuilder();
        String maxProfitLine = ""; // Memorizza la riga con il massimo guadagno
        int maxProfitIndex = -1; // Memorizza l'indice della riga con il massimo guadagno

        // Apri il contenuto HTML
        output.append("<html><body style='font-family: Courier New; font-size: 12px;'>");

        for (int i = 0; i < numberOfSpins; i++) {
            int number = spinRoulette();
            int result = calculateBetResult(number);
            totalProfitLoss += result;

            // Aggiorna il massimo guadagno
            if (totalProfitLoss > maxProfit) {
                maxProfit = totalProfitLoss;
                maxProfitIndex = i; // Memorizza l'indice della riga con il massimo guadagno
            }

            // Dettagli del tiro
            String color = getColor(number);
            String parity = getParity(number);
            String range = getRange(number);
            String situation = getSituation(result);
            String profitLoss = (result >= 0) ? "Guadagno: " + result + "€" : "Perdita: " + Math.abs(result) + "€";

            // Crea la riga di output
            String line = getSymbol(result) + " " + number + " | Colore: " + color + " | Parità: " + parity
                    + " | Range: " + range + " | Situazione: " + situation + " | " + profitLoss + " | Totale: "
                    + totalProfitLoss + "€<br>";

            // Memorizza la riga con il massimo guadagno
            if (totalProfitLoss == maxProfit) {
                maxProfitLine = line;
            }

            // Aggiungi la riga all'output con il colore appropriato
            if (totalProfitLoss < 0) {
                output.append("<span style='color:red;'>").append(line).append("</span>"); // Rosso
            } else if (sufficientCapital > 0 && totalProfitLoss >= sufficientCapital) {
                output.append("<span style='color:blue;'>").append(line).append("</span>"); // Blu
            } else {
                output.append("<span style='color:black;'>").append(line).append("</span>"); // Nero
            }
        }

        // Chiudi il contenuto HTML
        output.append("</body></html>");

        // Aggiungi il massimo guadagno alle statistiche
        stats.append("Massimo guadagno raggiunto: ").append(maxProfit).append("€\n");
        stats.append("Posizione del massimo guadagno: ").append(maxProfitIndex + 1).append("\n"); // +1 per l'indice
                                                                                                  // umano
        stats.append("Total Profit/Loss: ").append(totalProfitLoss).append("€\n");

        // Evidenzia la riga con il massimo guadagno
        String highlightedLine = "<span style='background-color: #F0E68C; font-weight: bold; color: black;'>"
                + maxProfitLine + "</span>";
        String finalOutput = output.toString().replace(maxProfitLine, highlightedLine);

        outputWebView.getEngine().loadContent(finalOutput); // Carica il contenuto HTML con la riga evidenziata
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

    public static void main(String[] args) {
        launch(args);
    }
}