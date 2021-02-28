package com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek;

import javafx.scene.text.Text;
import org.joda.time.DateTime;

import java.util.Locale;

public class MyDayOfTheWeek extends Text implements IObserverDay {
    private DateTime dt;

    @Override
    public void updateDayOfTheWeek() {
        DateTime.Property pDoW = dt.dayOfWeek();
        String dayOfTheWeek = pDoW.getAsText(Locale.getDefault());
        setText(dayOfTheWeek);
    }

    public void setDateValue(DateTime dateTime) {
        this.dt = dateTime;
    }
}
