package bte;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class ContentInserter {

    public static void insertImage(CustomEditor editor, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            ResizableImageView imageView = new ResizableImageView(image, 400);

            editor.insertImage(imageView);
        }
    }

    public static void insertTable(CustomEditor editor) {
        // Create a simple 3x3 table initially
        GridPane table = new GridPane();
        table.setHgap(5);
        table.setVgap(5);
        table.setStyle("-fx-border-color: #ccc; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                TextField cell = new TextField();
                cell.setPromptText("Cell " + (row + 1) + "," + (col + 1));
                cell.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
                GridPane.setHgrow(cell, Priority.ALWAYS);
                table.add(cell, col, row);
            }
        }

        ResizableTableView resizableTable = new ResizableTableView(table, 400, 200);
        editor.insertTable(resizableTable);
    }
}
