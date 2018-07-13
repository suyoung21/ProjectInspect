package com.glink.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

import com.glink.App;

/**
 * @author jiangshuyang
 */
public class ResUtil {
    public ResUtil() {
    }

    public static int getColor(int resId) {
        return App.getAppContext().getResources().getColor(resId);
    }

    public static String getString(int resId) {
        return App.getAppContext().getResources().getString(resId);
    }

    public static String getString(int resId, Object... formatArgs) {
        return App.getAppContext().getResources().getString(resId, formatArgs);
    }

    public static Drawable getDrawable(int resId) {
        return App.getAppContext().getResources().getDrawable(resId);
    }

    public static int getDimensionPixelOffset(int resId) {
        return App.getAppContext().getResources().getDimensionPixelOffset(resId);
    }

    public static int getDimensionPixelSize(int resId) {
        return App.getAppContext().getResources().getDimensionPixelSize(resId);
    }

    public static Resources getResources() {
        return App.getAppContext().getResources();
    }

    public static float getDimension(int resId) {
        return App.getAppContext().getResources().getDimension(resId);
    }

    public static DisplayMetrics getDisplayMetrics() {
        return App.getAppContext().getResources().getDisplayMetrics();
    }

    public static String[] getStringArray(int resId) {
        return App.getAppContext().getResources().getStringArray(resId);
    }

    public static int[] getIntegerArray(int resId) {
        return App.getAppContext().getResources().getIntArray(resId);
    }

    public static String getResourceName(int resId) {
        return App.getAppContext().getResources().getResourceName(resId);
    }

    public static int getIdentifier(String name, String defType, String defPackage) {
        return App.getAppContext().getResources().getIdentifier(name, defType, defPackage);
    }

    public static int getStringId(String name) {
        return getIdentifier(name, "string", App.getAppContext().getPackageName());
    }

    public static int getDrawableId(String name) {
        return getIdentifier(name, "drawable", App.getAppContext().getPackageName());
    }

    public static int getResId(String name) {
        return App.getAppContext().getResources().getIdentifier(name, "id", App.getAppContext().getPackageName());
    }

}
