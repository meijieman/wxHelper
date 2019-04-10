package com.major.wxhelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String WECAHT_PACKAGENAME = "com.tencent.mm";

    //微信首页
    public static final String WECHAT_CLASS_LAUNCHUI = "com.tencent.mm.ui.LauncherUI";
    //微信联系人页面
    public static final String WECHAT_CLASS_CONTACTINFOUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    //微信聊天页面
    public static final String WECHAT_CLASS_CHATUI = "com.tencent.mm.ui.chatting.ChattingUI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn_1).setOnClickListener(v->{
            // 开启辅助服务
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);

        });

        findViewById(R.id.btn_2).setOnClickListener(v->{
            // 启动微信
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(WECAHT_PACKAGENAME, WECHAT_CLASS_LAUNCHUI);
            startActivity(intent);

        });

        init();
    }

    private TextView start, sendStatus;
    private EditText sendName, sendContent;
    private AccessibilityManager accessibilityManager;
    private String name, content;


    private void init() {
        start = findViewById(R.id.testWechat);
        sendName = findViewById(R.id.sendName);
        sendContent = findViewById(R.id.sendContent);
        sendStatus = findViewById(R.id.sendStatus);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndStartService();
            }
        });
    }

    private int goWecaht() {
        try {
            setValue(name, content);
            MyAccessibilityService.hasSend = false;
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(WeChatTextWrapper.WECAHT_PACKAGENAME, WeChatTextWrapper.WechatClass.WECHAT_CLASS_LAUNCHUI);
            startActivity(intent);

            while (true) {
                if (MyAccessibilityService.hasSend) {
                    return MyAccessibilityService.SEND_STATUS;
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        openService();
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return MyAccessibilityService.SEND_STATUS;
        }
    }


    private void openService() {
        try {
            //打开系统设置中辅助功能
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "找到微信自动发送消息，然后开启服务即可", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndStartService() {
        accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);

        name = sendName.getText().toString();
        content = sendContent.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(MainActivity.this, "联系人不能为空", Toast.LENGTH_SHORT);
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(MainActivity.this, "内容不能为空", Toast.LENGTH_SHORT);
        }

        if (!accessibilityManager.isEnabled()) {
            openService();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    statusHandler.sendEmptyMessage(goWecaht());
                }
            }).start();
        }
    }

    Handler statusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setSendStatusText(msg.what);
        }
    };

    private void setSendStatusText(int status) {
        if (status == MyAccessibilityService.SEND_SUCCESS) {
            sendStatus.setText("微信发送成功");
        } else {
            sendStatus.setText("微信发送失败");
        }
    }

    public void setValue(String name, String content) {
        WechatUtils.NAME = name;
        WechatUtils.CONTENT = content;
        MyAccessibilityService.hasSend = false;
    }
}
