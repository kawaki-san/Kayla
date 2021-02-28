package com.rtkay.kayla.api.outlook.calendar.observer.days;

import com.microsoft.graph.models.extensions.User;
import com.rtkay.kayla.api.outlook.calendar.GetCalendar;
import com.rtkay.kayla.api.outlook.calendar.SetupCalendarUI;
import javafx.scene.text.Text;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class MyCustomDate extends Text implements IObserverDay {
    private DateTime dt;
    private String accessToken;
    private User user;

    @Override
    public void updateDate() {
        LocalDate localDate = dt.toLocalDate();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, dd");
        String str = localDate.toString(fmt);
        setText(str);

    }
    @Override
    public void updateCalendar() {
        try {
            SetupCalendarUI.eventsList.clear();
            GetCalendar.getCalendarEvents(accessToken, user.mailboxSettings.timeZone);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDateValue(DateTime dateTime) {
        this.dt = dateTime;
    }

    public void setOptions(String accessToken, User user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
