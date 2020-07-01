package com.attendance.bk.utils

import android.os.Process
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by CXJ
 * Created date 2019/1/10/010
 */
object RxWorkerUtil {

    private var workerScheduler: Scheduler  // Rx工作线程池

    init {
        val maxProcessCount = Runtime.getRuntime().availableProcessors() + 2
        val threadPool = ThreadPoolExecutor(2, maxProcessCount, 20,
                TimeUnit.SECONDS,
                LinkedBlockingQueue(),
                RxWorkerScheduler()
        )
        workerScheduler = Schedulers.from(threadPool)
    }


    fun workerScheduler(): Scheduler {
        return workerScheduler
    }


    private class RxWorkerScheduler : ThreadFactory {

        private val poolNum = AtomicInteger(1)
        private val group: ThreadGroup
        private val threadNum = AtomicInteger(1)
        private val namePrefix: String

        init {
            val sm = System.getSecurityManager()
            group = if (sm != null) sm.threadGroup else Thread.currentThread().threadGroup
            namePrefix = "pool-" + poolNum.getAndIncrement() + "-thread-"
        }

        override fun newThread(r: Runnable): Thread {
            val t = Thread(group, r, namePrefix + threadNum.getAndIncrement(), 0)
            if (t.isDaemon) {
                t.isDaemon = false
            }
            if (t.priority != Process.THREAD_PRIORITY_BACKGROUND) {
                t.priority = Process.THREAD_PRIORITY_BACKGROUND
            }
            return t
        }
    }

}

fun <T> Single<T>.workerThreadChange(): Single<T> = this.compose {
    it.subscribeOn(RxWorkerUtil.workerScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(RxWorkerUtil.workerScheduler())
}

fun <T> Single<T>.ioThreadChange(): Single<T> = this.compose {
    it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
}

fun <T> Observable<T>.workerThreadChange(): Observable<T> = this.compose {
    it.subscribeOn(RxWorkerUtil.workerScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(RxWorkerUtil.workerScheduler())
}

fun <T> Observable<T>.ioThreadChange(): Observable<T> = this.compose {
    it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
}

fun <T> Flowable<T>.workerThreadChange(): Flowable<T> = this.compose {
    it.subscribeOn(RxWorkerUtil.workerScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(RxWorkerUtil.workerScheduler())
}

fun <T> Flowable<T>.ioThreadChange(): Flowable<T> = this.compose {
    it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
}


fun <T> Maybe<T>.workerThreadChange(): Maybe<T> = this.compose {
    it.subscribeOn(RxWorkerUtil.workerScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(RxWorkerUtil.workerScheduler())
}

fun <T> Maybe<T>.ioThreadChange(): Maybe<T> = this.compose {
    it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
}

fun Completable.workerThreadChange(): Completable = this.compose {
    it.subscribeOn(RxWorkerUtil.workerScheduler())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(RxWorkerUtil.workerScheduler())
}

fun Completable.ioThreadChange(): Completable = this.compose {
    it.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
}
