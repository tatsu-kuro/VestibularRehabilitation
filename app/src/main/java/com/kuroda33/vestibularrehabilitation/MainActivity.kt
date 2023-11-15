package com.kuroda33.vestibularrehabilitation

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , View.OnClickListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mini13.setOnClickListener(this)
        mini15.setOnClickListener(this)
        mini110.setOnClickListener(this)
        mini23.setOnClickListener(this)
        mini25.setOnClickListener(this)
        mini210.setOnClickListener(this)
        mini33.setOnClickListener(this)
        mini35.setOnClickListener(this)
        mini310.setOnClickListener(this)
    }
    override fun onClick(v: View) {
        when(v.id){
            R.id.mini13 -> mini13()
            R.id.mini15 -> mini15()
            R.id.mini110 -> mini110()
            R.id.mini23 -> mini23()
            R.id.mini25 -> mini25()
            R.id.mini210 -> mini210()
            R.id.mini33 -> mini33()
            R.id.mini35 -> mini35()
            R.id.mini310 -> mini310()
        }
    }
    var list = intArrayOf()
    fun setRehalist(){
        val intent= Intent(this,CameraActivity::class.java)
        var reha =ArrayList<Int>()
        for(i in 0..list.count()-1){
            reha.add(list[i])
        }
        intent.putExtra("rehaList",reha)
        startActivity(intent)
    }

    //(1)視標タイプ,(2)背景,(3)音声,(4)表示時間,(5)次の表示までの時間,(6)表示番号
    //上記6個で１個の視標パターンを設定する

    //＜＜（１）視標タイプ＞＞-----２～９(puusuit)、１１～９２（saccade）、１１１～２２２（OKN）
    //pursuit（２～９）
    //2:fixed
    //3:0.3Hz
    //6:0.6Hz
    //9:0.9Hz
    //saccade（２１～９２）
    //1の桁speed(1:低速,2:高速)
    //10の桁は以下の如し
    //1:右5点　2:左5点　3:右3点　4:左3点　5:左右3点　6:上下3点　
    //7:左右ランダム3点　8:上下ランダム3点　9:上下左右ランダム9点
    //OKN　（１１１～２２２）
    //1の桁  ：１=低速、２=高速
    //10の桁 ：１=小幅　２=大幅
    //100の桁：１=左へ　２=右へ
    //＜＜（２）背景＞＞------------------------------
    //0:white
    //1:gray
    //2:checker
    //3:camera
    //＜＜（３）音声＞＞----未設定
    //＜＜（４）表示時間＞＞
    //10*2で10秒となる。15で7.5秒
    //＜＜（５）次の表示パターンまでの時間＞＞----------------
    //2*2で２秒
    //0なら、間を置かず次の視標を表示
    //＜＜（６）どこかに表示する番号＞＞-----未設定-------

    //視標タイプ,背景,音声,時間,次の表示までの時間,表示番号
    private fun mini13(){//初級編３分
        list = intArrayOf(
            3,0,3,30*2, 3*2,0,//0.3Hz pursuit　背景白30秒　3秒ポーズ
            3,0,3,30*2, 5*2,0,//0.3Hz pursuit　背景白30秒　5秒ポーズ

            51,0,8,30*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            51,0,8,30*2, 5*2,0, //左右saccades 背景白30秒  5秒ポーズ

            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ
            2,0,8,30*2, 1*2,0//上下VOR×１背景白30秒　1秒ポーズ
        )
        setRehalist()
    }
    private fun mini15(){//初級編５分
        list = intArrayOf(
            3,0,3,30*2, 3*2,0,//0.3Hz pursuit　背景白30秒　3秒ポーズ
            3,0,3,30*2, 5*2,0,//0.3Hz pursuit　背景白30秒　5秒ポーズ

            51,0,8,15*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            61,0,8,15*2, 5*2,0, //上下saccades 背景白30秒  5秒ポーズ
            51,0,8,15*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            61,0,8,15*2, 5*2,0, //上下saccades 背景白30秒  5秒ポーズ

            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ
            2,0,8,30*2, 5*2,0, //上下VOR×１背景白30秒　5秒ポーズ
            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ
            2,0,8,30*2, 1*2,0//上下VOR×１背景白30秒　1秒ポーズ
        )
        setRehalist()
    }
    private fun mini110(){//初級編１０分
        list = intArrayOf(
            3,0,3,30*2, 3*2,0,//0.3Hz pursuit　背景白30秒　3秒ポーズ
            3,0,3,30*2, 5*2,0,//0.3Hz pursuit　背景白30秒　5秒ポーズ

            51,0,8,15*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            61,0,8,15*2, 5*2,0, //上下saccades 背景白30秒  5秒ポーズ
            51,0,8,15*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            61,0,8,15*2, 5*2,0, //上下saccades 背景白30秒  5秒ポーズ

            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ
            2,0,8,30*2, 5*2,0, //上下VOR×１背景白30秒　5秒ポーズ
            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ
            2,0,8,30*2, 5*2,0, //上下VOR×１背景白30秒　5秒ポーズ
            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ
            2,0,8,30*2, 1*2,0, //上下VOR×１背景白30秒　1秒ポーズ

            2,0,8,30*2, 3*2,0, //左右VOR cancellation背景白30秒　3秒ポーズ
            2,0,8,30*2, 5*2,0, //上下VOR cancellation背景白30秒　5秒ポーズ
            2,0,8,30*2, 3*2,0, //左右VOR cancellation背景白30秒　3秒ポーズ
            2,0,8,30*2, 5*2,0, //上下VOR cancellation背景白30秒　5秒ポーズ

            111,0,3,30*2, 3*2,0, //OKN 左向き　背景白30秒　3秒ポーズ
            211,0,0,30*2, 5*2,0, //OKN　右向き　背景白30秒　5秒ポーズ
            111,0,3,30*2, 3*2,0, //OKN　左向き　背景白30秒　3秒ポーズ
            211,0,0,30*2, 1*2,0//OKN　右向き　背景白30秒　1秒ポーズ
        )
        setRehalist()
    }
    private fun mini23(){//中級編３分
        list = intArrayOf(
            6,0,3,30*2, 3*2,0,//0.6Hz pursuit　背景白30秒　3秒ポーズ
            6,0,3,30*2, 5*2,0,//0.6Hz pursuit　背景白30秒　5秒ポーズ

            52,0,8,30*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            52,0,8,30*2, 5*2,0, //左右saccades 背景白30秒  5秒ポーズ

            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ　立位
            2,0,8,30*2, 1*2,0//上下VOR×１背景白30秒　1秒ポーズ　立位
        )
        setRehalist()
    }
    private fun mini25(){//中級編５分
        list = intArrayOf(
            6,0,3,30*2, 3*2,0,//0.6Hz pursuit　背景白30秒　3秒ポーズ
            6,0,3,30*2, 5*2,0,//0.6Hz pursuit　背景白30秒　5秒ポーズ

            52,0,8,15*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            62,0,8,15*2, 5*2,0, //上下saccades 背景白30秒  5秒ポーズ
            52,0,8,15*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            62,0,8,15*2, 5*2,0, //上下saccades 背景白30秒  5秒ポーズ

            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ　立位
            2,0,8,30*2, 5*2,0, //上下VOR×１背景白30秒　5秒ポーズ　立位
            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ　立位
            2,0,8,30*2, 1*2,0 //上下VOR×１背景白30秒　1秒ポーズ　立位
        )
        setRehalist()
    }
    private fun mini210(){//中級編１０分
        list = intArrayOf(
            6,0,3,30*2, 3*2,0,//0.6Hz pursuit　背景白30秒　3秒ポーズ
            6,0,3,30*2, 5*2,0,//0.6Hz pursuit　背景白30秒　5秒ポーズ

            52,0,8,15*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            62,0,8,15*2, 5*2,0, //上下saccades 背景白30秒  5秒ポーズ
            52,0,8,15*2, 3*2,0, //左右saccades 背景白30秒  3秒ポーズ
            62,0,8,15*2, 5*2,0, //上下saccades 背景白30秒  5秒ポーズ

            2,0,8,60*2, 3*2,0, //左右VOR×１背景白60秒　3秒ポーズ　座位
            2,0,8,60*2, 5*2,0, //上下VOR×１背景白60秒　5秒ポーズ　座位
            2,0,8,60*2, 3*2,0, //左右VOR×１背景白60秒　3秒ポーズ　立位
            2,0,8,60*2, 1*2,0, //上下VOR×１背景白60秒　1秒ポーズ　立位

            2,0,8,30*2, 3*2,0, //左右VOR cancellation背景白30秒　3秒ポーズ　立位
            2,0,8,30*2, 5*2,0, //上下VOR cancellation背景白30秒　5秒ポーズ　立位
            2,0,8,30*2, 3*2,0, //左右VOR cancellation背景白30秒　3秒ポーズ　立位
            2,0,8,30*2, 5*2,0, //上下VOR cancellation背景白30秒　5秒ポーズ　立位

            112,0,3,30*2, 3*2,0, //OKN 左向き　背景白30秒　3秒ポーズ
            212,0,0,30*2, 1*2,0 //OKN　右向き　背景白30秒　1秒ポーズ
        )
        setRehalist()
    }
    private fun mini33(){//上級編３分
        list = intArrayOf(
            9,2,3,30*2, 3*2,0,//0.9Hz pursuit　背景チェッカー　30秒　3秒ポーズ
            9,2,3,30*2, 5*2,0,//0.9Hz pursuit　背景チェッカー　30秒　5秒ポーズ

            52,2,8,15*2, 3*2,0, //左右saccades 背景チェッカー　30秒  3秒ポーズ
            62,2,8,15*2, 5*2,0, //上下saccades 背景チェッカー　30秒  5秒ポーズ

            2,0,8,30*2, 3*2,0, //左右VOR×１背景白30秒　3秒ポーズ　立位
            2,0,8,30*2, 1*2,0 //上下VOR×１背景白30秒　1秒ポーズ　立位
        )
        setRehalist()
    }
    private fun mini35(){//上級編５分
        var rehaList = ArrayList<Int>()
        list = intArrayOf(
            9,2,3,30*2, 3*2,0,//0.9Hz pursuit　背景チェッカー　30秒　3秒ポーズ
            9,3,3,30*2, 5*2,0,//0.9Hz pursuit　背景カメラ　30秒　5秒ポーズ

            72,2,8,15*2, 3*2,0, //左右ランダムsaccades 背景チェッカー　30秒  3秒ポーズ
            82,2,8,15*2, 5*2,0, //上下ランダムsaccades 背景チェッカー　30秒  5秒ポーズ
            72,3,8,15*2, 3*2,0, //左右ランダムsaccades 背景カメラ　30秒  3秒ポーズ
            82,3,8,15*2, 5*2,0, //上下ランダムsaccades 背景カメラ　30秒  5秒ポーズ

            2,0,8,60*2, 3*2,0, //左右VOR×１背景白60秒　3秒ポーズ　立位
            2,0,8,60*2, 1*2,0//上下VOR×１背景白60秒　1秒ポーズ　立位
        )
        setRehalist()
    }
    private fun mini310(){//上級編１０分
        list = intArrayOf(
            9,2,3,30*2, 3*2,0,//0.9Hz pursuit　背景チェッカー　30秒　3秒ポーズ
            9,2,3,30*2, 5*2,0,//0.9Hz pursuit　背景チェッカー 30秒　5秒ポーズ
            9,3,3,30*2, 3*2,0,//0.9Hz pursuit　背景カメラ　30秒　3秒ポーズ
            9,3,3,30*2, 5*2,0,//0.9Hz pursuit　背景カメラ　30秒　5秒ポーズ

            92,2,8,15*2, 3*2,0, //左右ランダムsaccades 背景チェッカー　30秒  3秒ポーズ
            92,2,8,15*2, 5*2,0, //上下ランダムsaccades 背景チェッカー　30秒  5秒ポーズ
            92,3,8,15*2, 3*2,0, //左右ランダムsaccades 背景カメラ　30秒  3秒ポーズ
            92,3,8,15*2, 5*2,0, //上下ランダムsaccades 背景カメラ　30秒  5秒ポーズ

            2,2,8,60*2, 3*2,0, //左右VOR×１背景チェッカー　60秒　3秒ポーズ　立位
            2,2,8,60*2, 5*2,0, //左右VOR×１背景チェッカー　60秒　5秒ポーズ　立位
            2,2,8,60*2, 3*2,0, //上下VOR×１背景チェッカー　60秒　3秒ポーズ　立位
            2,2,8,60*2, 5*2,0, //上下VOR×１背景チェッカー　60秒　5秒ポーズ　立位

            2,2,8,30*2, 3*2,0, //左VOR cancellation背景チェッカー　30秒　3秒ポーズ立位
            2,2,8,30*2, 5*2,0, //上VOR cancellation背景チェッカー　30秒　5秒ポーズ立位

            122,0,3,30*2, 3*2,0, //OKN 左向き　背景白30秒　3秒ポーズ
            222,0,0,30*2, 1*2,0 //OKN　右向き　背景白30秒　1秒ポーズ
        )
        setRehalist()
    }
}
