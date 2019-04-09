package com.major.wxhelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
    }
}
