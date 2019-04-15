package com.major.wxhelper;

/**
 * Desc: TODO
 * <p>
 * Author: meijie
 * PackageName: com.major.wxhelper
 * ProjectName: wxHelper
 * Date: 2019/4/10 18:28
 */
public interface WxConstant {

    String PKG_NAME = "com.tencent.mm";

    // 首页，从底部栏微信点入的聊天界面的 activity 也叫这个
    String ACT_LAUNCHER_UI = "com.tencent.mm.ui.LauncherUI";
    // 个人信息展示页面（点击头像进入的那个页面）
    String ACT_CONTACTINFOUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
    // 聊天
    String ACT_CHATUI = "com.tencent.mm.ui.chatting.ChattingUI";
    // 朋友圈
    String ACT_SNS_USERUI = "com.tencent.mm.plugin.sns.ui.SnsUserUI";
    // 支付
    String ACT_PAY = "com.tencent.mm.plugin.mall.ui.MallIndexUI";


    // wx 7.0.3
    /*
     通讯录界面
     */
    String ID_CONTACTUI_LISTVIEW_ID = "com.tencent.mm:id/mi";
    String ID_CONTACTUI_ITEM_ID = "com.tencent.mm:id/ng";
    String ID_CONTACTUI_NAME_ID = "com.tencent.mm:id/ng";

    /*
     聊天界面
     */
    String ID_CHATUI_EDITTEXT_ID = "com.tencent.mm:id/amb";
    String ID_CHATUI_USERNAME_ID = "com.tencent.mm:id/k3";
    String ID_CHATUI_SWITCH_ID = "com.tencent.mm:id/a_x";
    String ID_CHATUI_TEXT = "com.tencent.mm:id/nu"; // 文本
    String ID_CHATUI_URL = "com.tencent.mm:id/apv"; // 超链接
    String ID_CHATUI_TIME = "com.tencent.mm:id/ag";

    String ID_CHATUI_BACK_ID = "com.tencent.mm:id/k2";    // 返回键

    /*
        支付返回键，个人信息展示页面返回键
     */
    String ID_CONTACT_INFO_UI_BACK_ID = "com.tencent.mm:id/kb";
}
