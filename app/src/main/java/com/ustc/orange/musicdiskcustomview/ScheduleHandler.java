package com.ustc.orange.musicdiskcustomview;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ScheduleHandler extends Handler {

  private final int mInterval;
  private final Runnable mRunnable;
  private boolean mStopped = true;

  public ScheduleHandler(Looper looper, int interval, Runnable runnable) {
    super(looper);
    mInterval = interval;
    mRunnable = runnable;
  }

  public ScheduleHandler(int interval, Runnable runnable) {
    mInterval = interval;
    mRunnable = runnable;
  }

  @Override
  public void handleMessage(Message msg) {
    if (mStopped) {
      return;
    }
    mRunnable.run();
    sendEmptyMessageDelayed(0, mInterval);
  }

  public void start() {
    if (!mStopped) {
      return;
    }
    mStopped = false;
    sendEmptyMessage(0);
  }

  public void delayStart() {
    if (!mStopped) {
      return;
    }
    mStopped = false;
    sendEmptyMessageDelayed(0, mInterval);
  }

  public void stop() {
    mStopped = true;
    removeMessages(0);
  }

  public boolean isRunning() {
    return !mStopped;
  }
}
