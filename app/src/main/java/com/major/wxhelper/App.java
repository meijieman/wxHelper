package com.major.wxhelper;

import android.app.Application;

import com.major.base.log.LogUtil;

/**
 * Desc: TODO
 * <p>
 * Author: meijie
 * PackageName: com.major.wxhelper
 * ProjectName: wxHelper
 * Date: 2019/4/11 10:03
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init(getPackageName(), "tag_wx", true, false);
    }
}
