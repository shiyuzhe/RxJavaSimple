package com.example.wanji.rxjavasimple

import android.arch.lifecycle.Transformations.map
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.zipWith
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 *   by  :   syz
 *   Time: 2019/1/10 13:35
 *   Description:
 */
class operators {


    @Test
    fun concat() {
        //timeline  collection and emit once
        Observable.concat(arrayListOf(Observable.range(1, 4))).concatWith(Observable.just(32)).subscribeBy {
            System.out.println(it)
        }
        //Observable.concat(a,b) === a.concatWith(b)
        Observable.concat<Int> {
            Observable.just(1)
            Observable.just(2)
        }
        Observable.just(1).concatWith(Observable.just(2))
    }

    @Test
    fun create() {
        Observable.create<Int> {
            it.onNext(1)
            it.onError(Throwable())
            it.onComplete()
        }.subscribeBy(
                onError = {
                    System.out.println(it)
                }
        )
        Observable.create<Int> {
            it.onNext(1)
            it.onComplete()
        }.subscribeBy {
            System.out.println(it)
        }
    }

    private var subscribe: Disposable? = null
    @Test
    fun delay() {
        Observable.just("").doOnError { Observable.just("err") }

        Observable.range(1, 10).map {
            Observable.just("::$it")
        }.subscribeBy {

        }
        subscribe = Observable.range(1, 10).flatMap {
            Observable.just("::$it")
        }.doOnSubscribe {
            //dispose之前的同一个事件源？
            if (subscribe?.isDisposed == false)
                subscribe?.dispose()
        }.subscribe {
            System.out.println(it)
        }
    }

    @Test
    fun merge() {
        Observable.just(11).mergeWith(Observable.range(1, 5)).subscribeBy {
            System.out.println(it)
        }
        Observable.merge<Int> {
            it.onNext(Observable.just(11))
            it.onNext(Observable.range(1, 3))
            it.onComplete()
        }.subscribeBy {
            System.out.println(it)
        }
    }

    @Test
    fun zip() {
        Observable.range(1, 4).zipWith(Observable.just("sdf"))
                .map {
                    //first:1,second:sdf
                    //丢弃了2，3，4
                    "${it.first}:${it.second}"
                }.subscribeBy {
                    System.out.println(it)

                }
    }


    /**
     * 相邻的元素重复
     */
    @Test
    fun distinct() {
        System.out.println("distinctUntilChanged")
        Observable.create<Int> {
            for (i in 0..3) {
                it.onNext(i)
                it.onNext(i)
            }
            for (i in 0..3) {
                it.onNext(i)
            }
            it.onComplete()
        }.distinctUntilChanged().subscribeBy {
            System.out.print("$it ")
        }

        System.out.println("distinct")
        Observable.create<Int> {
            for (i in 0..3) {
                it.onNext(i)
                it.onNext(i)
            }
            for (i in 0..3) {
                it.onNext(i)
            }
            it.onComplete()
        }.distinct().subscribeBy {
            System.out.print("$it ")
        }
    }

    @Test
    fun retry() {
        Observable.create<Int> {
            it.onNext(1)
            it.onError(Throwable())
            it.onNext(3)
        }.doOnSubscribe {
            System.out.println("subscribing")
        }.retryWhen {
            val counter = AtomicInteger()
            it.takeWhile {
                counter.getAndIncrement() != 3
            }.flatMap {
                //延时 X seconds
                System.out.println("delay retry by " + counter.get() + " second(s)")
                Observable.timer(counter.toLong(), TimeUnit.SECONDS)
            }
        }.blockingSubscribe()
    }

    @Test
    fun r() {
        /**
         * 重连3次,1,2,3s
         */
        var num = 0
        Observable.timer(1, TimeUnit.SECONDS).doOnSubscribe {
            System.out.println("subscribing")
        }.map {
            if (++num > 2)
                1
            else
                throw  RuntimeException()
        }.retryWhen {
            val counter = AtomicInteger()
            it.takeWhile {
                counter.getAndIncrement() != 3
            }.flatMap {
                System.out.println("delay retry by " + counter.get() + " second(s)")
                Observable.timer(counter.toLong(), TimeUnit.SECONDS)
            }
        }.subscribeBy {
            System.out.println("subscribeBy$it")
        }
    }

    @Test
    fun rr() {
        Observable.create<String> {
            System.out.println("subscribing")
            it.onError(java.lang.RuntimeException("always fails"))
        }.retryWhen {
            it.zipWith(Observable.range(1, 3).flatMap { timer ->
                System.out.println("delay retry by $timer second(s)")
                Observable.timer(timer.toLong(), TimeUnit.SECONDS)
            })
        }.blockingForEach {}
    }


}