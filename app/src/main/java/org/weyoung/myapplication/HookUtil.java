package org.weyoung.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookUtil {
    public static void hookStartActivity() {
        try {

            Class<?> activityManagerClazz = Class.forName("android.app.ActivityManager");
            Field activityManagerSingletonField = activityManagerClazz.getDeclaredField("IActivityManagerSingleton");
            activityManagerSingletonField.setAccessible(true);
            Object activityManagerSingleton = activityManagerSingletonField.get(null);

            Class<?> singletonClazz = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClazz.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

            final Object IActivityManager = mInstanceField.get(activityManagerSingleton);

            Object activityManagerProxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{Class.forName("android.app.IActivityManager")}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("startActivity".equals(method.getName())) {
                        int index = 0;

                        for (int i = 0; i < args.length; i++) {
                            if ("android.content.Intent".equals(args[i].getClass().getName())) {
                                index = i;
                                break;
                            }
                        }
                        Intent proxyIntent = new Intent();
                        proxyIntent.setClassName("org.weyoung.myapplication", ProxyActivity.class.getName());
                        proxyIntent.putExtra("pluginIntent", (Intent) args[index]);
                        args[index] = proxyIntent;
                    }
                    return method.invoke(IActivityManager, args);
                }
            });

            mInstanceField.set(activityManagerSingleton, activityManagerProxy);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hookActivityThread() {
        try {
            Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadField = activityThreadClazz.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadField.setAccessible(true);
            Object activityThread = currentActivityThreadField.get(null);

            Field handlerField = activityThreadClazz.getDeclaredField("mH");
            handlerField.setAccessible(true);
            Object handler = handlerField.get(activityThread);

            Class<?> handlerClazz = Class.forName("android.os.Handler");
            Field callbackField = handlerClazz.getDeclaredField("mCallback");
            callbackField.setAccessible(true);
            callbackField.set(handler, new Handler.Callback() {
                @Override
                public boolean handleMessage(@NonNull Message msg) {
                    if (msg.what == 100) {
                        try {
                            Field intentField = msg.obj.getClass().getDeclaredField("intent");
                            intentField.setAccessible(true);
                            Intent proxyIntent = (Intent) intentField.get(msg.obj);
                            Intent pluginIntent = proxyIntent.getParcelableExtra("pluginIntent");
                            if (pluginIntent != null) {
                                intentField.set(msg.obj, pluginIntent);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
