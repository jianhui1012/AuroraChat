package com.golike.customviews.reactmanager;

import android.app.Activity;
import android.content.Intent;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.golike.customviews.EditExtension;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by admin on 2017/8/25.
 */

public class RNChatUIManager extends SimpleViewManager<RNChatUI> implements ActivityEventListener {
    private Activity activity;
    private RNChatUI mChatView;
    private static final String REACT_CHAT_INPUT = "RCTChatUI";
    private EditExtension mEditExtension;
    public static final int SEND_TEXT_MSG = 1;
    public static final int SEND_VOICE_MSG = 2;
    public static final int SEND_PIC_MSG = 3;
    public static final int SEND_RICHTEXT_MSG = 4;
    public static final int SEND_LOCATION_MSG = 5;
    public static final int UPDATE_MSG_STATUS = 6;
    public static final int GET_HISTORY_MESSAGE = 7;
    public static final int HEADERCLICK = 8;


    @Override
    public String getName() {
        return REACT_CHAT_INPUT;
    }

    public RNChatUIManager(ReactApplicationContext reactContext) {
        reactContext.addActivityEventListener(this);
    }

    @Override
    protected RNChatUI createViewInstance(final ThemedReactContext reactContext) {
        //ReactApplicationContext设置onActivityResult回
        activity = reactContext.getCurrentActivity();//(Activity)invoke(reactContext,"getCurrentActivity");
        mChatView = new RNChatUI(activity,reactContext);
        mEditExtension = mChatView.getEditExtension();
        return mChatView;
    }

    /***
     * 初始化聊天的信息
     * @param chatInputView
     * @param curChatInfo
     */
    @ReactProp(name = "chatInfo")
    public void setChatInfo(RNChatUI chatInputView, ReadableMap curChatInfo) {
        if (chatInputView == null)
            return;
        chatInputView.setChatInfo(curChatInfo);
    }


    /***
     * set if need onRefresh
     * @param chatInputView
     * @param isOnRefresh
     */
    @ReactProp(name = "isOnRefresh")
    public void setIsOnRefresh(RNChatUI chatInputView, boolean isOnRefresh) {
        chatInputView.setHasMoreLocalMessages(isOnRefresh);
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
                "sendVoiceMsg", SEND_VOICE_MSG,
                "sendPicMsg", SEND_PIC_MSG,
                "sendRichTextMsg", SEND_RICHTEXT_MSG,
                "sendLocationMsg", SEND_LOCATION_MSG,
                "updateMsgStatus", UPDATE_MSG_STATUS,
                "getHistoryMessage", GET_HISTORY_MESSAGE
        );
    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == 102) {
            if (null != activity)
                activity.finish();
        } else {
            if (mEditExtension != null) {
                mEditExtension.onActivityPluginResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void receiveCommand(RNChatUI chatView, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case SEND_TEXT_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    chatView.sendTextMsg(msg);
                }
                break;
            case SEND_VOICE_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    chatView.sendVoiceMsg(msg);
                }
                break;
            case SEND_PIC_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    chatView.sendImageMsg(msg);
                }
                break;
            case SEND_RICHTEXT_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    chatView.sendRichText(msg);
                }
                break;
            case UPDATE_MSG_STATUS:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    chatView.updateMsgStatue(msg);
                }
                break;
            case GET_HISTORY_MESSAGE:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    chatView.sendHisttoryMsgs(msg);
                }
                break;
        }
    }


}
