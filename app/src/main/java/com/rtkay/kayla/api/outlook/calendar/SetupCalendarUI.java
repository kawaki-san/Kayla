package com.rtkay.kayla.api.outlook.calendar;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SetupCalendarUI {
    protected static ObservableList<Node> calendarList;
    public static ObservableList<Node> eventsList;
    public SetupCalendarUI() {
    }

   public void setupCalendarUI(ObservableList<Node> calendarDaysList, HBox hBoxCalendar, ScrollPane scrollPaneCalendar) {
        Bindings.bindContentBidirectional(calendarDaysList, hBoxCalendar.getChildren());
        scrollPaneCalendar.prefHeightProperty().bind(hBoxCalendar.prefHeightProperty().subtract(5));
        scrollPaneCalendar.setFitToHeight(true);
        calendarList = calendarDaysList;
    }

    public  void setupEventsUI(ObservableList<Node> todoList, VBox vBoxTodo, ScrollPane scrollPaneTodo) {
        Bindings.bindContentBidirectional(todoList, vBoxTodo.getChildren());
        scrollPaneTodo.prefWidthProperty().bind(vBoxTodo.prefWidthProperty().subtract(5));
        scrollPaneTodo.setFitToWidth(true);
        eventsList = todoList;

        //scroll to bottom of list
       /* todoList.addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    scrollPaneTodo.vvalueProperty().bind(vBoxTodo.heightProperty());
                }
            }
        });*/
    }


}