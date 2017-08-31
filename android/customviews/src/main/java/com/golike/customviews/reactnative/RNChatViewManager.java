package com.golike.customviews.reactnative;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.golike.customviews.ChatView;
import com.golike.customviews.EditExtension;
import com.golike.customviews.IExtensionClickListener;
import com.golike.customviews.R;
import com.golike.customviews.manager.AudioPlayManager;
import com.golike.customviews.manager.AudioRecordManager;
import com.golike.customviews.model.Conversation;
import com.golike.customviews.model.ImageMessage;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.TextMessage;
import com.golike.customviews.model.UserInfo;
import com.golike.customviews.plugin.IPluginModule;
import com.golike.customviews.utilities.PermissionCheckUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2017/8/25.
 */

public class RNChatViewManager extends ViewGroupManager<ChatView> {
    private ReactApplicationContext mReactContext;
    private  Activity activity;
    private ChatView mChatView;
    private static final String REACT_CHAT_INPUT = "RCTChatView";
    private EditExtension mEditExtension;

    private float mLastTouchY;
    private boolean mUpDirection;
    private float mOffsetLimit;
    private boolean finishing = false;
    @Override
    public String getName() {
        return REACT_CHAT_INPUT;
    }

    public  RNChatViewManager(ReactApplicationContext reactContext){
        this.mReactContext=reactContext;
    }

    @Override
    protected ChatView createViewInstance(ThemedReactContext reactContext) {
        //ReactApplicationContext设置onActivityResult回调
        this.mReactContext.addActivityEventListener(mActivityEventListener);
        activity=reactContext.getCurrentActivity();
        mChatView=new ChatView(activity);
        mEditExtension=mChatView.getEditExtension();
        mEditExtension.setConversation(Conversation.ConversationType.PRIVATE,"xxxx");
        mEditExtension.setExtensionClickListener(new IExtensionClickListener() {
            @Override
            public void onSendToggleClick(View view, String text) {
                if(!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text.trim())) {
                    TextMessage textMessage = TextMessage.obtain(text);
                    textMessage.setUserInfo(new UserInfo("1001", "golike", Uri.parse("http://img.17bangtu.com/dfile?md5=99d16d4817174715ff86e3ef1e618ad5:200x200")));
                    Message message = Message.obtain("xxx", Conversation.ConversationType.PRIVATE, textMessage);
                    message.setMessageDirection(Message.MessageDirection.SEND);
                    EventBus.getDefault().post(message);
                } else {
                    Log.e("ConversationFragment", "text content must not be null");
                }
            }

            @Override
            public void onImageResult(List<Uri> imageList, boolean isFull) {
                Iterator i$ = imageList.iterator();

                while(i$.hasNext()) {
                    Uri image = (Uri)i$.next();
                    ImageMessage content = ImageMessage.obtain(image, image, isFull);
                    content.setUserInfo(new UserInfo("1001", "golike", Uri.parse("http://img.17bangtu.com/dfile?md5=99d16d4817174715ff86e3ef1e618ad5:200x200")));
                    Message message = Message.obtain("xxx", Conversation.ConversationType.PRIVATE, content);
                    //message.setSentStatus(SentStatus.SENDING);
                    message.setMessageDirection(Message.MessageDirection.SEND);
                    EventBus.getDefault().post(message);
                }
            }

            @Override
            public void onLocationResult(double var1, double var3, String var5, Uri var6) {

            }

            @Override
            public void onSwitchToggleClick(View v, ViewGroup var2) {

            }

            @Override
            public void onVoiceInputToggleTouch(View v, MotionEvent event) {
                String[] permissions = new String[]{"android.permission.RECORD_AUDIO"};
                if(!PermissionCheckUtil.checkPermissions(activity, permissions)) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        PermissionCheckUtil.requestPermissions(activity, permissions, 100);
                    }

                } else {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        AudioPlayManager.getInstance().stopPlay();
                        AudioRecordManager.getInstance().startRecord(v.getRootView(),  Conversation.ConversationType.PRIVATE, "xxx");
                        mLastTouchY = event.getY();
                        mUpDirection = false;
                        ((Button)v).setText(R.string.rc_audio_input_hover);
                    } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                        if(mLastTouchY - event.getY() > mOffsetLimit && !mUpDirection) {
                            AudioRecordManager.getInstance().willCancelRecord();
                            mUpDirection = true;
                            ((Button)v).setText(R.string.rc_audio_input);
                        } else if(event.getY() - mLastTouchY > - mOffsetLimit && mUpDirection) {
                            AudioRecordManager.getInstance().continueRecord();
                            mUpDirection = false;
                            ((Button)v).setText(R.string.rc_audio_input_hover);
                        }
                    } else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        AudioRecordManager.getInstance().stopRecord();
                        ((Button)v).setText(R.string.rc_audio_input);
                    }
                }
            }

            @Override
            public void onEmoticonToggleClick(View v, ViewGroup viewGroup) {

            }

            @Override
            public void onPluginToggleClick(View v, ViewGroup viewGroup) {

            }

            @Override
            public void onMenuClick(int var1, int var2) {

            }

            @Override
            public void onEditTextClick(EditText editText) {

            }

            @Override
            public boolean onKey(View view, int var2, KeyEvent keyEvent) {
                return false;
            }

            @Override
            public void onExtensionCollapsed() {

            }

            @Override
            public void onExtensionExpanded(int var1) {

            }

            @Override
            public void onPluginClicked(IPluginModule pluginModule, int var2) {

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
