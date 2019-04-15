package com.major.wxhelper;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Context;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.major.base.log.LogUtil;
import com.major.base.util.CommonUtil;
import com.major.wxhelper.util.AsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc: TODO
 * <p>
 * Author: meijie
 * PackageName: com.major.wxhelper
 * ProjectName: wxHelper
 * Date: 2019/4/9 16:55
 */
public class WxService extends AccessibilityService {

    public static final int SEND_SUCCESS = 1;
    public static final int SEND_FAIL = 2;

    public static boolean sHasSend;
    public static int sSendStatus;
    public static String sName; // 发送给用户的昵称
    public static String sContent; // 发送内容

    private List<String> allNameList = new ArrayList<>();
    private int mRepeatCount;
    private int mFlow = 2; // 当前处理的业务流程 0. 空闲 1.发送消息给指定用户 2. 获取当前打开的聊天记录（并回复聊天）

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.w("onCreate");
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
    protected boolean onKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                // 在聊天界面，点击发送按钮
                LogUtil.i("onKeyEvent ACTION_DOWN");
                break;

            case KeyEvent.ACTION_UP:

                break;
        }

        return super.onKeyEvent(event);
    }

    /**
     * 必须重写的方法，响应各种事件。
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
//        String pkgName = event.getPackageName().toString();
//        LogUtil.i("pkgName " + pkgName);

        if (mFlow == 1) {
            int eventType = event.getEventType();
            LogUtil.i("eventType " + eventType);
            switch (eventType) {
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                    if (sHasSend) {
                        return;
                    }

                    String currentActivity = event.getClassName().toString();
                    LogUtil.i("currentActivity " + currentActivity);
                    switch (currentActivity) {
                        case WxConstant.ACT_LAUNCHER_UI:
                            handleLaunchUI();
                            break;
                        case WxConstant.ACT_CONTACTINFOUI:
                            handleContactInfoUI();
                            break;
                        case WxConstant.ACT_CHATUI:
                            handleChatUI();
                            break;
                    }
                }
                break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    LogUtil.i("onAccessibilityEvent TYPE_VIEW_CLICKED ");
                    // 经测试，虚拟按键BACK键、HOME键、最近任务键和音量键全部拦截到

                    break;
            }
        } else if (mFlow == 2) {
            int eventType = event.getEventType();
            LogUtil.i("eventType " + eventType);
            switch (eventType) {
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    String currentActivity = event.getClassName().toString();
                    if (WxConstant.ACT_CONTACTINFOUI.equals(currentActivity)) {
                        // 聊天界面
                        // FIXME: 2019/4/16
                        List<AccessibilityNodeInfo> nodes = AsUtil.findNodesById(this, WxConstant.ID_CHATUI_TEXT);
                        for (AccessibilityNodeInfo node : nodes) {
                            LogUtil.w(node.getText());
                        }

                    }
                    break;
            }
        }
    }

    private void handleChatUI() {
        //如果微信已经处于聊天界面，需要判断当前联系人是不是需要发送的联系人
        String curUserName = AsUtil.findTextById(this, WxConstant.ID_CHATUI_USERNAME_ID);
        if (curUserName != null && curUserName.equals(sName)) {
            if (AsUtil.findViewByIdAndPasteContent(this, WxConstant.ID_CHATUI_EDITTEXT_ID, sContent)) {
                sendContent();
            } else {
                //当前页面可能处于发送语音状态，需要切换成发送文本状态
                AsUtil.clickViewById(this, WxConstant.ID_CHATUI_SWITCH_ID);
                sleep(100);
                if (AsUtil.findViewByIdAndPasteContent(this, WxConstant.ID_CHATUI_EDITTEXT_ID, sContent)) {
                    sendContent();
                }
            }
        } else {
            //回到主界面
            AsUtil.clickViewById(this, WxConstant.ID_CHATUI_BACK_ID);
        }
    }

    private void handleLaunchUI() {
        // 判断是不是在聊天界面
        sleep(100);
        boolean rst = AsUtil.clickViewById(this, WxConstant.ID_CHATUI_BACK_ID);
        LogUtil.i("rst " + rst);
        sleep(100);
        //点击通讯录，跳转到通讯录页面
        AsUtil.clickViewByText(this, "通讯录");
        sleep(100);
        //再次点击通讯录，确保通讯录列表移动到了顶部
        AsUtil.clickViewByText(this, "通讯录");
        sleep(200);

        // 遍历通讯录列表，查找联系人
        AccessibilityNodeInfo itemInfo = traversalAndFindContacts();
        if (itemInfo != null) {
            AsUtil.click(itemInfo);
        } else {
            sSendStatus = SEND_FAIL;
            LogUtil.e("发送失败");
//            resetAndReturnApp();
        }
    }

    private void handleContactInfoUI() {
        AsUtil.clickViewByText(this, "发消息");
    }

    /**
     * 从头至尾遍历寻找联系人
     *
     * @return
     */
    private AccessibilityNodeInfo traversalAndFindContacts() {
        if (allNameList != null) {
            allNameList.clear();
        }
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        // 获取 listView
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(WxConstant.ID_CONTACTUI_LISTVIEW_ID);

        //是否滚动到了底部
        boolean scrollToBottom = false;
        if (CommonUtil.isNotEmpty(nodes)) {
            while (true) {
                //获取当前屏幕上的联系人信息
                List<AccessibilityNodeInfo> nameList = rootNode.findAccessibilityNodeInfosByViewId(WxConstant.ID_CONTACTUI_NAME_ID);
                List<AccessibilityNodeInfo> itemList = rootNode.findAccessibilityNodeInfosByViewId(WxConstant.ID_CONTACTUI_ITEM_ID);

                if (nameList != null && !nameList.isEmpty()) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if (i == 0) {
                            //必须在一个循环内，防止翻页的时候名字发生重复
                            mRepeatCount = 0;
                        }
                        AccessibilityNodeInfo itemInfo = itemList.get(i);
                        AccessibilityNodeInfo nodeInfo = nameList.get(i);
                        String nickname = nodeInfo.getText().toString();
                        LogUtil.d("nickname = " + nickname);
                        if (nickname.equals(sName)) {
                            return itemInfo;
                        }
                        if (!allNameList.contains(nickname)) {
                            allNameList.add(nickname);
                        } else if (allNameList.contains(nickname)) {
                            LogUtil.d("mRepeatCount = " + mRepeatCount);
                            if (mRepeatCount == 3) {
                                //表示已经滑动到顶部了
                                if (scrollToBottom) {
                                    LogUtil.d("没有找到联系人");
                                    //此次发消息操作已经完成
                                    sHasSend = true;
                                    return null;
                                }
                                scrollToBottom = true;
                            }
                            mRepeatCount++;
                        }
                    }
                }

                if (!scrollToBottom) {
                    //向下滚动
                    nodes.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                } else {
                    return null;
                }

                //必须等待，因为需要等待滚动操作完成
                sleep(500);
            }
        }
        return null;
    }

    private void sendContent() {
        AsUtil.clickViewByText(this, "发送");
        sSendStatus = SEND_SUCCESS;
        sHasSend = true;
        sleep(3000);
        resetAndReturnApp();
    }

    private void resetAndReturnApp() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTasks) {
            if (getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                am.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }

    @Override
    public void onInterrupt() {
        LogUtil.w("onInterrupt");
    }


    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
