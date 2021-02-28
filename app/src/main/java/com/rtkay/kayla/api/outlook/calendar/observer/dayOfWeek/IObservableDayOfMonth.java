package com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek;

public interface IObservableDayOfMonth {
    void registerDayOfMonthObserver(IObserverDayOfMonth iObserverDay);
    void removeDayOfMonthObserver(IObserverDayOfMonth iObserverDay);
}
