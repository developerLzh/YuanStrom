package com.lzh.yuanstrom.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;

import com.lzh.yuanstrom.adapter.DevAdapter2;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/6/17.
 */
public class CardViewExpand {
    public static void expand(final View v, final DevAdapter2 adapter2) {

//        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        final int targetHeight = getTargetHeight(v);

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

//                Log.e("abcd", "interpolatedTime-->" + interpolatedTime + "----targetHeight--->" + targetHeight * interpolatedTime);

                if (interpolatedTime == 1) {
                    v.getLayoutParams().height = targetHeight;
                    adapter2.notifyDataSetChanged();
                } else {
                    v.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
                }
                v.requestLayout();

//                Log.e("efgh", "v.getMeasuredHeight()----->" + v.getMeasuredHeight());
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
//        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));

        a.setDuration(((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density)) * 1);

        v.startAnimation(a);
    }

    public static void collapse(final View v, final DevAdapter2 adapter2) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                Log.e("abcd", "interpolatedTime-->" + interpolatedTime + "----targetHeight--->" + (initialHeight - (int) (initialHeight * interpolatedTime)));
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                    adapter2.notifyDataSetChanged();
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density) * 1);
        v.startAnimation(a);
    }

    public static int getTargetHeight(View v) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class,
                    int.class);
            m.setAccessible(true);
            m.invoke(v, View.MeasureSpec.makeMeasureSpec(
                    ((View) v.getParent()).getMeasuredWidth(),
                    View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED));
        } catch (Exception e) {

        }
        return v.getMeasuredHeight();
    }

//    public static void expand(final View v, int duration, int targetHeight) {
//
//        int prevHeight  = 0;
//
////        Log.e("targetHeight",targetHeight+"");
//
//        v.setVisibility(View.VISIBLE);
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                v.getLayoutParams().height = (int) animation.getAnimatedValue();
//                v.requestLayout();
//            }
//        });
//        valueAnimator.setInterpolator(new DecelerateInterpolator());
//        valueAnimator.setDuration(duration);
//        valueAnimator.start();
//    }
//
//    public static void collapse(final View v, int duration, int targetHeight) {
//        int prevHeight  = v.getHeight();
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
//        valueAnimator.setInterpolator(new DecelerateInterpolator());
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                v.getLayoutParams().height = (int) animation.getAnimatedValue();
//                v.requestLayout();
//            }
//        });
//        valueAnimator.setInterpolator(new DecelerateInterpolator());
//        valueAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                v.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
//        valueAnimator.setDuration(duration);
//        valueAnimator.start();
//    }

}
