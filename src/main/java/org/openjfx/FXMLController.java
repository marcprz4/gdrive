package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {
    static private String selectedType;
    static private boolean ok = false;
    String filename;
    @FXML
    private ChoiceBox<String> types;
    @FXML
    private Label label;
    @FXML
    private ListView<String> combo;
    @FXML
    private Button down;
    @FXML
    private Label result;
    @FXML
    private StackPane stack;
    @FXML
    private Label dropped;
    @FXML
    private Button load;
    @FXML
    private TextField size;
    private java.io.File file;
    private int start = 0;
    private int end = 10;
    private int add = 0;
    private ObservableList<String> observableList = FXCollections.observableArrayList();

    public static boolean isOk() {
        return ok;
    }

    public static void setOk(boolean ok) {
        FXMLController.ok = ok;
    }

    public static String getSelectedType() {
        return selectedType;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            DriveController.run();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        ObservableList<String> typesList = FXCollections.observableArrayList("Folders", "Files", "Photos");
        types.setItems(typesList);
        selectedType = types.getSelectionModel().toString();
        Label label2 = new Label("Drag a file here to upload.");
        Label dropped = new Label("");
        VBox dragTarget = new VBox();
        dragTarget.getChildren().addAll(label2, dropped);
        dragTarget.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != dragTarget
                        && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();
            }
        });

        dragTarget.setOnDragDropped(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    String name = NameCutter.cut(db.getFiles().toString());
                    dropped.setText(name.substring(0, name.length() - 1));
//                    fileList.getItems().add();
                    filename = db.getFiles().get(0).toString();
                    success = true;
                }
                event.setDropCompleted(success);

                event.consume();
            }
        });


        stack.getChildren().add(dragTarget);

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");

    }

    public void download(ActionEvent actionEvent) {
        load.getStyleClass().add("reset");
        int index = (combo.getSelectionModel().getSelectedIndex()) + add;

        boolean good;
        try {
            good = DriveController.download(index);
        } catch (IOException e) {
            good = false;
            e.printStackTrace();
        }
        if (good) {
            result.setText("Downloaded successfully.");
        } else
            result.setText("Download error.");
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        load.getStyleClass().add("reset");
        DriveController.upload(filename);
        result.setText("Uploaded successfully.");
    }

    public void load(ActionEvent actionEvent) throws IOException {
        DriveController.listFiles(types.getSelectionModel().getSelectedItem());
        observableList = FXCollections.observableList(DriveController.fileToString());
        combo.getItems().clear();

        String listSize = size.getText();
        boolean isInt = IntegerChecker.check(listSize);
        if (isInt) {
            combo.getItems().addAll(observableList.subList(0, 0 + Integer.parseInt(listSize)));
        } else {
            combo.getItems().addAll(observableList);
        }
        result.setText("Files loaded successfully.");
    }
}
