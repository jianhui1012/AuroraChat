package com.golike.customviews;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.golike.customviews.emoticon.AndroidEmoji;
import com.golike.customviews.utilities.RongUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2017/8/9.
 */

public class EditExtensionManager {
    private static final String TAG = "RongExtensionManager";
    private static String mAppKey;
    private static List<IExtensionModule> mExtModules;
    private static final String DEFAULT_REDPACKET = "com.jrmf360.rylib.modules.JrmfExtensionModule";
    private static final String DEFAULT_BQMM = "com.melink.bqmmplugin.rc.BQMMExtensionModule";

    private EditExtensionManager() {
        if(mExtModules != null) {
            Class e;
            Constructor constructor;
            IExtensionModule bqmm;
            try {
                e = Class.forName("com.jrmf360.rylib.modules.JrmfExtensionModule");
                constructor = e.getConstructor(new Class[0]);
                bqmm = (IExtensionModule)constructor.newInstance(new Object[0]);
                Log.i("RongExtensionManager", "add module " + bqmm.getClass().getSimpleName());
                mExtModules.add(bqmm);
                bqmm.onInit(mAppKey);
            } catch (Exception var5) {
                Log.i("RongExtensionManager", "Can\'t find com.jrmf360.rylib.modules.JrmfExtensionModule");
            }

            try {
                e = Class.forName("com.melink.bqmmplugin.rc.BQMMExtensionModule");
                constructor = e.getConstructor(new Class[0]);
                bqmm = (IExtensionModule)constructor.newInstance(new Object[0]);
                Log.i("RongExtensionManager", "add module " + bqmm.getClass().getSimpleName());
                mExtModules.add(bqmm);
                bqmm.onInit(mAppKey);
            } catch (Exception var4) {
                Log.i("RongExtensionManager", "Can\'t find com.melink.bqmmplugin.rc.BQMMExtensionModule");
            }
        }

    }

    public static EditExtensionManager getInstance() {
        return EditExtensionManager.SingletonHolder.sInstance;
    }

    static void init(Context context, String appKey) {
        Log.d("RongExtensionManager", "init");
        AndroidEmoji.init(context);
        RongUtils.init(context);
        mAppKey = appKey;
        mExtModules = new ArrayList();
    }

    public void registerExtensionModule(IExtensionModule extensionModule) {
        if(mExtModules == null) {
            Log.e("RongExtensionManager", "Not init in the main process.");
        } else if(extensionModule != null && !mExtModules.contains(extensionModule)) {
            Log.i("RongExtensionManager", "registerExtensionModule " + extensionModule.getClass().getSimpleName());
            if(mExtModules.size() <= 0 || !((IExtensionModule)mExtModules.get(0)).getClass().getCanonicalName().equals("com.jrmf360.rylib.modules.JrmfExtensionModule") && !((IExtensionModule)mExtModules.get(0)).getClass().getCanonicalName().equals("com.melink.bqmmplugin.rc.BQMMExtensionModule")) {
                mExtModules.add(extensionModule);
            } else {
                mExtModules.add(0, extensionModule);
            }

            extensionModule.onInit(mAppKey);
        } else {
            Log.e("RongExtensionManager", "Illegal extensionModule.");
        }
    }

    public void unregisterExtensionModule(IExtensionModule extensionModule) {
        if(mExtModules == null) {
            Log.e("RongExtensionManager", "Not init in the main process.");
        } else if(extensionModule != null && mExtModules.contains(extensionModule)) {
            Log.i("RongExtensionManager", "unregisterExtensionModule " + extensionModule.getClass().getSimpleName());
            mExtModules.remove(extensionModule);
        } else {
            Log.e("RongExtensionManager", "Illegal extensionModule.");
        }
    }

    public List<IExtensionModule> getExtensionModules() {
        return mExtModules;
    }

    void connect(String token) {
        Iterator i$ = mExtModules.iterator();

        while(i$.hasNext()) {
            IExtensionModule extensionModule = (IExtensionModule)i$.next();
            extensionModule.onConnect(token);
        }

    }

    void disconnect() {
        Iterator i$ = mExtModules.iterator();

        while(i$.hasNext()) {
            IExtensionModule extensionModule = (IExtensionModule)i$.next();
            extensionModule.onDisconnect();
        }

    }

    void onReceivedMessage(Message message) {
        Iterator i$ = mExtModules.iterator();

        while(i$.hasNext()) {
            IExtensionModule extensionModule = (IExtensionModule)i$.next();
            extensionModule.onReceivedMessage(message);
        }

    }

    private static class SingletonHolder {
        static EditExtensionManager sInstance = new EditExtensionManager();

        private SingletonHolder() {
        }
    }
}
