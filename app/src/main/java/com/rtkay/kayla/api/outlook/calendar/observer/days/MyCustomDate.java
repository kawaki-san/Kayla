package com.rtkay.kayla.api.outlook.calendar.observer.days;

import javafx.scene.text.Text;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class MyCustomDate extends Text implements IObserverDay {
    private DateTime dt;

    @Override
    public void updateDayOfTheWeek() {
        LocalDate localDate = dt.toLocalDate();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE, dd");
        String str = localDate.toString(fmt);
        setText(str);
    }

    public void setDateValue(DateTime dateTime) {
        this.dt = dateTime;
    }
}
