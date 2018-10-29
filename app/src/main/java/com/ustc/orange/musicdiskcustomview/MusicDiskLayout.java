package com.ustc.orange.musicdiskcustomview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Random;

/**
 * 实现了类似抖音的唱片功能，唱片大小默认为布局的0.3倍
 * 布局最好是正方形，长方形需自行校准坐标
 * @author orange910617@gmail.com
 * 2018/10/26
 */
public class MusicDiskLayout extends FrameLayout {

  /** 飘动的音符资源文件，数量自取，这里是6个文件 **/
  private final Drawable[] MUSIC_DRAWABLES = new Drawable[6];
  /** 音符动画的持续时间，默认为3s **/
  private int mAnimationDurationMills = 3000;
  /** 唱片的图片素材 **/
  private Drawable mDiskDrawable;
  /** 音符动画的size，默认为布局的1／6，可随意调整 **/
  private LayoutParams mMusicIconLayoutParams;
  private BezierEvaluator mBezierEvaluator;
  private Random mRandom = new Random();
  /** 同时出现在页面中的音符个数，默认为2个，可设置 **/
  private int mMusicIconNum = 2;

  /** 音符动画起始点 **/
  private PointF mPointFStart;
  /** 音符动画终止点 **/
  private PointF mPointFEnd;
  /** 音符动画二阶贝塞尔曲线控制点 **/
  private PointF mPointFControl;

  private ImageView mDiskView;
  /** 执行周期性任务的handler **/
  private ScheduleHandler mScheduleHandler;

  public MusicDiskLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    initParams();
  }

  public MusicDiskLayout(@NonNull Context context, @Nullable AttributeSet attrs,
                         int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initParams();
  }

  /** 动画开始 **/
  public void start() {
    startRotateAnimator();
    if (mScheduleHandler == null) {
      mScheduleHandler = new ScheduleHandler(mAnimationDurationMills / mMusicIconNum, () -> createParticle().mAnimatorSet.start());
    }
    if (!mScheduleHandler.isRunning()) {
      mScheduleHandler.start();
    }
  }

  /** 动画停止 **/
  public void stop() {
    mDiskView.clearAnimation();
    if (mScheduleHandler.isRunning()) {
      mScheduleHandler.stop();
    }
  }

  /** 初始化，必须调用 **/
  public void init() {
    int size = getLayoutParams().width;
    int musicIconSize = size / 6;
    initDiskView(size);

    mPointFControl = new PointF(0.20f * size, 0.5f * size);
    mPointFStart = new PointF(0.85f * size, 0.65f * size);
    mPointFEnd = new PointF(0.70f * size, 0.20f * size);

    mMusicIconLayoutParams = new LayoutParams(musicIconSize, musicIconSize);
    mBezierEvaluator = new BezierEvaluator();
  }

  /**
   * 设置转盘的资源文件
   * @param drawableId 资源文件id
   * @return
   */
  public MusicDiskLayout setDiskDrawable(int drawableId) {
    this.mDiskDrawable = getResources().getDrawable(drawableId);
    return this;
  }

  /**
   * 设置动画时长，毫秒
   * @param animationDurationMills 时长
   */
  public MusicDiskLayout setAnimationDurationMills(int animationDurationMills) {
    this.mAnimationDurationMills = animationDurationMills;
    return this;
  }

  /**
   * 设置同时出现在页面中的音符个数
   * @param musicIconNum 个数
   */
  public MusicDiskLayout setMusicIconNum(int musicIconNum) {
    this.mMusicIconNum = musicIconNum;
    return this;
  }

  /** 初始化一些对象的默认值 */
  private void initParams() {
    MUSIC_DRAWABLES[0] = getResources().getDrawable(R.drawable.music_icon_0);
    MUSIC_DRAWABLES[1] = getResources().getDrawable(R.drawable.music_icon_1);
    MUSIC_DRAWABLES[2] = getResources().getDrawable(R.drawable.music_icon_2);
    MUSIC_DRAWABLES[3] = getResources().getDrawable(R.drawable.music_icon_3);
    MUSIC_DRAWABLES[4] = getResources().getDrawable(R.drawable.music_icon_4);
    MUSIC_DRAWABLES[5] = getResources().getDrawable(R.drawable.music_icon_5);
    mDiskDrawable = getResources().getDrawable(R.drawable.music_disk);
  }

  /**
   * 初始化转盘，位置靠右，垂直居中
   * 转盘的大小为父布局的0.3倍，如更改比例，控制点／起始点／终止点的坐标需要进行调整
   * @param parentSize 父控件布局的大小
   */
  private void initDiskView(int parentSize) {
    mDiskView = new ImageView(getContext());
    mDiskView.setLayoutParams(new LayoutParams((int) (0.3 * parentSize), (int) (0.3 * parentSize)));
    mDiskView.setX(0.70f * parentSize);
    mDiskView.setY(0.35f * parentSize);
    mDiskView.setImageDrawable(mDiskDrawable);
    addView(mDiskView);
  }

  /**
   * 唱片旋转动画，持续时长为飘动动画的2倍
   */
  private void startRotateAnimator() {
    ObjectAnimator rotate = ObjectAnimator.ofFloat(mDiskView, View.ROTATION, 0, 360);
    rotate.setInterpolator(new LinearInterpolator());
    rotate.setDuration(mAnimationDurationMills * 2);
    rotate.setRepeatCount(-1);
    rotate.start();
  }

  /**
   * 创建漂飘动的音符
   * 添加了三组动画，可根据自己需要进行调整
   */
  private MusicParticle createParticle() {
    final MusicParticle particle = new MusicParticle();
    particle.mImageView = new ImageView(getContext());
    int index = mRandom.nextInt(120) % MUSIC_DRAWABLES.length;
    particle.mImageView.setImageDrawable(MUSIC_DRAWABLES[index]);
    particle.mImageView.setLayoutParams(mMusicIconLayoutParams);

    // 缩放动画
    ObjectAnimator scaleX =
        ObjectAnimator.ofFloat(particle.mImageView, View.SCALE_X, 0.4f, 1f, 0.4f);
    ObjectAnimator scaleY =
        ObjectAnimator.ofFloat(particle.mImageView, View.SCALE_Y, 0.4f, 1f, 0.4f);
    scaleX.setDuration(mAnimationDurationMills);
    scaleY.setDuration(mAnimationDurationMills);

    // 旋转动画
    ObjectAnimator rotate = ObjectAnimator.ofFloat(particle.mImageView, View.ROTATION, 0, -45);
    rotate.setDuration(mAnimationDurationMills);

    // 平移动画
    ValueAnimator move = ValueAnimator.ofObject(mBezierEvaluator, mPointFStart, mPointFEnd);
    move.setDuration(mAnimationDurationMills);
    move.addUpdateListener(new AnimatorUpdateListener(particle));

    particle.mAnimatorSet = new AnimatorSet();
    particle.mAnimatorSet.playTogether(scaleX, scaleY, move, rotate);
    particle.mAnimatorSet.setInterpolator(new LinearInterpolator());
    particle.mAnimatorSet.addListener(new AnimatorListener(particle));

    addView(particle.mImageView);
    return particle;
  }

  private static class MusicParticle {
    private ImageView mImageView;
    private AnimatorSet mAnimatorSet;
  }

  /**
   * 贝塞尔曲线，用于控制音符运动轨迹
   * 这里使用的是二阶贝塞尔曲线
   */
  private class BezierEvaluator implements TypeEvaluator<PointF> {
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
      PointF point = new PointF();
      float timeLeft = 1.0f - fraction;
      point.x = timeLeft * timeLeft * (startValue.x)
          + 2 * timeLeft * fraction * (mPointFControl.x)
          + fraction * fraction * (endValue.x);

      point.y = timeLeft * timeLeft * (startValue.y)
          + 2 * timeLeft * fraction * (mPointFControl.y)
          + fraction * fraction * (endValue.y);
      return point;
    }
  }

  private class AnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    private MusicParticle mParticle;

    public AnimatorUpdateListener(MusicParticle particle) {
      mParticle = particle;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
      PointF pointF = (PointF) animation.getAnimatedValue();
      mParticle.mImageView.setX(pointF.x);
      mParticle.mImageView.setY(pointF.y);
      if (animation.getAnimatedFraction() > 0.5f) {
        mParticle.mImageView.setAlpha(2 - 2 * animation.getAnimatedFraction());
      } else {
        mParticle.mImageView.setAlpha(2 * animation.getAnimatedFraction());
      }
    }
  }

  /**
   * 动画监听，可根据需求自己添加一些控制逻辑
   */
  private class AnimatorListener extends AnimatorListenerAdapter {
    private MusicParticle mParticle;

    public AnimatorListener(MusicParticle particle) {
      mParticle = particle;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
      mParticle.mAnimatorSet.removeAllListeners();
      removeView(mParticle.mImageView);
      mParticle = null;
    }
  }
}
