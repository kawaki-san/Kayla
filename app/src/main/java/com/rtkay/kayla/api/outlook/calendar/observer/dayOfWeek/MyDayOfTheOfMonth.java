package com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek;

import javafx.scene.text.Text;
import org.joda.time.DateTime;

public class MyDayOfTheOfMonth extends Text implements IObserverDayOfMonth {
    private DateTime dt;
    @Override
    public void updateDayOfTheMonth() {
        String day = String.format("%02d", dt.getDayOfMonth());
        setText(day);
    }

    public void setDateValue(DateTime dateTime) {
        this.dt = dateTime;
    }
}
