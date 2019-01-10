package com.example.wanji.rxjavasimple.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.wanji.rxjavasimple.R
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.act_create.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * testSomeCreateOperators
 * others see RxJavaSimple\app\src\test\java\com\example\wanji\rxjavasimple\CreatingObservablesTest.kt
 */
class ActCreate : AppCompatActivity() {
    var answer = ""//log Info

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_create)
        testDefer()
        interval.setOnClickListener { testIntervalAndTimer() }
    }

    private fun testIntervalAndTimer() {
        answer = "interval"
        val interval = Observable.interval(100, TimeUnit.MILLISECONDS).subscribeBy {
            showText("{$it:${Date().time}}")
        }
        Observable.timer(2, TimeUnit.SECONDS).subscribeBy {
            interval.dispose()
            Log.e("ActCreate", "interval.dispose():$answer")
        }
    }

    private fun testDefer() {
        val observable = Observable.defer<Int> {
            ObservableSource {
                for (i in 0..100) {
                    it.onNext(i)
                    if (i == 0)
                        showText("observable be subscribe\n")
                }
                it.onComplete()
            }
        }
        Observable.range(111, 2).subscribeBy { showText("range:$it\n") }
        defer.setOnClickListener {
            showText("defer be click \n")
            val disposable = observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread()).subscribeBy { showText("::$it") }
            observable.subscribeOn(Schedulers.newThread()).map {
                if (it == 99) {
                    disposable.dispose()
                    showText("dispose\n")
                }
                it
            }.observeOn(AndroidSchedulers.mainThread()).subscribeBy(onNext = {
                showText(":$it")
            }, onComplete = {
                Log.e("ActCreate", answer)
            })
        }
    }

    fun showText(text: String) {
        if (text.contains("50"))
            answer += "\n"
        answer += text
    }
}
