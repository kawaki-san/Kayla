package com.rtkay.kayla.api.outlook.calendar;

import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.User;
import com.rtkay.kayla.App;
import com.rtkay.kayla.api.outlook.calendar.observer.clock.ObservableDate;
import com.rtkay.kayla.api.outlook.calendar.observer.clock.MyClock;
import com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek.MyDayOfTheOfMonth;
import com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek.MyDayOfTheWeek;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.joda.time.DateTime;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class CalendarDriver {
    @FXML
    private Text txtEventSubject;
    @FXML
    private Text txtEventTime;
    private DateTime dt;
    private int todayDate;
    private static List<Event> events;
    private static int DAYS_IN_A_WEEK = 7;
    private User user;
    private String accessToken;
    private static ObservableList<Node> calendarList;
    private static ObservableList<Node> eventsList;

    public void initialiseClock(MyDayOfTheOfMonth txtDayOfTheMonth, MyDayOfTheWeek txtDayOfTheWeek, MyClock txtCurrentTime) {
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {

                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        DateTime instance = new DateTime();
                        todayDate = instance.getDayOfMonth();
                        txtDayOfTheWeek.setDateValue(instance);
                        txtDayOfTheMonth.setDateValue(instance);
                        clock(txtCurrentTime, txtDayOfTheWeek, txtDayOfTheMonth);

                      /*  final Properties oAuthProperties = new Properties();
                        try {
                            oAuthProperties.load(App.class.getResourceAsStream("api/calendar/oAuth.properties"));
                        } catch (IOException e) {
                            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
                        }
                        final String appId = oAuthProperties.getProperty("app.id");
                        final String[] appScopes = oAuthProperties.getProperty("app.scopes").split(",");
                        // Get an access token
                        OutlookAuth.initialize(appId);
                        accessToken = OutlookAuth.getUserAccessToken(appScopes);
                        // Greet the user
                        user = Graph.getUser(accessToken);
                        System.out.println("Welcome " + user.displayName);
                        System.out.println("Time zone: " + user.mailboxSettings.timeZone);
                        System.out.println();
                        // List the calendar
                        listCalendarEvents(accessToken, user.mailboxSettings.timeZone);*/
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();

    }

    public CalendarDriver() {

    }

    private void clock(MyClock txtCurrentTime, MyDayOfTheWeek txtDayOfTheWeek, MyDayOfTheOfMonth txtDayOfTheMonth) {
        ObservableDate date = new ObservableDate();
        date.registerClockObserver(txtCurrentTime);
        date.registerDayOfTheWeekObserver(txtDayOfTheWeek);
        date.registerDayOfMonthObserver(txtDayOfTheMonth);
        date.dayChanged();
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTime currentTime = new DateTime();
            txtCurrentTime.setDateValue(currentTime);
            date.clockChanged();
            //if different day
            if(currentTime.getDayOfMonth() != todayDate){
                todayDate = currentTime.getDayOfMonth();
                date.dayChanged();
            }
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }


    private static String formatDateTimeTimeZone(DateTimeTimeZone date) {
        LocalDateTime dateTime = LocalDateTime.parse(date.dateTime);

        return dateTime.format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)) +
                " (" + date.timeZone + ")";
    }

    private static void createEvent(String accessToken, String timeZone, Scanner input) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");

        // Prompt for subject
        String subject = "";
        while (subject.isBlank()) {
            System.out.print("Subject (required): ");
            subject = input.nextLine();
        }

        // Prompt for start date/time
        LocalDateTime start = null;
        while (start == null) {
            System.out.print("Start (mm/dd/yyyy hh:mm AM/PM): ");
            String date = input.nextLine();

            try {
                start = LocalDateTime.parse(date, inputFormat);
            } catch (DateTimeParseException dtp) {
                System.out.println("Invalid input, try again.");
            }
        }

        // Prompt for end date/time
        LocalDateTime end = null;
        while (end == null) {
            System.out.print("End (mm/dd/yyyy hh:mm AM/PM): ");
            String date = input.nextLine();

            try {
                end = LocalDateTime.parse(date, inputFormat);
            } catch (DateTimeParseException dtp) {
                System.out.println("Invalid input, try again.");
            }

            if (end.isBefore(start)) {
                System.out.println("End time must be after start time.");
                end = null;
            }
        }

        // Prompt for attendees
        HashSet<String> attendees = new HashSet<String>();
        System.out.print("Would you like to add attendees? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            String attendee = "";
            do {
                System.out.print("Enter an email address (leave blank to finalize the list): ");
                attendee = input.nextLine();

                if (!attendee.isBlank()) {
                    attendees.add(attendee);
                }
            } while (!attendee.isBlank());
        }

        // Prompt for body
        String body = null;
        System.out.print("Would you like to add a body? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            System.out.print("Enter a body: ");
            body = input.nextLine();
        }

        // Confirm input
        System.out.println();
        System.out.println("New event:");
        System.out.println("Subject: " + subject);
        System.out.println("Start: " + start.format(inputFormat));
        System.out.println("End: " + end.format(inputFormat));
        System.out.println("Attendees: " + (attendees.size() > 0 ? attendees.toString() : "NONE"));
        System.out.println("Body: " + (body == null ? "NONE" : body));

        System.out.print("Is this correct? (y/n): ");
        if (input.nextLine().trim().toLowerCase().startsWith("y")) {
            Graph.createEvent(accessToken, timeZone, subject, start, end, attendees, body);
            System.out.println("Event created.");
        } else {
            System.out.println("Canceling.");
        }

        System.out.println();
    }

    private static void listCalendarEvents(String accessToken, String timeZone) {
        ZoneId tzId = GraphToIana.getZoneIdFromWindows("Pacific Standard Time");

        // Get midnight of the first day of the week (assumed Sunday)
        // in the user's timezone, then convert to UTC
        ZonedDateTime startOfWeek = ZonedDateTime.now(tzId)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                .truncatedTo(ChronoUnit.DAYS)
                .withZoneSameInstant(ZoneId.of("UTC"));

        // Add 7 days to get the end of the week
        ZonedDateTime endOfWeek = startOfWeek.plusDays(DAYS_IN_A_WEEK);


        // Get the user's events
        events = Graph.getCalendarView(accessToken,
                startOfWeek, endOfWeek, timeZone);

        System.out.println("Events:");

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            System.out.println("Subject: " + event.subject);
            System.out.println("  Organizer: " + event.organizer.emailAddress.name);
            System.out.println("  Start: " + formatDateTimeTimeZone(event.start));
            System.out.println("  End: " + formatDateTimeTimeZone(event.end));
            try {

                VBox day = FXMLLoader.load(App.class.getResource("layouts/calendar/calendar-day.fxml"));
                VBox dayEvents = FXMLLoader.load(App.class.getResource("layouts/todo/listItem-todo.fxml"));
                //Get text child in the VBox
                if (i == 0) {
                    day.getStyleClass().add("day-selected");
                }
                Platform.runLater(() -> {
                    day.setOnMouseEntered(e ->
                            day.getStyleClass().add("day-selected")
                    );
                    day.setOnMouseExited(e ->
                            day.getStyleClass().remove("day-selected")
                    );
                    Text dayNum = (Text) day.getChildren().get(0);
                    // dayNum.setText(String.valueOf(i));
                    // System.out.println(event.start.dateTime);
                    DateTime currentDay = new DateTime(event.start.dateTime);
                    String dayOfMonth = String.format("%02d", currentDay.getDayOfMonth());
                    dayNum.setText(dayOfMonth);
                    calendarList.add(day);
                    System.out.println("Size: " + events.size());

                    //Events list
                    String hours = String.format("%02d", currentDay.getHourOfDay());
                    String minutes = String.format("%02d", currentDay.getMinuteOfHour());
                    Text eventTime = (Text) dayEvents.getChildren().get(0);
                    eventTime.setText(hours + ":" + minutes);
                    HBox eventContainer = (HBox) dayEvents.getChildren().get(1);
                    Text eventSubject = (Text) eventContainer.getChildren().get(1);
                    eventSubject.setText(event.subject);
                    eventsList.add(dayEvents);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    public void setupCalendarDays(ObservableList<Node> calendarDaysList, HBox hBoxCalendar, ScrollPane scrollPaneCalendar,
                                  ObservableList<Node> todoList, VBox vBoxTodo, ScrollPane scrollPaneTodo) {
        setupCalendaUI(calendarDaysList, hBoxCalendar, scrollPaneCalendar);
        setupEventsUI(todoList, vBoxTodo, scrollPaneTodo);
    }

    private void setupCalendaUI(ObservableList<Node> calendarDaysList, HBox hBoxCalendar, ScrollPane scrollPaneCalendar) {
        Bindings.bindContentBidirectional(calendarDaysList, hBoxCalendar.getChildren());
        scrollPaneCalendar.prefHeightProperty().bind(hBoxCalendar.prefHeightProperty().subtract(5));
        scrollPaneCalendar.setFitToHeight(true);
        calendarList = calendarDaysList;
    }

    private void setupEventsUI(ObservableList<Node> todoList, VBox vBoxTodo, ScrollPane scrollPaneTodo) {
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
