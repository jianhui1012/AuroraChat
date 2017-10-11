package com.golike.customviews.reactmanager;

import android.app.Activity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.golike.customviews.ChatView;
import com.golike.customviews.DefaultExtensionModule;
import com.golike.customviews.EmotionExtension;
import com.golike.customviews.emoticon.IEmojiItemClickListener;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by admin on 2017/8/25.
 */

public class RNEmotionViewManager extends SimpleViewManager<EmotionExtension> {
    private ReactContext mReactContext;
    private EmotionExtension mEmotionExtension;
    private static final String REACT_CHAT_INPUT = "RCTEmotionView";

    public static final int SEND_TEXT_MSG = 1;
    public static final int SEND_VOICE_MSG = 2;
    private Activity mActivity;


    @Override
    public String getName() {
        return REACT_CHAT_INPUT;
    }

    public RNEmotionViewManager() {

    }

    IEmojiItemClickListener iEmojiItemClickListener = new IEmojiItemClickListener() {
        public void onEmojiClick(String emoji) {
            //Toast.makeText(mActivity, "onEmojiClick", Toast.LENGTH_LONG).show();
            RNEmotionViewManager.this.dispatchInsertCommand(emoji);
        }

        public void onDeleteClick() {
            if (mEmotionExtension != null)
                RNEmotionViewManager.this.mEmotionExtension.dispatchKeyEvent(new KeyEvent(0, 67));
            RNEmotionViewManager.this.dispatchDeleteCommand();
        }
    };

    @Override
    protected EmotionExtension createViewInstance(final ThemedReactContext reactContext) {
        this.mReactContext = reactContext;
        this.mActivity = reactContext.getCurrentActivity();
        mEmotionExtension = new EmotionExtension(mActivity, iEmojiItemClickListener);
        return mEmotionExtension;
    }


    public void dispatchInsertCommand(String emoji) {
        ReactContext reactContext = mReactContext;
        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            WritableMap body = new WritableNativeMap();
            body.putString("cmd", "insert");
            body.putString("emoji", emoji);
            sendEvent(this.mReactContext, "RNEMCMD", body);
        }
    }

    public void dispatchDeleteCommand() {
        ReactContext reactContext = mReactContext;
        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            WritableMap body = new WritableNativeMap();
            body.putString("cmd", "delete");
            sendEvent(this.mReactContext, "RNEMCMD", body);
        }
    }

    /***
     * send onChatUIEvent from native modules to js bundle
     * @param cmd
     */
    private void dispatchEvent(String cmd) {
        WritableMap data = Arguments.createMap();
        data.putDouble("target", mEmotionExtension.getId());
        data.putString("cmd", cmd);
        Event event = new RNChatUIEvent(mEmotionExtension.getId(), data);
        EventDispatcher eventDispatcher =
                mReactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
        eventDispatcher.dispatchEvent(event);
    }

    /***
     * send event from native module to js bundle
     * @param reactContext
     * @param eventName
     * @param params
     */
    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }


    /***
     * set if need onRefresh
     * @param chatInputView
     * @param isOnRefresh
     */
    @ReactProp(name = "isOnRefresh")
    public void setIsOnRefresh(ChatView chatInputView, boolean isOnRefresh) {
        if (isOnRefresh)
            chatInputView.mHasMoreLocalMessages = false;
    }


    @Override
    public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put(
                        "topChatUIEvent",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of(
                                        "bubbled", "onChatUIEvent", "captured", "onChatUIEventCapture")))
                .build();
    }

    @Override
    public
    @Nullable
    Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "sendTextMsg", SEND_TEXT_MSG,
                "sendVoiceMsg", SEND_VOICE_MSG
        );
    }

    @Override
    public void receiveCommand(EmotionExtension emotionExtension, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case SEND_TEXT_MSG:
                break;
        }
    }
}
