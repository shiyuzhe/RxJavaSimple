package com.example.wanji.rxjavasimple

import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 * rxJava_空间维度
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)


    }

    /**
     *  map
     *  filter
     *  flatMap
     */
    @Test
    fun rxJava_translate() {
        Observable.just(1, 3, 5, 9, 88)
                .map { it + 11 }
                .filter { it < 30 }
                .flatMap { Observable.just("it:$it") }
                .subscribeBy {
                    System.out.println(it)
                }
    }

    /**
     *  doOnNext
     *  startWith
     *  use in ：加载本地图片在列表展示，当网络请求返回结果时更新列表
     */
    @Test
    fun rxJava_startWith() {
        //读取缓存和网络请求结果统一处理
        //观察者只需要对数据做响应，不需要考虑数据来源（网络、本地）
        //先展示本地数据startWith，当just返回结果时再展示数据集
        Observable.just(arrayOf(1, 2, 3, 5, 7))
                .doOnNext {
                    //cache or update cache
                }
                .startWith(Observable.just(arrayOf(1, 2))).subscribeBy {
                    //回调2次，一次startWith，五次just
                    System.out.println("it:$it")
                }
    }

    /**
     * distinctUntilChanged 对比两个数据集合是否相同，相同则只发送一个数据
     *  use in ：加载本地图片在列表展示，当网络请求返回结果时更新列表
     *  当两次请求返回的结果一致时不会发送网络请求返回的结果给观察者
     */
    @Test
    fun rxJava_distinctUntilChanged() {
        //读取缓存和网络请求结果统一处理
        //观察者只需要对数据做响应，不需要考虑数据来源（网络、本地）
        //两次请求结果相同时之更新一次
        val ints = arrayOf(1, 2, 3, 5, 7)
        Observable.just(ints)
                .doOnNext {
                    //cache or update cache
                }
                .startWith(ints)
                .distinctUntilChanged()
                .subscribeBy {
                    for (i in it)
                        System.out.println("it:$i")
                }
    }

    /**
     * merge
     * distinct 对两组数据去重
     */
    @Test
    fun rxJava_distinct() {
        //读取缓存和网络请求结果统一处理
        //观察者只需要对数据做响应，不需要考虑数据来源（网络、本地）
        //数据不重复
        Observable.merge(Observable.just(1, 2, 3, 5, 7), Observable.just(3, 66, 7))
                .distinct()
                .subscribeBy {
                    System.out.println("it:$it")
                }
    }


    @Test
    fun rxJava_compose() {
        val items = mutableListOf<String>()
        val observable1 = Observable.create<MutableList<String>> {
            System.out.println("observable1")
            Observable.just(mutableListOf("string:1", "string:${it}"))
        }.startWith(mutableListOf<String>())
        val observable2 = Observable.timer(2, TimeUnit.SECONDS).flatMap {
            Observable.just(mutableListOf("string2", "string:$it")).startWith(mutableListOf<String>())
        }
        Observable.just(1, 2, 3)
                .map {
                    val lists = mutableListOf<Observable<MutableList<String>>>()
                    val v = Observable.just(mutableListOf("string:$it", "string:$it")).startWith(mutableListOf<String>())
                    when (it) {
                        1 -> lists.add(v)
                        2 -> lists.add(v)
                        3 -> lists.add(v)
                    }
                    lists
                }.flatMap {
                    Observable.combineLatest(it) {
                        for (obj in it) {
                            items.addAll(obj as MutableList<String>)
                        }
                        items
                    }
                }
                .distinct()
                .subscribeBy {
                    System.out.println("size:${it.size}")
                }


    }

    @Test
    fun rxJava_composeLLLLLLL() {
        Observable.create<List<String>> {
            it.onNext(mutableListOf("a", "v", "cc"))
            it.onComplete()
        }.subscribeBy {
            for (i in it)
                System.out.println("it:$i")
        }


    }
}
