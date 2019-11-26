package org.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

public class ListController {
    private static ObservableList<String> list = FXCollections.observableArrayList();

    public static ObservableList<String> setItems(int start, int end, ChoiceBox<String> combo) {
        list = FXCollections.observableArrayList(DriveController.fileToString(DriveController.getFiles().subList(start, end)));
        list.add("previous...page");
        list.add("next...page");
        combo.setItems(list);
        combo.getSelectionModel().clearSelection();
        return list;
    }
}
