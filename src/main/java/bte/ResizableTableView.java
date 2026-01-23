package bte;

import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ResizableTableView extends StackPane {

    private final GridPane table;
    private final Rectangle selectionBorder;
    private final Rectangle topLeftHandle;
    private final Rectangle topRightHandle;
    private final Rectangle bottomLeftHandle;
    private final Rectangle bottomRightHandle;

    private boolean isSelected = false;
    private double dragStartX, dragStartY;
    private double originalWidth, originalHeight;
    private ResizeMode resizeMode = ResizeMode.NONE;

    private enum ResizeMode {
        NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public ResizableTableView(GridPane table, double initialWidth, double initialHeight) {
        this.table = table;
        this.table.setPrefWidth(initialWidth);
        this.table.setPrefHeight(initialHeight);

        // Selection border
        this.selectionBorder = new Rectangle();
        this.selectionBorder.setFill(Color.TRANSPARENT);
        this.selectionBorder.setStroke(Color.rgb(33, 150, 243)); // Material Blue
        this.selectionBorder.setStrokeWidth(2);
        this.selectionBorder.setVisible(false);

        // Resize handles
        double handleSize = 8;
        this.topLeftHandle = createHandle(handleSize);
        this.topRightHandle = createHandle(handleSize);
        this.bottomLeftHandle = createHandle(handleSize);
        this.bottomRightHandle = createHandle(handleSize);

        // Add children
        this.getChildren().addAll(table, selectionBorder,
                topLeftHandle, topRightHandle, bottomLeftHandle, bottomRightHandle);

        // Setup event handlers
        setupEventHandlers();
        updateLayout();
    }

    private Rectangle createHandle(double size) {
        Rectangle handle = new Rectangle(size, size);
        handle.setFill(Color.rgb(33, 150, 243));
        handle.setStroke(Color.WHITE);
        handle.setStrokeWidth(1);
        handle.setVisible(false);
        return handle;
    }

    private void setupEventHandlers() {
        // Click to select
        this.setOnMouseClicked(e -> {
            setSelected(true);
            e.consume();
        });

        // Handle resize from corners
        setupHandleDrag(topLeftHandle, ResizeMode.TOP_LEFT, Cursor.NW_RESIZE);
        setupHandleDrag(topRightHandle, ResizeMode.TOP_RIGHT, Cursor.NE_RESIZE);
        setupHandleDrag(bottomLeftHandle, ResizeMode.BOTTOM_LEFT, Cursor.SW_RESIZE);
        setupHandleDrag(bottomRightHandle, ResizeMode.BOTTOM_RIGHT, Cursor.SE_RESIZE);

        // Keyboard delete
        this.setFocusTraversable(true);
        this.setOnKeyPressed(this::handleKeyPress);

        // Listen for size changes
        table.prefWidthProperty().addListener((obs, oldVal, newVal) -> updateLayout());
        table.prefHeightProperty().addListener((obs, oldVal, newVal) -> updateLayout());
    }

    private void setupHandleDrag(Rectangle handle, ResizeMode mode, Cursor cursor) {
        handle.setCursor(cursor);

        handle.setOnMousePressed(e -> {
            resizeMode = mode;
            dragStartX = e.getScreenX();
            dragStartY = e.getScreenY();
            originalWidth = table.getPrefWidth();
            originalHeight = table.getPrefHeight();
            e.consume();
        });

        handle.setOnMouseDragged(e -> {
            double deltaX = e.getScreenX() - dragStartX;
            double deltaY = e.getScreenY() - dragStartY;

            double newWidth = originalWidth;
            double newHeight = originalHeight;

            switch (resizeMode) {
                case TOP_LEFT:
                    newWidth = originalWidth - deltaX;
                    newHeight = originalHeight - deltaY;
                    break;
                case TOP_RIGHT:
                    newWidth = originalWidth + deltaX;
                    newHeight = originalHeight - deltaY;
                    break;
                case BOTTOM_LEFT:
                    newWidth = originalWidth - deltaX;
                    newHeight = originalHeight + deltaY;
                    break;
                case BOTTOM_RIGHT:
                    newWidth = originalWidth + deltaX;
                    newHeight = originalHeight + deltaY;
                    break;
                case NONE:
                    break;
            }

            // Minimum size constraint
            if (newWidth > 100) {
                table.setPrefWidth(newWidth);
            }
            if (newHeight > 100) {
                table.setPrefHeight(newHeight);
            }

            updateLayout();
            e.consume();
        });

        handle.setOnMouseReleased(e -> {
            resizeMode = ResizeMode.NONE;
            e.consume();
        });
    }

    private void handleKeyPress(KeyEvent e) {
        if (isSelected && (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE)) {
            // Remove from parent
            if (this.getParent() != null) {
                ((StackPane) this.getParent()).getChildren().remove(this);
            }
            e.consume();
        }
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        selectionBorder.setVisible(selected);
        topLeftHandle.setVisible(selected);
        topRightHandle.setVisible(selected);
        bottomLeftHandle.setVisible(selected);
        bottomRightHandle.setVisible(selected);

        if (selected) {
            this.requestFocus();
        }
    }

    private void updateLayout() {
        double width = table.getPrefWidth();
        double height = table.getPrefHeight();

        // Update border
        selectionBorder.setWidth(width + 4);
        selectionBorder.setHeight(height + 4);

        // Update handle positions
        double handleSize = 8;
        double halfHandle = handleSize / 2;

        topLeftHandle.setTranslateX(-width / 2 - halfHandle);
        topLeftHandle.setTranslateY(-height / 2 - halfHandle);

        topRightHandle.setTranslateX(width / 2 - halfHandle);
        topRightHandle.setTranslateY(-height / 2 - halfHandle);

        bottomLeftHandle.setTranslateX(-width / 2 - halfHandle);
        bottomLeftHandle.setTranslateY(height / 2 - halfHandle);

        bottomRightHandle.setTranslateX(width / 2 - halfHandle);
        bottomRightHandle.setTranslateY(height / 2 - halfHandle);
    }

    public GridPane getTable() {
        return table;
    }
}
