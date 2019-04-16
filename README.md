https://github.com/Clearlee/AutoSendWeChatMsg

https://www.jianshu.com/p/5cac6d439eeb?from=timeline

AccessibilityService分析与防御
https://blog.csdn.net/u010255127/article/details/79184399

UiAutomatorViewer 查看界面的布局层次
D:\android\sdk\tools\bin\uiautomatorviewer.bat

当前界面 activity
adb shell dumpsys window | findstr mCurrentFocus


```
/**
 * 微信主界面或者是聊天界面
 */
static final String WECHAT_LAUNCHER = "com.tencent.mm.ui.LauncherUI";
```
这里须要注意的是WECHAT_LAUNCHER，微信主界面以及聊天界面应该採用的FragmentActivity+Fragment这样导致假设用户进入到微信主界面则会调用AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED，导致再次进入微信聊天界面不会再调用AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED，而会调用AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED，而AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED仅仅要内容改变后都会调用，所以通常是使用AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED来作为监測事件的。所以解决问题的方式就是添加推断条件：
（1）触发AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED这个事件搜索列表界面是否有"领取红包"字样，假设没有则设置一个变量
（2）假设没有触发AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED而触发了AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED，则去推断之前设置的变量综合来推断