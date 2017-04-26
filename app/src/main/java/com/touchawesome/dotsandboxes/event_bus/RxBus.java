package com.touchawesome.dotsandboxes.event_bus;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 *  * Created by scelus on 24.04.17
 */
public class RxBus {

    private static RxBus instance;

    private final Subject<Object, Object> bus = new SerializedSubject<>(PublishSubject.create());

    public static RxBus getInstance() {
        if (instance == null) {
            instance = new RxBus();
        }
        return instance;
    }

    private RxBus() {

    }

    public void send(Object o) {
        bus.onNext(o);
    }

    public Observable<Object> getBus() {
        return bus;
    }

    public Subscription subscribe(Class filter, Subscriber<? super Object> subscriber) {
        return bus.filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return filter == null || filter.isInstance(o);
            }
        }).subscribe(subscriber);
    }


    public void unSubscribe(Subscription subscription) {
        if (subscription != null && subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}

