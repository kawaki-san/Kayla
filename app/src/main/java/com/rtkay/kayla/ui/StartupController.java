package com.rtkay.kayla.ui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartupController implements Initializable {
    @FXML
    private HBox hBoxCalendar;
    @FXML
    private ScrollPane scrollPaneCalendar;
    private ObservableList<Node> calendarDaysList = FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Bindings.bindContentBidirectional(calendarDaysList, hBoxCalendar.getChildren());
        scrollPaneCalendar.prefHeightProperty().bind(hBoxCalendar.prefHeightProperty().subtract(5));
        scrollPaneCalendar.setFitToHeight(true);
        calendarDaysList.addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    scrollPaneCalendar.hvalueProperty().bind(hBoxCalendar.widthProperty());
                }
            }
        });

        for (int i = 0; i <7 ; i++) {
            try {
                Node day = FXMLLoader.load(getClass().getResource("../layouts/calendar/calendar-day.fxml"));
                calendarDaysList.add(day);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
