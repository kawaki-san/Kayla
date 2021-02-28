package com.rtkay.kayla.api.outlook.calendar.observer.clock;

import com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek.IObservableDay;
import com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek.IObservableDayOfMonth;
import com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek.IObserverDay;
import com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek.IObserverDayOfMonth;

import java.util.ArrayList;
import java.util.List;

public class ObservableDate implements IObservableClock, IObservableDay, IObservableDayOfMonth {
    private List<IObserverClock> clockObserversList = new ArrayList<>();
    private List<IObserverDay> dayObserversList = new ArrayList<>();
    private List<IObserverDayOfMonth> dayMonthObserversList = new ArrayList<>();

    @Override
    public void registerClockObserver(IObserverClock iObserverClock) {
        this.clockObserversList.add(iObserverClock);
    }

    @Override
    public void removeClockObserver(IObserverClock iObserverClock) {
        this.clockObserversList.remove(iObserverClock);
    }

    @Override
    public void registerDayOfTheWeekObserver(IObserverDay iObserverDay) {
        this.dayObserversList.add(iObserverDay);
    }

    @Override
    public void removeDayOfTheWeekObserver(IObserverDay iObserverDay) {
        this.dayObserversList.remove(iObserverDay);
    }

    @Override
    public void dayChanged() {
        for (IObserverDay control : dayObserversList) {
            control.updateDayOfTheWeek();
        }
        for (IObserverDayOfMonth control : dayMonthObserversList) {
            control.updateDayOfTheMonth();
        }
    }

    @Override
    public void clockChanged() {
        for (IObserverClock control : clockObserversList) {
            control.updateTime();
        }
    }

    @Override
    public void registerDayOfMonthObserver(IObserverDayOfMonth iObserverDay) {
        this.dayMonthObserversList.add(iObserverDay);
    }

    @Override
    public void removeDayOfMonthObserver(IObserverDayOfMonth iObserverDay) {
        this.dayMonthObserversList.remove(iObserverDay);
    }
}
