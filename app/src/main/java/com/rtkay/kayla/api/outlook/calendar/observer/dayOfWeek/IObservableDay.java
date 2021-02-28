package com.rtkay.kayla.api.outlook.calendar.observer.dayOfWeek;

public interface IObservableDay {
    void registerDayOfTheWeekObserver(IObserverDay iObserverDay);
    void removeDayOfTheWeekObserver(IObserverDay iObserverDay);

    void dayChanged();
}
