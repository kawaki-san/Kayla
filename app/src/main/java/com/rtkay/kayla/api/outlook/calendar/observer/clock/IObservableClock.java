package com.rtkay.kayla.api.outlook.calendar.observer.clock;

public interface IObservableClock {
    void registerClockObserver(IObserverClock iObserverClock);
    void removeClockObserver(IObserverClock iObserverClock);

    void clockChanged();

}
