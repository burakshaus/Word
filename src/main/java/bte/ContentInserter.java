package bte;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ContentInserter {

    public static void insertImage(CustomEditor editor, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Resim Seç");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            ResizableImageView imageView = new ResizableImageView(image, 400);
            editor.insertImage(imageView);
        }
    }

    public static void openTablePicker(CustomEditor editor, Node sourceButton) {
        TablePickerPopup popup = new TablePickerPopup((rows, cols) -> {
            insertTable(editor, rows, cols);
        });

        double x = sourceButton.localToScreen(0, 0).getX();
        double y = sourceButton.localToScreen(0, 0).getY() + sourceButton.getBoundsInLocal().getHeight();
        popup.show(sourceButton.getScene().getWindow(), x, y);
    }

    public static void insertTable(CustomEditor editor, int rows, int cols) {
        GridPane table = new GridPane();
        table.setHgap(1);
        table.setVgap(1);
        // Tablo kenarlıkları ve arka planı
        table.setStyle("-fx-border-color: #ccc; -fx-padding: 1; -fx-background-color: #ddd;");

        // Tabloyu boyutlandırılabilir kapsayıcıya al
        ResizableTableView resizableTable = new ResizableTableView(table, cols * 100, rows * 30);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TextField cell = new TextField();
                cell.setStyle("-fx-background-color: white; -fx-border-color: #eee; -fx-border-width: 0.5;");
                GridPane.setHgrow(cell, Priority.ALWAYS);

                // --- SAĞ TIK MENÜSÜ ---
                final int currentRow = row;
                final int currentCol = col;

                ContextMenu contextMenu = new ContextMenu();

                MenuItem deleteRowItem = new MenuItem("Satırı Sil");
                deleteRowItem.setOnAction(e -> deleteRow(table, currentRow));

                MenuItem deleteColItem = new MenuItem("Sütunu Sil");
                deleteColItem.setOnAction(e -> deleteCol(table, currentCol));

                MenuItem deleteTableItem = new MenuItem("Tabloyu Sil");
                deleteTableItem.setOnAction(e -> {
                    // Otomatik silme yöntemi: Tablonun içinde olduğu paneli bul ve oradan sil
                    if (resizableTable.getParent() != null) {
                        ((Pane) resizableTable.getParent()).getChildren().remove(resizableTable);
                    }
                });

                contextMenu.getItems().addAll(deleteRowItem, deleteColItem, new SeparatorMenuItem(), deleteTableItem);

                // Hücreye menüyü ata (TextField'ın kendi menüsünü ezer)
                cell.setContextMenu(contextMenu);
                // ---------------------

                table.add(cell, col, row);
            }
        }

        editor.insertTable(resizableTable);
    }

    // --- YARDIMCI SİLME FONKSİYONLARI ---

    private static void deleteRow(GridPane grid, int rowIdx) {
        List<Node> nodesToRemove = new ArrayList<>();
        List<Node> nodesToShift = new ArrayList<>();

        for (Node child : grid.getChildren()) {
            Integer rowIndex = GridPane.getRowIndex(child);
            // GridPane'de null index 0 demektir, garantiye alalım
            int r = (rowIndex == null) ? 0 : rowIndex;

            if (r == rowIdx) {
                nodesToRemove.add(child);
            } else if (r > rowIdx) {
                nodesToShift.add(child);
            }
        }

        grid.getChildren().removeAll(nodesToRemove);
        // Alt satırları yukarı kaydır
        for (Node node : nodesToShift) {
            Integer r = GridPane.getRowIndex(node);
            GridPane.setRowIndex(node, (r == null ? 0 : r) - 1);
        }
    }

    private static void deleteCol(GridPane grid, int colIdx) {
        List<Node> nodesToRemove = new ArrayList<>();
        List<Node> nodesToShift = new ArrayList<>();

        for (Node child : grid.getChildren()) {
            Integer colIndex = GridPane.getColumnIndex(child);
            int c = (colIndex == null) ? 0 : colIndex;

            if (c == colIdx) {
                nodesToRemove.add(child);
            } else if (c > colIdx) {
                nodesToShift.add(child);
            }
        }

        grid.getChildren().removeAll(nodesToRemove);
        // Sağ sütunları sola kaydır
        for (Node node : nodesToShift) {
            Integer c = GridPane.getColumnIndex(node);
            GridPane.setColumnIndex(node, (c == null ? 0 : c) - 1);
        }
    }
}