package bte;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;

import java.util.function.BiConsumer;

public class TablePickerPopup extends Popup {

    private final int MAX_ROWS = 10;
    private final int MAX_COLS = 10;
    private final Rectangle[][] gridCells = new Rectangle[MAX_ROWS][MAX_COLS];
    private final Label statusLabel = new Label("0 x 0 Tablo");

    public TablePickerPopup(BiConsumer<Integer, Integer> onTableSelected) {
        VBox root = new VBox(5);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0); -fx-border-color: #ccc; -fx-border-radius: 3;");

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);

        // 10x10 Izgarayı Oluştur
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLS; col++) {
                Rectangle rect = new Rectangle(20, 20, Color.WHITE);
                rect.setStroke(Color.LIGHTGRAY);

                final int r = row;
                final int c = col;

                // Fare üzerindeyken boyama mantığı
                rect.setOnMouseEntered(e -> updateGridVisuals(r, c));

                // Tıklayınca tabloyu oluştur
                rect.setOnMouseClicked(e -> {
                    onTableSelected.accept(r + 1, c + 1); // +1 çünkü index 0'dan başlar
                    hide();
                });

                gridCells[row][col] = rect;
                grid.add(rect, col, row);
            }
        }

        root.getChildren().addAll(grid, statusLabel);
        getContent().add(root);
        setAutoHide(true); // Dışarı tıklayınca kapansın
    }

    private void updateGridVisuals(int targetRow, int targetCol) {
        statusLabel.setText((targetRow + 1) + " x " + (targetCol + 1) + " Tablo");

        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLS; col++) {
                if (row <= targetRow && col <= targetCol) {
                    // Seçili alan (Word mavisi)
                    gridCells[row][col].setFill(Color.web("#e1f0fa")); // Açık mavi
                    gridCells[row][col].setStroke(Color.web("#2b579a")); // Koyu mavi kenar
                } else {
                    // Seçili olmayan alan
                    gridCells[row][col].setFill(Color.WHITE);
                    gridCells[row][col].setStroke(Color.LIGHTGRAY);
                }
            }
        }
    }
}