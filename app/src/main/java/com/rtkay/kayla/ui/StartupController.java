package com.rtkay.kayla.ui;

import com.rtkay.kayla.api.outlook.calendar.CalendarDriver;
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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartupController implements Initializable {
    @FXML
    private ScrollPane scrollPaneTodo;
    @FXML
    private VBox vBoxTodo;
    @FXML
    private HBox hBoxCalendar;
    @FXML
    private ScrollPane scrollPaneCalendar;
    private ObservableList<Node> calendarDaysList = FXCollections.observableArrayList();
    private ObservableList<Node> todoList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        calendarUISetup();
        todoListUI();
        new CalendarDriver();
    }



    private void todoListUI() {
        Bindings.bindContentBidirectional(todoList, vBoxTodo.getChildren());
        scrollPaneTodo.prefWidthProperty().bind(vBoxTodo.prefWidthProperty().subtract(5));
        scrollPaneTodo.setFitToWidth(true);
        todoList.addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    scrollPaneTodo.vvalueProperty().bind(vBoxTodo.heightProperty());
                }
            }
        });

        for (int i = 0; i < 7; i++) {
            try {
                VBox item = FXMLLoader.load(getClass().getResource("../layouts/todo/listItem-todo.fxml"));
                todoList.add(item);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void calendarUISetup() {
        Bindings.bindContentBidirectional(calendarDaysList, hBoxCalendar.getChildren());
        scrollPaneCalendar.prefHeightProperty().bind(hBoxCalendar.prefHeightProperty().subtract(5));
        scrollPaneCalendar.setFitToHeight(true);
        for (int i = 0; i < 7; i++) {
            try {
                VBox day = FXMLLoader.load(getClass().getResource("../layouts/calendar/calendar-day.fxml"));
                if (i == 0) {
                    day.getStyleClass().add("day-selected");
                }
                Text dayNum = (Text) day.getChildren().get(0);
                dayNum.setText(String.valueOf(i));
                calendarDaysList.add(day);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
