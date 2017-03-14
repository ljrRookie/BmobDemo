package com.example.bmobdemo.Util;

import android.os.CountDownTimer;

import com.example.bmobdemo.activity.MainActivity;

/**
 * Created by user on 2017/3/12.
 */

public class MyTimer extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public MyTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }
    //计时过程
    @Override
    public void onTick(long millisUntilFinished) {


    }
    //计时完成
    @Override
    public void onFinish() {

    }
}
