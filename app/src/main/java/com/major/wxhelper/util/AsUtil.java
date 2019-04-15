package com.major.wxhelper.util;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.major.base.util.CommonUtil;

import java.util.List;

/**
 * Desc: 辅助功能工具类
 * <p>
 * Author: meijie
 * PackageName: com.major.wxhelper
 * ProjectName: wxHelper
 * Date: 2019/4/10 18:33
 */
public class AsUtil {

    /**
     * 在当前页面查找文字内容并点击
     */
    public static void clickViewByText(AccessibilityService service, String text) {
        List<AccessibilityNodeInfo> nodes = findNodesByText(service, text);
        if (CommonUtil.isNotEmpty(nodes)) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node != null
                        && (text.equals(node.getText().toString()) || text.equals(node.getContentDescription().toString()))) {
                    click(node);
                    break;
                }
            }
        }
    }

    /**
     * 根据 viewId 点击 view
     */
    public static boolean clickViewById(AccessibilityService service, String id) {
        AccessibilityNodeInfo node = findNodeById(service, id);
        if (node != null) {
            return click(node);
        }
        return false;
    }

    // 查找一系列相同内容的控件
    public static List<AccessibilityNodeInfo> findNodesByText(AccessibilityService service, String text) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            return rootNode.findAccessibilityNodeInfosByText(text);
        }

        return null;
    }

    // 根据 id 获取控件（当前页面第一个获取到的）
    public static AccessibilityNodeInfo findNodeById(AccessibilityService service, String id) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(id);
            if (CommonUtil.isNotEmpty(nodes)) {
                return nodes.get(0);
            }
        }

        return null;
    }

    public static List<AccessibilityNodeInfo> findNodesById(AccessibilityService service, String id) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(id);
            if (CommonUtil.isNotEmpty(nodes)) {
                return nodes;
            }
        }

        return null;
    }

    @SuppressWarnings("api")
    public static boolean findViewByIdAndPasteContent(AccessibilityService service, String id, String content) {
        AccessibilityNodeInfo node = findNodeById(service, id);
        if (node != null) {
            Bundle bundle = new Bundle();
            bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
            return true;
        }
        return false;
    }

    public static String findTextById(AccessibilityService service, String id) {
        AccessibilityNodeInfo node = findNodeById(service, id);
        if (node != null) {
            String text = node.getText().toString();
            return text;
        }
        return null;
    }

    /**
     * 在当前页面查找对话框文字内容并点击
     *
     * @param text1 默认点击text1
     */
    public static void findDialogAndClick(AccessibilityService service, String text1, String text2) {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }

        List<AccessibilityNodeInfo> dialogWait = rootNode.findAccessibilityNodeInfosByText(text1);
        List<AccessibilityNodeInfo> dialogConfirm = rootNode.findAccessibilityNodeInfosByText(text2);
        if (!dialogWait.isEmpty() && !dialogConfirm.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : dialogWait) {
                if (nodeInfo != null && text1.equals(nodeInfo.getText().toString())) {
                    click(nodeInfo);
                    break;
                }
            }
        }
    }

    //模拟点击事件
    public static boolean click(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return false;
        }
        if (nodeInfo.isClickable()) {
            return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            return click(nodeInfo.getParent());
        }
    }

    //模拟返回事件
    public static void performBack(AccessibilityService service) {
        if (service == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }
}
