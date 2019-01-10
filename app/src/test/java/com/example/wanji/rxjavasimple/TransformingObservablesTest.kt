package com.example.wanji.rxjavasimple

import io.reactivex.*
import io.reactivex.observables.GroupedObservable
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Test

/**
 *   by  :   syz
 *   Time: 2018/12/12 10:41
 *   Description:
 *
 */
class TransformingObservablesTest {

    /**
     * Buffer — periodically gather items from an Observable into bundles and emit these bundles rather than emitting the items one at a time
     *
     * Note that if the source Observable issues an onError notification, Buffer will pass on this notification immediately without first emitting the buffer it is in the process of assembling, even if that buffer contains items that were emitted by the source Observable before it issued the error notification.
     * 每收集到n个发射一次
     * 缓存过程中遇到onError时，当前已缓存未发射数据将被抛弃
     */
    @Test
    fun testBuffer() {
        Observable.create<Int> {
            for (i in 0..10) it.onNext(i)
            it.onError(Throwable())
            for (i in 100..110) it.onNext(i)
        }.buffer(3).onErrorReturn { mutableListOf(1, 2) }.subscribeBy {
            System.out.print(it)
        }

        //[0, 1, 2][3, 4, 5][6, 7, 8][1, 2]
        //每10ms发送一次
        //Observable.range(1, 100).buffer(10, TimeUnit.MILLISECONDS)
    }

    /**
     * Window — periodically subdivide items from an Observable into Observable windows and emit these windows rather than emitting the items one at a time
     *
     * 类似于buffer，count代表分组大小
     * 不同的是：buffer发射的是list,window发射的是Observable
     */
    @Test
    fun testWindow() {
        Observable.range(0, 10).window(4).subscribeBy { it: Observable<Int>? ->
            System.out.print("\n: $it")
            it?.subscribeBy {
                System.out.print(": $it")
            }
        }
        /**
         * : io.reactivex.subjects.UnicastSubject@27c20538: 1: 2: 3: 4
        : io.reactivex.subjects.UnicastSubject@59494225: 5: 6: 7: 8
        : io.reactivex.subjects.UnicastSubject@6e1567f1: 9: 10
         */
    }


    /**
     *  Map — transform the items emitted by an Observable by applying a function to each item
     *  Any to Any
     */
    @Test
    fun testMap() {
        Observable.range(1, 5).map { "map:$it" }.subscribeBy {
            System.out.print(":$it")
        }
        //:map:1:map:2:map:3:map:4:map:5
    }

    /**
     *  FlatMap — transform the items emitted by an Observable into Observables,
     *  then flatten the emissions from those into a single Observable
     *  Any to Observable
     */
    @Test
    fun testFlatMap() {
        Observable.fromIterable(mutableListOf(1, 11, 111)).flatMap {
            Observable.range(it, 2)
        }.subscribeBy {
            System.out.print(":$it")
        }
        //:1:2:11:12:111:112

    }

    /**
     *  GroupBy — divide an Observable into a set of Observables that each emit a different group of items from the original Observable, organized by key
     *  把一组数据分组并按组发射
     */
    @Test
    fun testGroupBy() {
        Observable.range(1, 25).groupBy {
            //返回一个key来标识该元素所属分组，按分组分发下去
            "key:${it / 10}"
        }.subscribeBy { groupedObservable: GroupedObservable<String, Int> ->
            System.out.print("\n$groupedObservable:${groupedObservable.key}----------")
            groupedObservable.subscribeBy {
                System.out.print(":$it")
            }
        }
        /**
         * io.reactivex.internal.operators.observable.ObservableGroupBy$GroupedUnicast@3f49dace:key:0----------:1:2:3:4:5:6:7:8:9
        io.reactivex.internal.operators.observable.ObservableGroupBy$GroupedUnicast@56ac3a89:key:1----------:10:11:12:13:14:15:16:17:18:19
        io.reactivex.internal.operators.observable.ObservableGroupBy$GroupedUnicast@27c20538:key:2----------:20:21:22:23:24:25
         */
    }


    /**
     * Scan — apply a function to each item emitted by an Observable, sequentially, and emit each successive value
     * Scan运算符将函数应用于源Observable发出的第一个项目，然后将该函数的结果作为其自己的第一个发射。
     * 它还将函数的结果与源Observable发出的第二个项一起反馈到函数中，以便生成其第二个发射。
     * 它继续反馈其自身的后续排放以及源Observable的后续排放，以创建其序列的其余部分。
     *   《《累加器》》
     */
    @Test
    fun testScan() {
        System.out.print("scanOperator ")
        Observable.range(1, 5).scan { t1: Int, t2: Int ->
            System.out.print("$t1+$t2=")
            t1 + t2
        }.subscribeBy {
            System.out.print("$it;")
        }
        //scanOperator 1;1+2=3;3+3=6;6+4=10;10+5=15;
    }


}