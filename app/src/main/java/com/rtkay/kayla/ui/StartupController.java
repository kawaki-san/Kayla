package com.rtkay.kayla.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.rtkay.kayla.api.outlook.calendar.CalendarDriver;
import com.rtkay.kayla.api.outlook.calendar.GetCalendar;
import com.rtkay.kayla.api.outlook.calendar.SetupCalendarUI;
import com.rtkay.kayla.api.outlook.calendar.observer.clock.MyClock;
import com.rtkay.kayla.api.outlook.calendar.observer.days.MyCustomDate;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StartupController implements Initializable {
    @FXML
    private JFXToggleButton toggleEvents;
    @FXML
    private Gauge progressGauge;
    @FXML
    private Text txtTaskCount;
    @FXML
    private JFXButton btnCloseWindow;
    @FXML
    private JFXButton btnMinimiseWindow;
    @FXML
    private MyClock txtTime;
    @FXML
    private MyCustomDate txtCustomDate;

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
    private CalendarDriver calendarDriver;
    private final SetupCalendarUI setupCalendarUI = new SetupCalendarUI();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCloseWindow.setOnMouseClicked(event -> Platform.exit());
        btnMinimiseWindow.setOnMouseClicked(event -> Platform.runLater(() -> {
            Stage stage = (Stage) btnCloseWindow.getScene().getWindow();
            stage.setIconified(true);
        }));

        calendarDriver = new CalendarDriver();
        calendarDriver.initialiseClock(txtCustomDate, txtTime);
        setupCalendarUI.setupCalendarUI(calendarDaysList, hBoxCalendar, scrollPaneCalendar);
        setupCalendarUI.setupEventsUI(todoList, vBoxTodo, scrollPaneTodo);
        GetCalendar.setTaskCount(txtTaskCount);
        GetCalendar.setProgressGauge(progressGauge);
        scrollPaneTodo.managedProperty().bind(scrollPaneTodo.visibleProperty());
        toggleEvents.selectedProperty().addListener(tog->{
            if(toggleEvents.isSelected()){
                scrollPaneTodo.setVisible(true);
            } else {
                scrollPaneTodo.setVisible(false);
            }
        });
    }


}
