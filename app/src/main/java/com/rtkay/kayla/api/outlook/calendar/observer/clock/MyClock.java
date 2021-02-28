package com.rtkay.kayla.api.outlook.calendar.observer.clock;

import javafx.scene.text.Text;
import org.joda.time.DateTime;

public class MyClock extends Text implements IObserverClock {
    private DateTime dt;

    @Override
    public void updateTime() {
        String hours = String.format("%02d", dt.getHourOfDay());
        String minutes = String.format("%02d", dt.getMinuteOfHour());
        String seconds = String.format("%02d", dt.getSecondOfMinute());
        setText(hours + ":" + minutes + ":" + seconds);
    }

    public void setDateValue(DateTime dateTime) {
        this.dt = dateTime;
    }
}
