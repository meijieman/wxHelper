package com.major.wxhelper;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.major.base.log.LogUtil;

import java.util.List;

/**
 * Desc: TODO
 * <p>
 * Author: meijie
 * PackageName: com.major.wxhelper
 * ProjectName: wxHelper
 * Date: 2019/4/9 16:55
 */
public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init(getPackageName(), "tag_wx", true, false);
    }

    @Override
    protected void onServiceConnected() {
//        AccessibilityServiceInfo info = getServiceInfo();
//        //这里可以设置多个包名，监听多个应用
//        info.packageNames = new String[]{"xxx.xxx.xxx", "yyy.yyy.yyy","...."};
//        setServiceInfo(info);

        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String pkgName = event.getPackageName().toString();
        LogUtil.i("pkgName " + pkgName);

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }

        // 第一种是通过节点View的Text内容来查找
        List<AccessibilityNodeInfo> nodeInfoList = rootNode.findAccessibilityNodeInfosByText("微信");
        LogUtil.i("pkgName nodes " + nodeInfoList.size());
        for (AccessibilityNodeInfo info : nodeInfoList) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 第二种是通过节点View在xml布局中的id名称
        rootNode.findAccessibilityNodeInfosByViewId("");

        // 模拟点击指定事件
        rootNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);


        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                String currentActivity = event.getClassName().toString();
                LogUtil.i("currentActivity " + currentActivity);
//                com.tencent.mm.ui.LauncherUI


                break;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
