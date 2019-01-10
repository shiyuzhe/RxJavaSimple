package com.example.wanji.rxjavasimple

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 *   by  :   syz
 *   Time: 2018/11/12 13:30
 *   Description:
 */


/**
 *
 */

fun <T : Any> Observable<T>.subscribeByThread(onErr: (Throwable) -> Unit = {}, onNextStub: (T) -> Unit = {}
): Disposable = this.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribeBy(onErr, {}, onNextStub)


fun <T : Any> Observable<T>.subscribeByThreadRetry(disposable: Disposable?, onErrStub: (Throwable) -> Unit = {}, onNextStub: (T) -> Unit = {}
): Disposable = this.retryWhen {
    val counter = AtomicInteger()
    it.takeWhile {
        counter.getAndIncrement() != 3
    }.flatMap {
        Observable.timer(counter.toLong(), TimeUnit.SECONDS)
    }
}.observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .doOnSubscribe {
            //retryWhen之后调用一次，之前每次重连都调用
            disposable?.dispose()
        }.subscribeBy(onErrStub, {}, onNextStub)