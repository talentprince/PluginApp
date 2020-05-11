package org.weyoung.myapplication;

import android.content.Intent;

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
}
