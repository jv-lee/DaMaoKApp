package com.lockstudio.sticklocker.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import cn.opda.android.activity.R;


public class Splash {

    private final int MSG_START_ANIMATION = 10;
    private final int MSG_START_FINISH = 11;
    private final int MSG_FINISH = 12;
    private final int MSG_SHOW_STARTING_LOGO = 13;

    private Activity mActivity;
    private View splashView;
    private OnSplashFinishedListener onSplashFinishedListener;

    public Splash(Activity activity, OnSplashFinishedListener onSplashFinishedListener) {
        this.mActivity = activity;
        this.onSplashFinishedListener = onSplashFinishedListener;

        splashView= mActivity.findViewById(R.id.splash_layout);
    }

    public void start() {
        handler.sendEmptyMessageDelayed(MSG_START_ANIMATION, 1000);
    }

    public void ignore() {
        splashView.setVisibility(View.GONE);
        if (onSplashFinishedListener != null) {
            onSplashFinishedListener.onSplashFinished();
        }
    }

    private void startAnimation() {
        View view = splashView.findViewById(R.id.splash_white);
        if (view != null) {
            Animation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            translateAnimation.setDuration(600);
            translateAnimation.setFillAfter(true);
            translateAnimation.setFillEnabled(true);
            translateAnimation.setInterpolator(mActivity, android.R.anim.decelerate_interpolator);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    handler.sendEmptyMessage(MSG_SHOW_STARTING_LOGO);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(translateAnimation);
            view.setVisibility(View.VISIBLE);
        }
    }

    public void finish() {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handler.sendEmptyMessage(MSG_FINISH);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        View v = splashView.findViewById(R.id.splash_root_layout);
        if (v != null) {
            v.startAnimation(animation);
        } else {
            handler.sendEmptyMessage(MSG_FINISH);
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_ANIMATION:
                    startAnimation();
                    break;

                case MSG_FINISH:
                	handler.removeMessages(MSG_FINISH);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        	 if (onSplashFinishedListener != null && splashView.getVisibility()==View.VISIBLE) {
                                 onSplashFinishedListener.onSplashFinished();
                             }
                             splashView.setVisibility(View.GONE);
                        }
                    });
                    break;

                case MSG_START_FINISH:
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                    break;

                case MSG_SHOW_STARTING_LOGO:
                    if (MConstants.SHOWFA) {
                        View logo = splashView.findViewById(R.id.splash_starting_logo);
                        if (logo != null) {
                            AlphaAnimation animation = new AlphaAnimation(0, 1);
                            animation.setDuration(200);
                            logo.startAnimation(animation);
                            logo.setVisibility(View.VISIBLE);
                        }
                        handler.sendEmptyMessageDelayed(MSG_START_FINISH, 1200);
                    } else {
                        handler.sendEmptyMessageDelayed(MSG_START_FINISH, 1000);
                    }
                    break;

                default:
                    break;
            }
            return false;
        }
    });


    public interface OnSplashFinishedListener {
        void onSplashFinished();
    }
}
