package com.rtkay.kayla.api.outlook.calendar.observer.clock;

import com.rtkay.kayla.api.outlook.calendar.observer.days.IObservableDay;
import com.rtkay.kayla.api.outlook.calendar.observer.days.IObserverDay;

import java.util.ArrayList;
import java.util.List;

public class ObservableDate implements IObservableClock, IObservableDay {
    private List<IObserverClock> clockObserversList = new ArrayList<>();
    private List<IObserverDay> dayObserversList = new ArrayList<>();

    @Override
    public void registerClockObserver(IObserverClock iObserverClock) {
        this.clockObserversList.add(iObserverClock);
    }

    @Override
    public void removeClockObserver(IObserverClock iObserverClock) {
        this.clockObserversList.remove(iObserverClock);
    }

    @Override
    public void registerCustomDateObserver(IObserverDay iObserverDay) {
        this.dayObserversList.add(iObserverDay);
    }

    @Override
    public void removeCustomDateObserver(IObserverDay iObserverDay) {
        this.dayObserversList.remove(iObserverDay);
    }

    @Override
    public void dayChanged() {
        for (IObserverDay control : dayObserversList) {
            control.updateDate();
        }

    }

    @Override
    public void clockChanged() {
        for (IObserverClock control : clockObserversList) {
            control.updateTime();
        }
    }

    public void updateCalendar() {
        for (IObserverDay control : dayObserversList) {
            control.updateCalendar();
        }
    }
}
