package com.glink;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiangshuyang
 */
public class App extends MultiDexApplication {
    private static App instance;
    //沙盒（内网）开关
    public boolean isSandbox = true;
    private List<Activity> mActivityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    public static Context getAppContext() {
        if (instance != null) {
            return instance.getApplicationContext();
        }
        throw new RuntimeException("APP instance is null");
    }

    /**
     * 关闭所有activity
     */
    public void finishAllActivity() {
        List<Activity> list = new ArrayList<Activity>(5);
        list.addAll(mActivityList);
        int size = list.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity item = list.get(i);
            item.finish();
        }
        list.clear();
    }

    /**
     * 关闭activity,弹出栈的个数
     */
    public void finishActivity(int stack) {
        List<Activity> list = new ArrayList<Activity>(5);
        list.addAll(mActivityList);
        int size = list.size();
        for (int i = size - 1; i >= size - stack; i--) {
            Activity item = list.get(i);
            item.finish();
        }
        list.clear();
    }

    /**
     * 获取当前activity
     * @return
     */
    public Activity getCurrentActivity() {
        return mActivityList.get(mActivityList.size() -1);
    }
    public void createActivity(Activity activity) {
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);
        }
    }

    public void destroyActivity(Activity activity) {
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);
        }
    }

    /**
     * 清空所有的activity回到home界面
     */
    public void goHome() {
        List<Activity> list = new ArrayList<Activity>(5);
        int size = mActivityList.size();
        for (int i = size - 1; i > 0; i--) {
            Activity item = mActivityList.get(i);
            item.finish();
            list.add(item);
        }
        mActivityList.removeAll(list);
        list.clear();
    }

    /**
     * 是否处于栈顶
     *
     * @param activity
     * @return
     */
    public boolean isStackTop(Activity activity) {
        int position = mActivityList.size() - 1;
        if (mActivityList.get(position).equals(activity)) {
            return true;
        }
        return false;
    }
}
