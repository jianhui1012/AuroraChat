package com.golike.customviews;

import android.content.Context;
import android.content.ContextWrapper;
import android.location.LocationProvider;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.golike.customviews.common.WeakValueHashMap;
import com.golike.customviews.model.Conversation.*;
import com.golike.customviews.model.MessageContent;
import com.golike.customviews.model.ProviderTag;
import com.golike.customviews.model.UserInfo;
import com.golike.customviews.widget.provider.IContainerItemProvider.*;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2017/8/16.
 */
public class RongContext extends ContextWrapper {
    private static final String TAG = "RongContext";
    private static final int NOTIFICATION_CACHE_MAX_COUNT = 64;
    private static RongContext sContext;
    private EventBus mBus = EventBus.getDefault();
    private ExecutorService executorService;

    private Map<Class<? extends MessageContent>, MessageProvider> mTemplateMap = new HashMap();
    private Map<Class<? extends MessageContent>, MessageProvider> mWeakTemplateMap = new WeakValueHashMap();
    private Map<Class<? extends MessageContent>, ProviderTag> mProviderMap = new HashMap();
    private LocationProvider mLocationProvider;
    private List<String> mCurrentConversationList = new ArrayList();
    Handler mHandler = new Handler(this.getMainLooper());
    private UserInfo mCurrentUserInfo;
    private boolean isUserInfoAttached;
    private boolean isShowUnreadMessageState;
    private boolean isShowNewMessageState;

    public static void init(Context context) {
        if(sContext == null) {
            sContext = new RongContext(context);
        }

    }

    public static RongContext getInstance() {
        return sContext;
    }

    protected RongContext(Context base) {
        super(base);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public EventBus getEventBus() {
        return this.mBus;
    }

    public void registerMessageTemplate(MessageProvider provider) {
        ProviderTag tag =  provider.getClass().getAnnotation(ProviderTag.class);
        if(tag == null) {
            throw new RuntimeException("ProviderTag not def MessageContent type");
        } else {
            this.mTemplateMap.put(tag.messageContent(), provider);
            this.mProviderMap.put(tag.messageContent(), tag);
        }
    }

    public MessageProvider getMessageTemplate(Class<? extends MessageContent> type) {
        MessageProvider provider =  this.mWeakTemplateMap.get(type);
        if(provider == null) {
            try {
                if(this.mTemplateMap != null && this.mTemplateMap.get(type) != null) {
                    provider = (MessageProvider)(this.mTemplateMap.get(type)).clone();
                    this.mWeakTemplateMap.put(type, provider);
                } else {
                    Log.e("RongContext", "The template of message can\'t be null. type :" + type);
                }
            } catch (CloneNotSupportedException var4) {
                var4.printStackTrace();
            }
        }

        return provider;
    }

    public ProviderTag getMessageProviderTag(Class<? extends MessageContent> type) {
        return  this.mProviderMap.get(type);
    }


    public void executorBackground(Runnable runnable) {
        if(runnable != null) {
            this.executorService.execute(runnable);
        }
    }

    public LocationProvider getLocationProvider() {
        return this.mLocationProvider;
    }

    public void setLocationProvider(LocationProvider locationProvider) {
        this.mLocationProvider = locationProvider;
    }

    public UserInfo getCurrentUserInfo() {
        return this.mCurrentUserInfo != null?this.mCurrentUserInfo:null;
    }

    public String getToken() {
        return this.getSharedPreferences("RongKitConfig", 0).getString("token", "");
    }

    public void setUserInfoAttachedState(boolean state) {
        this.isUserInfoAttached = state;
    }

    public boolean getUserInfoAttachedState() {
        return this.isUserInfoAttached;
    }

    public void showUnreadMessageIcon(boolean state) {
        this.isShowUnreadMessageState = state;
    }

    public void showNewMessageIcon(boolean state) {
        this.isShowNewMessageState = state;
    }

    public boolean getUnreadMessageState() {
        return this.isShowUnreadMessageState;
    }

    public boolean getNewMessageState() {
        return this.isShowNewMessageState;
    }

}