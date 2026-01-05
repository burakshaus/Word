package bte;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class MainApp extends Application {

    private File currentFile;
    private HTMLEditor editor;
    private Label wordCountLabel = new Label("Words: 0");
    private Label charCountLabel = new Label("Chars: 0");
    private boolean isDirty = false;
    @Override
    public void start(Stage stage) {
        // 1. Initialize the HTMLEditor
        // This component includes Bold, Italic, Underline, and Font tools by default
        editor = new HTMLEditor();
        editor.setPrefHeight(600);

        // 2. Use a BorderPane to let the editor fill the window
        BorderPane root = new BorderPane();


        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveAsItem = new MenuItem("Save as");
        openItem.setOnAction( e -> openFile(stage));
        fileMenu.getItems().addAll(newItem, openItem, saveAsItem);
        MenuBar menuBar = new MenuBar(fileMenu);
        root.setTop(menuBar);
        root.setCenter(editor);
        root.setBottom(wordCountLabel);


        saveAsItem.setOnAction( e -> saveFile(stage));

        HBox statusBar = new HBox(70);
        statusBar.setStyle("-fx-padding: 6; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        statusBar.getChildren().addAll(wordCountLabel, charCountLabel);
        root.setBottom(statusBar);
        // 3. Setup the Scene
        Scene scene = new Scene(root, 1000, 700);
        scene.setOnKeyPressed( event -> {
            if (event.isControlDown()  && event.getCode() == KeyCode.S) {
                saveOrSaveAs(stage);
                event.consume();
            }
        });
        newItem.setOnAction( e -> {
            editor.setHtmlText("");
            currentFile = null;
            isDirty = false;
            updateStatus();
        });
        editor.setOnKeyPressed( e -> isDirty = true);
        editor.setOnMouseClicked( e -> isDirty = true);
        stage.setTitle("Burak's Word Application");
        stage.setScene(scene);
        stage.setOnCloseRequest( e -> {
            if (!confirmeDiscard(stage)){
                e.consume();
            }
        });
        stage.show();
    }

    private void saveOrSaveAs(Stage stage) {
        if (currentFile != null) {
            try {
                Files.writeString(currentFile.toPath(),editor.getHtmlText());
                isDirty = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveFile(stage);
        }
    }

    private void openFile(Stage stage) {
        if (!confirmeDiscard(stage)) return;
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Html Files", "*.html")
        );

        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String html = Files.readString(file.toPath());
                editor.setHtmlText(html);
                currentFile = file ;
                updateStatus();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateStatus() {
        String text = editor.getHtmlText()
                .replaceAll("<[^>]*>", "") // strip HTML tags
                .trim();

        int chars = text.length();
        int words = text.isEmpty() ? 0 : text.split("\\s+").length;

        wordCountLabel.setText("Words: " + words);
        charCountLabel.setText("Chars: " + chars);
    }

    private void saveFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Html Files", "*.html")
        );

        File file = chooser.showSaveDialog(stage);
        if (file != null) {
            try{
                Files.writeString(file.toPath(),editor.getHtmlText());
                updateStatus();
                isDirty = false;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean confirmeDiscard(Stage stage) {
        if (!isDirty) return true;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved changes");
        alert.setHeaderText("You have unsaved changes");
        alert.setContentText("Do you want to discard them");
        ButtonType discard = new ButtonType("Discard");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(discard, cancel);

        return alert.showAndWait().orElse(cancel) == discard;
    }
    public static void main(String[] args) {
        launch(args);
    }
}