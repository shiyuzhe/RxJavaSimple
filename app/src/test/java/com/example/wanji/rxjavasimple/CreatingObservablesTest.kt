package com.example.wanji.rxjavasimple

import io.reactivex.*
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 *   by  :   syz
 *   Time: 2018/12/12 10:41
 *   Description:
 *
 */
class CreatingObservablesTest {
    /**
     * Create — create an Observable from scratch by calling observer methods programmatically
     *
     * 您可以使用Create运算符从头开始创建Observable。 您将此运算符传递给接受观察者作为其参数的函数。
     * 编写此函数，使其表现为Observable  - 通过适当地调用观察者的onNext，onError和onCompleted方法。
     *
     * 一个格式良好的有限Observable必须尝试只调用一次观察者的onCompleted方法或者只调用一次onError方法，并且此后不得尝试调用任何观察者的其他方法。
     */
    @Test
    fun testCreate() {
        Observable.create<Int>(ObservableOnSubscribe {
        })
        Observable.create<Int> {
            it.onNext(1)
            it.onNext(2)
            it.onComplete()
        }.subscribeBy {
            System.out.println("testCreate:$it")
        }
    }

    /**
     * Defer — do not create the Observable until the observer subscribes,and create a fresh Observable for each observer
     *
     * Defer运算符等待观察者订阅它，然后它生成一个Observable，通常带有Observable工厂函数。
     * 它为每个用户重新执行此操作，因此尽管每个用户可能认为它订阅了相同的Observable，但实际上每个用户都有自己的单独序列。
     *
     * 在某些情况下，等到最后一刻（即直到订阅时间）生成Observable可以确保此Observable包含最新的数据。
     */
    @Test
    fun testDefer() {
        val observable = Observable.defer<Int> {
            ObservableSource {
                for (i in 0..100)
                    it.onNext(i)
                it.onComplete()
            }
        }
        val disposable = observable.subscribeBy {
            System.out.println("testDefer::::$it")
        }
        observable.subscribeBy {
            if (it == 3)
                disposable.dispose()
            System.out.println("testDefer:$it")
        }
    }


    /**
     * Empty/Never/Throw — create Observables that have very precise and limited behavior
     *
     * Empty，Never和Throw操作符生成具有非常特定和有限行为的Observable。
     * 这些对于测试目的很有用，有时也可以与其他Observable组合使用，或者作为参数，用于期望其他Observable作为参数的运算符。
     * Empty：create an Observable that emits no items but terminates normally
     * Never：create an Observable that emits no items and does not terminate
     * Throw：create an Observable that emits no items and terminates with an error
     */
    @Test
    fun testEmpty() {
        val empty = Observable.empty<Int>()
        val never = Observable.never<Int>()
        val error = Observable.error<Int> { Throwable() }
        Observable.range(1, 10).flatMap {
            when (it) {
                2 -> Observable.empty<Int>()
                4 -> Observable.error<Int> { Throwable() }.onErrorReturn { -1 }
                7 -> Observable.never()
                else -> Observable.just(it)
            }
        }.subscribeBy {
            System.out.print("$it::")
        }
        //  1::3::-1::5::6::8::9::10::
    }

    /**
     * From — convert some other object or data structure into an Observable
     *
     * 当您使用Observable时，如果您使用的所有数据都可以表示为Observables，而不是Observables和其他类型的混合，则可以更方便。 这允许您使用一组运算符来控制数据流的整个生命周期。
     * Iterables，Iterables可以被认为是一种同步的Observable;
     * Futures，作为一种始终只发出单一项目的Observable。 通过显式地将这些对象转换为Observable，您可以允许它们作为对等体与其他Observable进行交互。
     *
     * 因此，大多数ReactiveX实现都具有允许您将特定于语言的对象和数据结构转换为Observable的方法。
     */
    @Test
    fun testFrom() {

        val fromCallable = Observable.fromCallable { "fromCallable" }
        val fromPublisher = Observable.fromPublisher<String> {  }


// ?       Observable.fromFuture()
        Observable.fromArray(arrayOf(1, 2, 3)).subscribeBy {
            System.out.println("testFrom:$it")
        }
        Observable.fromIterable(mutableListOf(1, 2, 3)).subscribeBy {
        }
        Observable.fromIterable(arrayListOf(1, 2, 3)).subscribeBy {
        }
    }


    /**
     * Interval — create an Observable that emits a sequence of integers spaced by a particular time interval
     *
     * The Interval operator returns an Observable that emits an infinite sequence of ascending integers, with a constant interval of time of your choosing between emissions.
     * Interval运算符返回一个Observable，它发出无限的升序整数序列，并在排放之间选择一个恒定的时间间隔。
     */
    @Test
    fun testInterval() {
//        Observable.interval(100, TimeUnit.MILLISECONDS).subscribeBy {
//            System.out.println("testRange:$it")
//        }
    }

    /**
     * Just — convert an object or a set of objects into an Observable that emits that or those objects
     *
     * create an Observable that emits a particular item
     * Just运算符将项目转换为发出该项目的Observable。
     * 就像From类似，但请注意From将潜入一个数组或一个iterable或类似的东西来拉出要发出的项目，而Just只是简单地发出数组或者可迭代的或者你有什么，不变 ，作为单个项目。
     *
     * 请注意，如果将null传递给Just，它将返回一个Observable，它将null作为项发出。 不要错误地假设这将返回一个空的Observable（一个根本不发出任何项目）。 为此，您将需要Empty运算符。
     */
    @Test
    fun testJust() {
        Observable.just(1)
        Observable.just(1, 2)
        Observable.just(null)
        Flowable.just(1)
        Flowable.just(1, 2)
        Maybe.just(1)
        Single.just(1)

    }

    /**
     * Range — create an Observable that emits a range of sequential integers
     *
     * The Range operator emits a range of sequential integers, in order, where you select the start of the range and its length.
     */
    @Test
    fun testRange() {
        Observable.range(1, 20).subscribeBy {
            System.out.println("testRange:$it")
        }
    }

    /**
     * Repeat — create an Observable that emits a particular item or sequence of items repeatedly
     *
     * create an Observable that emits a particular item multiple times
     * The Repeat operator emits an item repeatedly. Some implementations of this operator allow you to repeat a sequence of items, and some permit you to limit the number of repetitions.
     */
    @Test
    fun testRepeat() {
        Observable.just("hello").repeat(8).subscribeBy {
            System.out.println("testRepeat:$it")
        }
    }

    /**
     * Start — create an Observable that emits the return value of a function
     *
     * There are a number of ways that programming languages have for obtaining values as the result of calculations, with names like functions, futures, actions, callables, runnables, and so forth.
     * The operators grouped here under the Start operator category make these things behave like Observables so that they can be chained with other Observables in an Observable cascade
     */
    @Test
    fun testStart() {
        Observable.range(1, 5).startWith(2)
        Observable.range(1, 5).startWith(arrayListOf(1, 2))
        Observable.range(1, 5).startWith {
            Observable.just(22, 33)
        }
    }

    /**
     *  Timer — create an Observable that emits a single item after a given delay
     *  create an Observable that emits a particular item after a given delay
     *
     *  The Timer operator creates an Observable that emits one particular item after a span of time that you specify.
     *
     */
    @Test
    fun testTimer() {
        Observable.timer(100, TimeUnit.MILLISECONDS)
    }
}