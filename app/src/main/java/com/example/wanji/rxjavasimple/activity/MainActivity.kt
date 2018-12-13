package com.example.wanji.rxjavasimple.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.wanji.rxjavasimple.R
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        text.setOnClickListener {
//
//        }
        //防抖
        RxView.clicks(text).debounce(3, TimeUnit.SECONDS).subscribeBy {
            Log.e("MainActivity", "click text")
        }

//        RxView.clicks(text).throttleFirst(1, TimeUnit.SECONDS).subscribeBy {
//            Log.e("MainActivity", "click text")
//        }

        //双击
//        val clicks = RxView.clicks(text).map { System.currentTimeMillis() }.share()
//        clicks.zipWith(clicks.skip(1), zipper = { t1: Long, t2: Long ->
//            t2 - t1
//        }).filter {
//            it < 500
//        }.subscribe {
//            Log.e("MainActivity", "double click =")
//        }

        search()
        val share = RxView.clicks(text).share()
        share.buffer(share.debounce(400, TimeUnit.MILLISECONDS)).observeOn(AndroidSchedulers.mainThread())
                .filter { it.size >= 2 }
                .subscribe {
                    Log.e("MainActivity", "double click ")
                }
        createOperators.setOnClickListener {
            startActivity(Intent(this, ActCreate::class.java))
        }
    }


    /**
     * edit:EditView,在输入改变时触发一次请求
     * debounce：限流，防抖，400ms无输入操作时触发一次请求
     * switchMap：Observable结果合并，当后一个Observable开始发射元素时，自动取消订阅的Observable，前一个Observable中尚未发送完的元素会被抛弃
     */
    @SuppressLint("SetTextI18n")
    fun search() {
        var i = 0
        RxTextView.textChanges(edit)
                .debounce(400, TimeUnit.MILLISECONDS)
                .switchMap {
                    //模拟网络请求
                    Observable.just(arrayOf(1, 3, 2, 4))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    text.text = it.toString() + i++
                }
    }

}
