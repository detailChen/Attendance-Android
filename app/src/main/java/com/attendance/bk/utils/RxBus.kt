package com.attendance.bk.utils

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor

/**
 * EventBus
 *
 *
 * Created by Chen xuJie on 2019/02/17
 */
class RxBus {

    private val subject = PublishProcessor.create<Any>()

    fun post(o: Any) {
        subject.onNext(o)
    }

    fun toUIObserver(): Flowable<Any> {
        return subject.observeOn(AndroidSchedulers.mainThread())
    }

    fun <T> toObserverable(eventType: Class<T>): Flowable<T> {
        return subject.ofType(eventType)

    }

    fun <T> toUIObserver(eventType: Class<T>): Flowable<T> {
        return subject.ofType(eventType).observeOn(AndroidSchedulers.mainThread())
    }

    fun hasObservers(): Boolean {
        return subject.hasSubscribers()
    }

}
