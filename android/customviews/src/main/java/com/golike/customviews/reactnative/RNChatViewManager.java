package com.golike.customviews.reactnative;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.golike.customviews.ChatView;
import com.golike.customviews.EditExtension;
import com.golike.customviews.IExtensionClickListener;
import com.golike.customviews.model.Conversation;
import com.golike.customviews.plugin.IPluginModule;

import java.util.List;

/**
 * Created by admin on 2017/8/25.
 */

public class RNChatViewManager extends ViewGroupManager<ChatView> {
    private  Activity activity;
    private ChatView mChatView;
    private static final String REACT_CHAT_INPUT = "RCTChatView";
    private EditExtension mEditExtension;

    @Override
    public String getName() {
        return REACT_CHAT_INPUT;
    }

    @Override
    protected ChatView createViewInstance(ThemedReactContext reactContext) {
        activity=reactContext.getCurrentActivity();
        mChatView=new ChatView(activity);
        mEditExtension=mChatView.getEditExtension();
        mEditExtension.setConversation(Conversation.ConversationType.PRIVATE,"xxxx");
        mEditExtension.setExtensionClickListener(new IExtensionClickListener() {
            @Override
            public void onSendToggleClick(View var1, String var2) {

            }

            @Override
            public void onImageResult(List<Uri> var1, boolean var2) {

            }

            @Override
            public void onLocationResult(double var1, double var3, String var5, Uri var6) {

            }

            @Override
            public void onSwitchToggleClick(View var1, ViewGroup var2) {

            }

            @Override
            public void onVoiceInputToggleTouch(View var1, MotionEvent var2) {

            }

            @Override
            public void onEmoticonToggleClick(View var1, ViewGroup var2) {

            }

            @Override
            public void onPluginToggleClick(View var1, ViewGroup var2) {

            }

            @Override
            public void onMenuClick(int var1, int var2) {

            }

            @Override
            public void onEditTextClick(EditText var1) {

            }

            @Override
            public boolean onKey(View var1, int var2, KeyEvent var3) {
                return false;
            }

            @Override
            public void onExtensionCollapsed() {

            }

            @Override
            public void onExtensionExpanded(int var1) {

            }

            @Override
            public void onPluginClicked(IPluginModule var1, int var2) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEditExtension.setActivity(activity);
        reactContext.addActivityEventListener(mActivityEventListener);
        return mChatView;
    }

    @ReactProp(name = "menuContainerHeight")
    public void setMenuContainerHeight(ChatView chatInputView, int height) {
        Log.d("ReactChatInputManager", "Setting menu container height: " + height);
        chatInputView.setMinimumHeight(height);
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
            if(requestCode == 102) {
                activity.finish();
            } else {
                mEditExtension.onActivityPluginResult(requestCode, resultCode, intent);
            }
        }
    };


}
