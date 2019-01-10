package com.example.wanji.rxjavasimple.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.wanji.rxjavasimple.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.act_retry.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ActRetry : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_retry)
        retry.setOnClickListener {
            retry()
        }
    }

    /**
     * 重连三次
     * 测试：第三次重连时模拟请求成功，前两次发送错误
     */
    fun retry() {
        var num = 0//记录重连次数
        Observable.timer(1, TimeUnit.SECONDS).doOnSubscribe {
            System.out.println("subscribing")
        }.map {
            if (++num > 2)
                return@map 1
            throw  RuntimeException()
        }.retryWhen {
            val counter = AtomicInteger()
            it.takeWhile {
                counter.getAndIncrement() != 3
            }.flatMap {
                System.out.println("delay retry by " + counter.get() + " second(s)")
                Observable.timer(counter.toLong(), TimeUnit.SECONDS)
            }
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeBy {
                    System.out.println("subscribeBy$it")
                }
    }
}
