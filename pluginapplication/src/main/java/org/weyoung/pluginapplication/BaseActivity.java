package org.weyoung.pluginapplication;


import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextThemeWrapper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

abstract class BaseActivity extends AppCompatActivity {
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPathMethod = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.invoke(assetManager, "/mnt/sdcard/pluginapplication-debug.apk");
            Resources resources = new Resources(assetManager, getResources().getDisplayMetrics(), getResources().getConfiguration());

            mContext = new ContextThemeWrapper(getBaseContext(), 0);
            Field resourcesField = mContext.getClass().getDeclaredField("mResources");
            resourcesField.setAccessible(true);
            resourcesField.set(mContext, resources);

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
    }
}
