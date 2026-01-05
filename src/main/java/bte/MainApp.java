package bte;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class MainApp extends Application {

    private File currentFile;
    private HTMLEditor editor;

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

        saveAsItem.setOnAction( e -> saveFile(stage));


        // 3. Setup the Scene
        Scene scene = new Scene(root, 1000, 700);
        scene.setOnKeyPressed( event -> {
            if (event.isControlDown()  && event.getCode() == KeyCode.S) {
                saveOrSaveAs(stage);
                event.consume();
            }
        });
        stage.setTitle("Burak's Word Application");
        stage.setScene(scene);
        stage.show();
    }

    private void saveOrSaveAs(Stage stage) {
        if (currentFile != null) {
            try {
                Files.writeString(currentFile.toPath(),editor.getHtmlText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            saveFile(stage);
        }
    }

    private void openFile(Stage stage) {
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
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}