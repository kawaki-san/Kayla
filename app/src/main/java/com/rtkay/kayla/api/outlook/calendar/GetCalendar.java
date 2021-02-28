package com.rtkay.kayla.api.outlook.calendar;

import com.jfoenix.controls.JFXCheckBox;
import com.microsoft.graph.models.extensions.Event;
import com.rtkay.kayla.App;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetCalendar {
    protected static Text activeTask;
    protected static Gauge circleProgress;
    private static int size;
    private static int total;

    static void getCalendarEvents(String accessToken, String timeZone) throws IOException {
        ZoneId tzId = GraphToIana.getZoneIdFromWindows("Pacific Standard Time");

        // Get midnight of the first day of the week (assumed Sunday)
        // in the user's timezone, then convert to UTC
        ZonedDateTime startOfWeek = ZonedDateTime.now(tzId)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                .truncatedTo(ChronoUnit.DAYS)
                .withZoneSameInstant(ZoneId.of("UTC"));

        // Add 7 days to get the end of the week
        ZonedDateTime endOfWeek = startOfWeek.plusDays(CalendarDriver.DAYS_IN_A_WEEK);


        // Get the user's events
        CalendarDriver.eventsThisWeek = Graph.getCalendarView(accessToken,
                startOfWeek, endOfWeek, timeZone);


        //System.out.println("Events:");
        for (Event dayEvent : CalendarDriver.eventsThisWeek) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("E MMM dd");
            DateTime currentDay = new DateTime(dayEvent.start.dateTime);
            LocalDate localDate = currentDay.toLocalDate();
            String fDate = localDate.toString(fmt);
            List<Event> todayEvents = CalendarDriver.daysContainingEvents.get(localDate);
            if (todayEvents == null) {
                todayEvents = new ArrayList<Event>();
            }
            todayEvents.add(dayEvent);
            CalendarDriver.daysContainingEvents.put(localDate, todayEvents);
        }
        System.out.println("Map size is " + CalendarDriver.daysContainingEvents.size()); //outputs 7
        if (CalendarDriver.daysContainingEvents.size() > 0) {
            int count = 0;
            for (Map.Entry<LocalDate, List<Event>> entry : CalendarDriver.daysContainingEvents.entrySet()) {

                VBox day = FXMLLoader.load(App.class.getResource("layouts/calendar/calendar-day.fxml"));
                day.setOnMouseEntered(e ->
                        day.getStyleClass().add("day-selected")
                );
                day.setOnMouseExited(e ->
                        day.getStyleClass().remove("day-selected")
                );
                if (count == 0) {
                    day.getStyleClass().add("day-selected");
                }

                count++;
                List<Event> entries = entry.getValue();
                System.out.println(entry.getKey() + "/" + entry.getValue());
                System.out.println("Events for " + entry.getKey().toString() + " that day are");
                Text dayNum = (Text) day.getChildren().get(0);
                String date = String.format("%02d", entry.getKey().getDayOfMonth());
                dayNum.setText(date);

                day.setOnMouseClicked(event -> {
                    Text dayReading = (Text) day.getChildren().get(0);
                    String s = dayReading.getText();
                    s = s.replaceFirst("^0+(?!$)", "");
                    int dayOfMonth = Integer.parseInt(s);
                    //search through map for that date
                    for (Map.Entry<LocalDate, List<Event>> newEntry : CalendarDriver.daysContainingEvents.entrySet()) {
                        if (newEntry.getKey().getDayOfMonth() == dayOfMonth) {
                            for (Event e : newEntry.getValue()) {
                                VBox dayEvents = null;
                                try {
                                    dayEvents = FXMLLoader.load(App.class.getResource("layouts/todo/listItem-todo.fxml"));
                                    DateTime currentDay = new DateTime(e.start.dateTime);
                                    String hours = String.format("%02d", currentDay.getHourOfDay());
                                    String minutes = String.format("%02d", currentDay.getMinuteOfHour());
                                    Text eventTime = (Text) dayEvents.getChildren().get(0);
                                    eventTime.setText(hours + ":" + minutes);
                                    HBox eventContainer = (HBox) dayEvents.getChildren().get(1);
                                    Text eventSubject = (Text) eventContainer.getChildren().get(1);
                                    eventSubject.setText(e.subject);
                                    VBox finalDayEvents = dayEvents;
                                    Platform.runLater(() -> {
                                        SetupCalendarUI.eventsList.clear();
                                        SetupCalendarUI.eventsList.add(finalDayEvents);
                                    });
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }
                            }
                        }
                    }


                });


                Platform.runLater(() -> SetupCalendarUI.calendarList.add(day));
                for (Event e : entries) {
                    System.out.println(e.subject);
                }
                System.out.println("-------------------------------------------------");
            }
            Map.Entry<LocalDate, List<Event>> entry = CalendarDriver.daysContainingEvents.entrySet().iterator().next();
            //  LocalDate key = entry.getKey();
            List<Event> value = entry.getValue();

            for (Event event : value) {
                VBox dayEvents = FXMLLoader.load(App.class.getResource("layouts/todo/listItem-todo.fxml"));
                DateTime currentDay = new DateTime(event.start.dateTime);
                String hours = String.format("%02d", currentDay.getHourOfDay());
                String minutes = String.format("%02d", currentDay.getMinuteOfHour());
                Text eventTime = (Text) dayEvents.getChildren().get(0);
                eventTime.setText(hours + ":" + minutes);
                HBox eventContainer = (HBox) dayEvents.getChildren().get(1);
                Text eventSubject = (Text) eventContainer.getChildren().get(1);
                JFXCheckBox selection = (JFXCheckBox) eventContainer.getChildren().get(0);
                size = value.size();
                total = value.size();
                if (size == 0) {
                    activeTask.setText("no tasks");
                } else if (size == 1) {
                    activeTask.setText("1 task");
                } else {
                    activeTask.setText(size + " tasks");
                }
                circleProgress.setValue(0);
                circleProgress.setMinValue(0);
                circleProgress.setMaxValue(100);
                selection.selectedProperty().addListener(d -> {
                    if (selection.isSelected()) {
                        size = size-1;
                    }
                    if (!selection.isSelected()) {
                        size = size+1;
                    }

                    if (size == 0) {
                        activeTask.setText("no tasks");
                    } else if (size == 1) {
                        activeTask.setText("1 task");
                    } else {
                        activeTask.setText(size + " tasks");
                    }
                    calculateChanges(size);
                });
                eventSubject.setText(event.subject);
                Platform.runLater(() ->
                        SetupCalendarUI.eventsList.add(dayEvents));

            }

            System.out.println();
        }

    }

    private static void calculateChanges(int size) {
        System.out.println(size + " size");
        System.out.println(total + " total");
        float percentage = ((float) size/(float) total) * 100;
        System.out.println("Percentage is "+ percentage);
        float totalPercentage = 100;
        float finalValue = totalPercentage - percentage;
        System.out.println(finalValue + "%");

        circleProgress.setAnimated(true);
        circleProgress.setValue(finalValue);

    }

    public static void setTaskCount(Text txtTaskCount) {
        activeTask = txtTaskCount;
    }

    public static void setProgressGauge(Gauge progressGauge) {
        circleProgress = progressGauge;
    }
}