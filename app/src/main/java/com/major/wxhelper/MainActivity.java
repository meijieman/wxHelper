package com.major.wxhelper;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView sendStatus;
    private EditText sendName, sendContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_1).setOnClickListener(v -> {

        });

        findViewById(R.id.btn_2).setOnClickListener(v -> {

        });

        init();
    }

    private void init() {
        TextView start = findViewById(R.id.testWechat);
        sendName = findViewById(R.id.sendName);
        sendContent = findViewById(R.id.sendContent);
        sendStatus = findViewById(R.id.sendStatus);
        start.setOnClickListener(v -> checkAndStartService());
    }

    @WorkerThread
    private int send(String name, String content) {
        WechatUtils.NAME = name;
        WechatUtils.CONTENT = content;
        MyAccessibilityService.hasSend = false;

        // 启动微信
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(WeChatTextWrapper.WECAHT_PACKAGENAME, WeChatTextWrapper.WechatClass.WECHAT_CLASS_LAUNCHUI);
        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivity(intent);
        } else {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "未安装wx", Toast.LENGTH_SHORT).show());
        }

        while (true) {
            if (MyAccessibilityService.hasSend) {
                return MyAccessibilityService.sSendStatus;
            } else {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    openService();
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkAndStartService() {
        String name = sendName.getText().toString();
        String content = sendContent.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(MainActivity.this, "联系人不能为空", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(MainActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
        }

        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (!accessibilityManager.isEnabled()) {
            openService();
        } else {
            new Thread(() -> {
                int what = send(name, content);
                statusHandler.sendEmptyMessage(what);
            }).start();
        }
    }

    private void openService() {
        //打开系统设置中辅助功能
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
            Toast.makeText(MainActivity.this, "找到微信自动发送消息，然后开启服务即可", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("all")
    Handler statusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MyAccessibilityService.SEND_SUCCESS) {
                sendStatus.setText("微信发送成功");
            } else {
                sendStatus.setText("微信发送失败");
            }
        }
    };
}
