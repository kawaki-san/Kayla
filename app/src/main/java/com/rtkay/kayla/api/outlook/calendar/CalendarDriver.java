package com.rtkay.kayla.api.outlook.calendar;

import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.User;
import com.rtkay.kayla.App;
import com.rtkay.kayla.api.outlook.calendar.observer.clock.MyClock;
import com.rtkay.kayla.api.outlook.calendar.observer.clock.ObservableDate;
import com.rtkay.kayla.api.outlook.calendar.observer.days.MyCustomDate;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

public class CalendarDriver {
    private static DateTime instance;

    private int todayDate;
    protected static List<Event> eventsThisWeek;
    protected static final int DAYS_IN_A_WEEK = 7;
    private User user;
    private String accessToken;


    protected static TreeMap<LocalDate, List<Event>> daysContainingEvents = new TreeMap<>();

    public CalendarDriver() {

    }


    public void initialiseClock(MyCustomDate txtCustomDate, MyClock txtCurrentTime) {
        Service<Void> service = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        //Background work
                        final CountDownLatch latch = new CountDownLatch(1);
                        instance = new DateTime();
                        todayDate = instance.getDayOfMonth();
                        txtCustomDate.setDateValue(instance);
                        clock(txtCurrentTime, txtCustomDate);
                        GetCalendar.setDate(instance);
                        final Properties oAuthProperties = new Properties();
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
                        txtCustomDate.setOptions(accessToken,user);
                        GetCalendar.getCalendarEvents(accessToken, user.mailboxSettings.timeZone);
                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }
        };
        service.start();

    }

    private void clock(MyClock txtCurrentTime, MyCustomDate txtCustomDate) {
        ObservableDate date = new ObservableDate();
        date.registerClockObserver(txtCurrentTime);
        date.registerCustomDateObserver(txtCustomDate);
        date.dayChanged();
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTime currentTime = new DateTime();
            txtCurrentTime.setDateValue(currentTime);
            date.clockChanged();
            //if different day
            if (currentTime.getDayOfMonth() != todayDate) {
                todayDate = currentTime.getDayOfMonth();
                date.dayChanged();
                date.updateCalendar();
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


    public static boolean isSameDay(Date date1, Date date2) {
        org.joda.time.LocalDate localDate1 = new org.joda.time.LocalDate(date1);
        org.joda.time.LocalDate localDate2 = new org.joda.time.LocalDate(date2);
        return localDate1.equals(localDate2);
    }
}
