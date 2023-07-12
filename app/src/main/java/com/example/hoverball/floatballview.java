package com.example.hoverball;
/**
 * Author:Kevinxu
 * Date:2023/07/10
 * Description:Simple implementation of the hoverball.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class floatballview {
    private static String TAG = "floatballview";

    private Context context;

    private int height = 0;

    private int width = 0;

    public static floatballview floatView2;//单例模式中的饿汉模式

    public static floatballview getInstance(Context context) {
        if (floatView2 == null) {
            floatView2 = new floatballview(context);
        }
        return floatView2;
    }

    public floatballview(Context context) {

        this.context = context;

    }

    private WindowManager wm;

    private View view;// 悬浮球view

    WindowManager.LayoutParams params;// 控制悬浮球

    /**
     * 添加悬浮View
     *
     * @param
     */

    public void createFloatView() {

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.home_floatview, null);//(ViewGroup)this.view
            //将xml文件装换成view对象，加入当前的viewgroup树，如果root==null，就是加入根viewGroup
            //api讲解链接https://blog.csdn.net/lu202032/article/details/128430287
        }

        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);//获取系统服务WindowManagerService
        //WindowManager可以添加view到屏幕，也可以从屏幕删除view。它面向的对象一端是屏幕，另一端就是View
        height = wm.getDefaultDisplay().getHeight();//获取屏幕宽和高的第一种方法,获取到的值以像素点px为单位
        width = wm.getDefaultDisplay().getWidth();//我们这里具体下来，宽和高分别为1080px,2040px
        Log.d(TAG, "屏幕高wm.getDefaultDisplay().getHeight() " + height+"px");
        Log.d(TAG, "屏幕宽wm.getDefaultDisplay().getWidth() " + width+"px");

        params = new WindowManager.LayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //添加 FLAG_NOT_TOUCH_MODAL 和 FLAG_NOT_FOCUSABLE 后，浮窗外的点击事件由浮窗外响应，但是浮窗内的点击事件，则浮窗给响应了。
        //这个 | 下来的结果等于40   32|8=40
        //干货讲解：https://blog.csdn.net/WillWolf_Wang/article/details/120778785
        params.format = PixelFormat.TRANSLUCENT;//设置悬浮球半透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//判断当前版本是否可以使用响应的API
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;//新添加的view,设置为悬浮窗事件
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//WRAP_CONTENT表示view的大小和自身内容大小相同
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP | Gravity.LEFT;//设置从哪里开始算view的位置，这里是从左上开始，左上角为坐标(0,0)。
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        //获取屏幕高宽的第二种方法，和第一种方法得到的值相等，单位也是px 像素点
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
//        Log.d(TAG, "context.getResources().getDisplayMetrics().widthPixels " + screenWidth);
//        Log.d(TAG, "context.getResources().getDisplayMetrics().heightPixels " + screenHeight);
        params.x = screenWidth;//从左上角(0,0)开始，计算view的(x,y)坐标,(x,y)位于自定义view的右下角 对应gravity设置为 TOP LEFT
        params.y = screenHeight - height / 3;//自由控制悬浮view的显示位置
//        Log.d(TAG, "screenWidth " + params.x);
//        Log.d(TAG, "screenHeight - height / 3 " + params.y);

        view.setBackgroundColor(Color.TRANSPARENT);//无背景，背景透明
        //view.setVisibility(View.VISIBLE);
        //让自定义view可见，​
        // View.INVISIBLE不可见，但是它原来占用的位子还在。View.GONE不可见，并且不留痕迹，不占位置

        view.setOnTouchListener(new View.OnTouchListener() {//触屏事件监听
            float lastX, lastY;
            int oldOffsetX, oldOffsetY;
            int tag = 0;// 悬浮球 所需成员变量

            @Override
            public boolean onTouch(View v, MotionEvent event) {//每一个触控事件 DOWN UP MOVE CANCEL都会重新走一遍onTouch
                final int action = event.getAction();
                Log.d(TAG, "event.getAction() " + action);
                float x = event.getX();
                //屏幕上的点击点，相对于自定义view的位置，获取的单位也是px
                //这里由于gravity定义的是左上，所以是相对于左上(0,0)的坐标距离
                float y = event.getY();
                //这里有点难理解，建议看图理解 https://blog.csdn.net/yiyihuazi/article/details/82724557
                //获取点击点的“相对位置”
//                Log.d(TAG, "event.getX() " + x);
//                Log.d(TAG, "event.getY() " + y);
//                Log.d(TAG, "event.getRawX() " + event.getRawX());//getRawX()则是相对于整个屏幕的
//                Log.d(TAG, "event.getRawY() " + event.getRawY());
                if (tag == 0) {//tag 用来控制 oldOffsetX/Y 取得的值，一定是悬浮球最开始的坐标。
                    oldOffsetX = params.x;//悬浮球在屏幕上的老坐标。
                    oldOffsetY = params.y;
//                    Log.d(TAG, "tag 为0 oldOffsetX " + oldOffsetX);
//                    Log.d(TAG, "tag 为0 oldOffsetY " + oldOffsetY);
                }

                if (action == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "action == MotionEvent.ACTION_DOWN action= " + action);
                    lastX = x;
                    lastY = y;
//                    Log.d(TAG, "action == MotionEvent.ACTION_DOWN lastX = " + lastX);
//                    Log.d(TAG, "action == MotionEvent.ACTION_DOWN lastY = " + lastY);
                } else if (action == MotionEvent.ACTION_MOVE) {
                    Log.d(TAG, "action == MotionEvent.ACTION_MOVE action= " + action);
                    params.x += (int) (x - lastX) / 3; // 减小偏移量,防止过度抖动
                    params.y += (int) (y - lastY) / 3; // 减小偏移量,防止过度抖动
//                    Log.d(TAG,"params.x += (int) (x - lastX) / 3 =="+params.x);
//                    Log.d(TAG,"params.y += (int) (y - lastY) / 3 =="+params.y);
                    tag = 1;
                    wm.updateViewLayout(view, params);//更新自定义view位置
                } else if (action == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "action == MotionEvent.ACTION_UP action= " + action);
                    int newOffsetX = params.x;//悬浮球新的显示位置
                    int newOffsetY = params.y;
//                    Log.d(TAG,"newOffsetX = params.x =="+newOffsetX);
//                    Log.d(TAG,"newOffsetY = params.y =="+newOffsetY);
                    // 只要按钮移动位置不是很大,就认为是点击事件
                    if (Math.abs(oldOffsetX - newOffsetX) <= 20 && Math.abs(oldOffsetY - newOffsetY) <= 20) {
                        //20像素之内的移动默认为点击事件
                        Log.d(TAG,"Math.abs(oldOffsetX - newOffsetX)的值为："+Math.abs(oldOffsetX - newOffsetX));
                        Log.d(TAG,"Math.abs(oldOffsetY - newOffsetY)的值为："+Math.abs(oldOffsetY - newOffsetY));
                        //Math.abs取绝对值
                        //Math.abs规则详述 https://blog.csdn.net/weixin_49431999/article/details/121010819
                        if (MyClickListener != null) {
                            Log.d(TAG, "1 不等于空，走进点击事件");
                            MyClickListener.onClick(view);
                        }
                    } else {
                        if (params.x < width / 2) {//控制悬浮球始终靠着左右边框
                            //params.gravity的显示方向发生变化，这里如果还想保持原功能，也需要做一下修改适配
                            params.x = 0;
                        } else {
                            params.x = width;
                        }
                        wm.updateViewLayout(view, params);
                        tag = 0;
                    }
                }
                return true;
            }
        });

        try {
            wm.addView(view, params);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.context, "WindowManager 添加自定义View失败，详情查看打印堆栈", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 点击浮动按钮触发事件，需要override该方法
     */

    private View.OnClickListener MyClickListener;

    public void onFloatViewClick(View.OnClickListener ClickListener) {
        this.MyClickListener = ClickListener;
    }

    /**
     * 将悬浮View从WindowManager中移除，需要与createFloatView()成对出现
     */

    public void removeFloatView() {
        if (wm != null && view != null) {
            wm.removeViewImmediate(view);
// wm.removeView(view);//不要调用这个，WindowLeaked
            view = null;
            wm = null;
        }
    }

    /**
     * 隐藏悬浮View
     */

//    public void hideFloatView() {
//        if (wm != null && view != null && view.isShown()) {
//            view.setVisibility(View.GONE);
//        }
//    }

    /**
     * 显示悬浮View
     */

//    public void showFloatView() {
//        if (wm != null && view != null && !view.isShown()) {
//            view.setVisibility(View.VISIBLE);
//        }
//    }

//    public void updateViewLayout() {
//        if (wm != null) {
//            int screenWidth = (int) 480;
//            int screenHeight = (int) 720;
//            if (screenWidth == 0) {
//                screenWidth = context.getResources().getDisplayMetrics().widthPixels;
//            }
//
//            if (screenHeight == 0) {
//                screenHeight = context.getResources().getDisplayMetrics().heightPixels;
//                params.y = screenHeight - height / 3;//设置距离底部高度为屏幕三分之一
//            } else {
//                params.y = screenHeight;
//            }
//            params.x = screenWidth;
//            wm.updateViewLayout(view, params);
//        }
//
//    }

}
