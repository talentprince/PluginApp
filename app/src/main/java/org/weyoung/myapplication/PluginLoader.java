package org.weyoung.myapplication;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dalvik.system.DexClassLoader;

public class PluginLoader {
    public static final String PATH = "/mnt/sdcard/pluginapplication-debug.apk";

    public static void load(Context context) {
        try {
            Class<?> baseDexClazz = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = baseDexClazz.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object pathList = pathListField.get(context.getClassLoader());
            Class<?> dexPathListClazz = Class.forName("dalvik.system.DexPathList");
            Field dexElementsField = dexPathListClazz.getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object[] hostElements = (Object[]) dexElementsField.get(pathList);

            DexClassLoader pluginClassLoader = new DexClassLoader(PATH, context.getDataDir().getAbsolutePath(), "", context.getClassLoader());
            Object pluginPathList = pathListField.get(pluginClassLoader);
            Object[] pluginElements = (Object[]) dexElementsField.get(pluginPathList);

            Object[] newElements = (Object[]) Array.newInstance(Class.forName("dalvik.system.DexPathList$Element"), hostElements.length + pluginElements.length);
            System.arraycopy(hostElements, 0, newElements, 0, hostElements.length);
            System.arraycopy(pluginElements, 0, newElements, hostElements.length, pluginElements.length);


            dexElementsField.set(pathList, newElements);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void callPlugin() {
        try {
            Class<?> pluginTestClazz = Class.forName("org.weyoung.pluginapplication.Test");
            Method testMethod = pluginTestClazz.getDeclaredMethod("test");
            testMethod.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
