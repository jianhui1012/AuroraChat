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
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
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
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by admin on 2017/8/25.
 */

public class RNChatUIManager extends ViewGroupManager<ChatView> {
    private ReactApplicationContext mReactContext;
    private  Activity activity;
    private ChatView mChatView;
    private static final String REACT_CHAT_INPUT = "RCTChatUI";
    private EditExtension mEditExtension;

    private float mLastTouchY;
    private boolean mUpDirection;
    private float mOffsetLimit;
    private boolean finishing = false;
    //消息类型
    public static final int SEND_TEXT_MSG = 1;
    public static final int SEND_VOICE_MSG = 2;
    public static final int SEND_PIC_MSG = 3;
    public static final int SEND_RICHTEXT_MSG = 4;
    public static final int SEND_LOCATION_MSG = 5;
    @Override
    public String getName() {
        return REACT_CHAT_INPUT;
    }

    public RNChatUIManager(ReactApplicationContext reactContext){
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


    @Override
    public @Nullable
    Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "sendTextMsg", SEND_TEXT_MSG,
                "sendVoiceMsg", SEND_VOICE_MSG,
                "sendPicMsg", SEND_PIC_MSG,
                "sendRichTextMsg", SEND_RICHTEXT_MSG,
                "sendLocationMsg", SEND_LOCATION_MSG
        );
    }

    @Override
    public void receiveCommand(ChatView chatView, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case SEND_TEXT_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    String text = msg.hasKey("content") ? msg.getString("content") : null;
                    TextMessage textMessage = TextMessage.obtain(text);
                    textMessage.setUserInfo(new UserInfo("1001", "golike", Uri.parse("http://img.17bangtu.com/dfile?md5=99d16d4817174715ff86e3ef1e618ad5:200x200")));
                    Message message = Message.obtain("xxx", Conversation.ConversationType.PRIVATE, textMessage);
                    message.setMessageDirection(Message.MessageDirection.SEND);
                    EventBus.getDefault().post(message);
                }
                break;
            case SEND_VOICE_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                }
                break;
            case SEND_PIC_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                }
                break;
            case SEND_RICHTEXT_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                }
                break;
        }
    }
}
