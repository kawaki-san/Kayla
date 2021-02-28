package com.rtkay.kayla.api.outlook.calendar.observer.days;

public interface IObservableDay {
    void registerCustomDateObserver(IObserverDay iObserverDay);
    void removeCustomDateObserver(IObserverDay iObserverDay);

    void dayChanged();
}
