package com.example.hoverball;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "floatballview";
    Intent intent=new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        //进入侧边导航栏
        Button button4=findViewById(R.id.button4);

        //按钮1绑定事件
        button1.setOnClickListener(new View.OnClickListener() {
            //点击事件监听,OnClick事件监听是对OnTouch事件监听的封装，让app开发人员省略了对点击事件和滑动事件的区分
            //如果是创建自定义的view，需要自己去做滑动和点击的区分。
            @Override
            public void onClick(View v) {//动态申请悬浮窗权限,只有需要一直悬浮的才需要申请，如果只需要悬浮在当前应用，则不需要申请权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //当前的系统版本大于Android M版本
                    //这种写法经常用来判断响应的api接口是否可以使用。
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        //canDrawOverlays这个api只有Android M版本以上才有，这也是为什么上面要判断的原因。
                        //检查是否有悬浮窗权限
                        Log.d(TAG, "应用没有悬浮窗权限，打开授权页让用户授权");
                        //获取悬浮球权限的标准写法
                        Intent intent = new Intent();//Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                        intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        //这个权限还是需要动态申请，静态声明是没用的。
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        Log.d(TAG, "PackageNmae:" + getPackageName());
                        startActivityForResult(intent, 0);
                        //跳到权限授权页，由用户授权打开
                    }
                }
            }
        });
        //按钮2绑定事件
        button2.setOnClickListener(new View.OnClickListener() {//开启悬浮窗
            //内部匿名类高级写法+lambda缩写
            @Override
            public void onClick(View v) {
                floatballview.getInstance(MainActivity.this).createFloatView();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatballview.getInstance(MainActivity.this).removeFloatView();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(MainActivity.this,Side_navigation_bar.class);
                startActivity(intent);
            }
        });

        //点击悬浮按钮的响应事件,这里属于onCreate里面的的方法调用，Activity执行onCreat()生命周期
        //这里创建一个View.OnClickListener，同时内部匿名类重写点击事件，赋值给floatballview类的私有变量
        //功能
        floatballview.getInstance(MainActivity.this).onFloatViewClick(new View.OnClickListener() {
            //内部匿名类+lambda缩写  java高级写法
            @Override
            public void onClick(View v) {
                Log.d(TAG, "触发图片点击事件");
                Toast.makeText(MainActivity.this, "点击了悬浮球", Toast.LENGTH_LONG).show();
            }
        });
    }
}