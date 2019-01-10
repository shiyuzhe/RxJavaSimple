package com.example.wanji.rxjavasimple.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.wanji.rxjavasimple.R
import com.example.wanji.rxjavasimple.subscribeByThread
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.act_transforming.*
import java.util.concurrent.TimeUnit

class ActTransforming : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_transforming)
        testWindowOperator.setOnClickListener {
            testWindow()
        }
    }


    private fun testWindow() {
        Observable.range(1, 10).window(4)
                .subscribeByThread { it: Observable<Int>? ->
                    System.out.print("\n: $it")
                    it?.subscribeBy {
                        System.out.print(":$it")
                    }
                }
    }
}
