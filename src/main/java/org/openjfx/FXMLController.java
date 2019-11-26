package org.openjfx;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private ChoiceBox<String> combo;
    @FXML
    private Button down;
    @FXML
    private Label result;
    @FXML
    private StackPane stack;
    @FXML
    private Label dropped;

    private java.io.File file;
    private int start = 0;
    private int end = 10;
    private ObservableList<String> observableList = FXCollections.observableArrayList();
    String filename;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /////////////////////////////
        Label label2 = new Label("Drag a file to me.");
        Label dropped = new Label("");
        VBox dragTarget = new VBox();
        dragTarget.getChildren().addAll(label2,dropped);
        dragTarget.setOnDragOver(new EventHandler<DragEvent>() {

            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != dragTarget
                        && event.getDragboard().hasFiles()) {
                    /* allow for both copying and moving, whatever user chooses */
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
                    dropped.setText(db.getFiles().toString());
                    filename=db.getFiles().get(0).toString();
                    success = true;
                }
                /* let the source know whether the string was successfully
                 * transferred and used */
                event.setDropCompleted(success);

                event.consume();
            }
        });


        stack.getChildren().add(dragTarget);
//        primaryStage.setTitle("Drag Test");
//        primaryStage.setScene(scene);
//        primaryStage.show();
        ///////////////////////////////

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        observableList = ListController.setItems(start, end, combo);
    }

    public void download(ActionEvent actionEvent) {
        int index = combo.getSelectionModel().getSelectedIndex();

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
        String name2;
        int nm=0;
        for(int i=0;i<filename.length();i++){

            if(filename.charAt(i)==92) {
                nm = i;
            }
        }
        name2=filename.substring(nm+1,filename.length());

        File fileMetadata = new File();
        fileMetadata.setName(name2);
        java.io.File filePath = new java.io.File(filename);
        FileContent mediaContent = new FileContent("image/png", filePath);
        File file = DriveController.service.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
        result.setText("Uploaded successfully.");
    }

    public void checkPages(ActionEvent actionEvent) {
        if (combo.getSelectionModel().getSelectedIndex() == 11) {
            if (end < DriveController.getFiles().size()) {
                start += 10;
                end += 10;
            }
            observableList = ListController.setItems(start, end, combo);
        } else if (combo.getSelectionModel().getSelectedIndex() == 10) {
            if (start >= 10) {
                start -= 10;
                end -= 10;
            }
            observableList = ListController.setItems(start, end, combo);

        }
    }
}
