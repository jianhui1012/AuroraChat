package com.golike.customviews.reactmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
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
import com.golike.customviews.AutoRefreshListView;
import com.golike.customviews.ChatContext;
import com.golike.customviews.ChatView;
import com.golike.customviews.EditExtension;
import com.golike.customviews.IExtensionClickListener;
import com.golike.customviews.R;
import com.golike.customviews.manager.AudioPlayManager;
import com.golike.customviews.manager.AudioRecordManager;
import com.golike.customviews.model.Conversation;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.model.Event.ReadReceiptEvent;
import com.golike.customviews.model.ImageMessage;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.MessageContent;
import com.golike.customviews.model.ReadReceiptMessage;
import com.golike.customviews.model.TextMessage;
import com.golike.customviews.model.UIMessage;
import com.golike.customviews.model.UserInfo;
import com.golike.customviews.model.VoiceMessage;
import com.golike.customviews.plugin.IPluginModule;
import com.golike.customviews.utilities.FileUtils;
import com.golike.customviews.utilities.PermissionCheckUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Created by admin on 2017/8/25.
 */

public class RNChatUIManager extends SimpleViewManager<ChatView> implements ActivityEventListener {
    private ReactContext mReactContext;
    private Activity activity;
    private ChatView mChatView;
    private static final String REACT_CHAT_INPUT = "RCTChatUI";
    private EditExtension mEditExtension;

    private float mLastTouchY;
    private boolean mUpDirection;
    private float mOffsetLimit;
    private boolean finishing = false;

    public static final int SEND_TEXT_MSG = 1;
    public static final int SEND_VOICE_MSG = 2;
    public static final int SEND_PIC_MSG = 3;
    public static final int SEND_RICHTEXT_MSG = 4;
    public static final int SEND_LOCATION_MSG = 5;
    public static final int UPDATE_MSG_STATUS = 6;
    public static final int GET_HISTORY_MESSAGE = 7;
    public static final int HEADERCLICK = 8;
    private UserInfo curUserInfo = null;
    private String mTargetId;
    private ConversationType mConversationType;
    private AutoRefreshListView mChatList;
    private String chatType;


    @Override
    public String getName() {
        return REACT_CHAT_INPUT;
    }

    public RNChatUIManager(ReactApplicationContext reactContext) {
        reactContext.addActivityEventListener(this);
    }

    @Override
    protected ChatView createViewInstance(final ThemedReactContext reactContext) {
        this.mReactContext = reactContext;
        //ReactApplicationContext设置onActivityResult回调
        activity = reactContext.getCurrentActivity();
        mChatView = new ChatView(activity);
        mChatList = mChatView.getChatList();
        mEditExtension = mChatView.getEditExtension();
        //设置下拉刷新事件
        this.mChatList.setOnRefreshListener(new AutoRefreshListView.OnRefreshListener() {
            public void onRefreshFromStart() {
                if (mChatView.mHasMoreLocalMessages) {
                    dispatchEvent("OnRefresh");
                } else {
                    mChatList.onRefreshComplete(10, 10, false);
                }
            }

            public void onRefreshFromEnd() {
                Toast.makeText(reactContext, "onRefreshFromEnd", Toast.LENGTH_SHORT).show();
            }
        });
        this.mChatList.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE && mChatList.getCount() - mChatList.getHeaderViewsCount() == 0) {
                    if (mChatView.mHasMoreLocalMessages) {
                        dispatchEvent("OnRefresh");
                    } else if (mChatList.getRefreshState() != AutoRefreshListView.State.REFRESHING) {
                        //ChatView.this.getHistoryMessage(ChatView.this.mConversationType, ChatView.this.mTargetId, 30, 1);
                    }
                    return true;
                } else {
                    if (event.getAction() == MotionEvent.ACTION_UP && mEditExtension != null && mEditExtension.isExtensionExpanded()) {
                        mEditExtension.collapseExtension();
                    }
                    return false;
                }
            }
        });
        mEditExtension.setExtensionClickListener(new IExtensionClickListener() {
            @Override
            public void onSendToggleClick(View view, String text) {
                if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text.trim())) {
                    TextMessage textMessage = TextMessage.obtain(text);
                    //ListView更新文本消息
                    textMessage.setUserInfo(curUserInfo);
                    Message message = sendLocalMsg(textMessage);
                    //发送文本消息给JS
                    uploadtMsg(reactContext, message);
                } else {
                    Log.e("ConversationFragment", "text content must not be null");
                }
            }

            @Override
            public void onImageResult(List<Uri> imageList, boolean isFull) {
                Iterator i$ = imageList.iterator();
                while (i$.hasNext()) {
                    Uri image = (Uri) i$.next();
                    //ListView更新图片消息
                    ImageMessage content = ImageMessage.obtain(image, image, isFull);
                    content.setUserInfo(curUserInfo);
                    Message message = sendLocalMsg(content);
                    //发送图片消息给JS
                    uploadtMsg(reactContext, message);
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
                if (!PermissionCheckUtil.checkPermissions(activity, permissions)) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        PermissionCheckUtil.requestPermissions(activity, permissions, 100);
                    }

                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        AudioPlayManager.getInstance().stopPlay();
                        AudioRecordManager.getInstance().startRecord(v.getRootView(), ConversationType.PRIVATE, "xxx");
                        mLastTouchY = event.getY();
                        mUpDirection = false;
                        ((Button) v).setText(R.string.rc_audio_input_hover);
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        if (mLastTouchY - event.getY() > mOffsetLimit && !mUpDirection) {
                            AudioRecordManager.getInstance().willCancelRecord();
                            mUpDirection = true;
                            ((Button) v).setText(R.string.rc_audio_input);
                        } else if (event.getY() - mLastTouchY > -mOffsetLimit && mUpDirection) {
                            AudioRecordManager.getInstance().continueRecord();
                            mUpDirection = false;
                            ((Button) v).setText(R.string.rc_audio_input_hover);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        AudioRecordManager.getInstance().stopRecord();

                        ((Button) v).setText(R.string.rc_audio_input);
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
        //设置语音消息回调
        AudioRecordManager.getInstance().setOnVoiceChange(new AudioRecordManager.OnVoiceChangeListtener() {
            @Override
            public void sendVoiceMessage(VoiceMessage voiceMessage) {
                if (voiceMessage == null)
                    return;
                voiceMessage.setUserInfo(curUserInfo);
                Message message = sendLocalMsg(voiceMessage);
                //发送图片消息给JS
                uploadtMsg(reactContext, message);
            }
        });
        ChatContext.getInstance().setConversationBehaviorListener(new ChatContext.ConversationBehaviorListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                Toast.makeText(context,"onUserPortraitClick:"+userInfo.getUserId(),Toast.LENGTH_SHORT).show();
                //dispatchUserCommand(userInfo.getUserId());
                return false;
            }

            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                Toast.makeText(context,"onUserPortraitLongClick:"+userInfo.getUserId(),Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onMessageClick(Context context, View view, Message message) {
                return false;
            }

            @Override
            public boolean onMessageLinkClick(Context context, String st) {
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, Message message) {
                return false;
            }
        });
        mEditExtension.setActivity(activity);
        return mChatView;
    }


    /***
     * send msg to ui thread by the EventBus framework
     * @param messageContent
     * @return the msg of need send
     */
    private Message sendLocalMsg(MessageContent messageContent) {
        Message message = Message.obtain(mTargetId, mConversationType, messageContent);
        message.setMessageDirection(Message.MessageDirection.SEND);
        message.setUId(genMsguid());
        message.setSenderUserId(mTargetId);
        message.setSentTime(Calendar.getInstance().getTimeInMillis());
        message.setTargetId(mTargetId);
        EventBus.getDefault().post(message);
        return message;
    }

    /***
     * send userInfoIcon msg that have been click
     * @param userid
     */
    public void dispatchUserCommand(String userid) {
        dispatchUrlCommand("default", "user://" + userid);
    }

    /***
     * deal with the url of textMsg and richTextMsg
     * @param type  the type of msg
     * @param url   the url of msg
     */
    public void dispatchUrlCommand(String type, String url) {
        ReactContext reactContext = mReactContext;
        if (reactContext != null && reactContext.hasActiveCatalystInstance()) {
            WritableMap body = new WritableNativeMap();
            body.putString("cmd", "click");
            body.putString("type", type);
            body.putString("url", url);
            sendEvent(this.mReactContext,"RNIMUserCmd",body);
        }
    }

    /***
     * send onChatUIEvent from native modules to js bundle
     * @param cmd
     */
    private void dispatchEvent(String cmd) {
        WritableMap data = Arguments.createMap();
        data.putDouble("target", mChatView.getId());
        data.putString("cmd", cmd);
        Event event = new RNChatUIEvent(mChatView.getId(), data);
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
     * send all type msg from android to js
     * @param reactContext gloabl context
     * @param msg  the base of all msg
     */
    private synchronized void uploadtMsg(ReactContext reactContext, Message msg) {
        if (msg == null)
            return;
        //init data
        MessageContent messageContent = msg.getContent();
        UIMessage uiMsg = UIMessage.obtain(msg);
        WritableMap result = null;
        if (messageContent instanceof TextMessage) {
            result = new WritableNativeMap();
            result.putString("chattype", this.chatType);
            result.putString("type", "text");
            result.putString("msguid", uiMsg.getUId());
            result.putString("content", uiMsg.getTextMessageContent().toString());
            result.putString("userid", uiMsg.getSenderUserId());
        } else if (messageContent instanceof ImageMessage) {
            result = new WritableNativeMap();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), ((ImageMessage) messageContent).getThumUri());
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.putString("chattype", this.chatType);
            result.putString("type", "image");
            result.putString("msguid", uiMsg.getUId());
            result.putMap("content", savePicToTemp(image));
            result.putString("userid", uiMsg.getSenderUserId());
        } else if (messageContent instanceof VoiceMessage) {
            result = new WritableNativeMap();
            try {
                String base64 = encodeBase64File(((VoiceMessage) messageContent).getUri().getPath());
                result.putString("content", base64);
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.putString("chattype", this.chatType);
            result.putString("type", "voice");
            result.putString("msguid", uiMsg.getUId());
            result.putInt("duration", ((VoiceMessage) messageContent).getDuration());
            result.putString("userid", uiMsg.getSenderUserId());
        }
        sendEvent(reactContext, "uploadMsg", result);
    }

    /***
     * received message from javascript then send ui thread to draw the msg.
     * @param msg
     */
    private Message receivedMessageFromJS(ReadableMap msg) {
        //发送者相关信息
        ReadableMap sendUserInfo = msg.hasKey("senduserinfo") ? msg.getMap("senduserinfo") : null;
        String senduserid = "", sendname = "", portraitUri = "";
        if (sendUserInfo != null) {
            //senduserid--发送者的id sendname--发送者的昵称 portraitUri--头像地址
            senduserid = sendUserInfo.hasKey("senduserid") ? sendUserInfo.getString("senduserid") : "";
            sendname = sendUserInfo.hasKey("sendname") ? sendUserInfo.getString("sendname") : "";
            portraitUri = sendUserInfo.hasKey("portraitUri") ? sendUserInfo.getString("portraitUri") : "";
        }
        //消息ID
        int msgid = msg.hasKey("msgid") ? msg.getInt("msgid") : 0;
        //消息唯一ID
        String uid = msg.hasKey("uid") ? msg.getString("uid") : "";
        //本条消息是否属于自己
        boolean isOwn = msg.hasKey("own") ? msg.getBoolean("own") : true;
        //消息内容
        String content = msg.hasKey("content") ? msg.getString("content") : "";
        //发送的时间
        String sentTime = msg.hasKey("ts") ? msg.getString("ts") : "" + Calendar.getInstance().getTimeInMillis();
        String msgtype = msg.hasKey("type") ? msg.getString("type") : "text";
        MessageContent messageContent = null;
        switch (msgtype) {
            case "text":
                //构造文本消息体
                messageContent = TextMessage.obtain(content);
                break;
            case "image":
                //构造图片消息体
                messageContent = ImageMessage.obtain(Uri.parse(content), Uri.parse(content));
                break;
            case "voice":
                int duration = msg.hasKey("duration") ? msg.getInt("duration") : 0;
                try {
                    String dir = FileUtils.getMediaDownloadDir(mReactContext);
                    String path = dir + uid + ".voice";
                    decoderBase64File(content, path);
                    //构造语音消息体
                    messageContent = VoiceMessage.obtain(Uri.parse(path), duration);
                    ((VoiceMessage) messageContent).setBase64(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        //构造传送的消息包
        Message message = Message.obtain(this.mTargetId, this.mConversationType, messageContent);
        //是否是自己发的
        if (isOwn) {
            messageContent.setUserInfo(curUserInfo);
            message.setSenderUserId(curUserInfo.getUserId());
            message.setMessageDirection(Message.MessageDirection.SEND);
        } else {
            messageContent.setUserInfo(new UserInfo(senduserid, sendname, Uri.parse(portraitUri)));
            message.setSenderUserId(senduserid);
            message.setMessageDirection(Message.MessageDirection.RECEIVE);
        }
        message.setMessageId(msgid);
        message.setUId(uid);
        message.setSentTime(Long.parseLong(sentTime));
        return message;
    }


    /***
     * 初始化聊天的信息
     * @param chatInputView
     * @param curChatInfo
     */
    @ReactProp(name = "chatInfo")
    public void setChatInfo(ChatView chatInputView, ReadableMap curChatInfo) {
        this.chatType = curChatInfo.hasKey("chattype") ? curChatInfo.getString("chattype") : "none";
        if (chatType == "none")
            return;
        if ("priv".equals(chatType))
            this.mConversationType = ConversationType.PRIVATE;
        else if ("group".equals(chatType))
            this.mConversationType = ConversationType.GROUP;
        String targetId = curChatInfo.hasKey("targetid") ? curChatInfo.getString("targetid") : "";
        this.mTargetId = targetId;
        //设置会话
        chatInputView.getEditExtension().setConversation(this.mConversationType, this.mTargetId);
        //senduserid--发送者的id sendname--发送者的昵称 portraitUri--头像地址
        String userid = curChatInfo.hasKey("userid") ? curChatInfo.getString("userid") : "";
        String name = curChatInfo.hasKey("name") ? curChatInfo.getString("name") : "";
        String portraitUri = curChatInfo.hasKey("portraitUri") ? curChatInfo.getString("portraitUri") : "";
        if (curUserInfo == null)
            curUserInfo = new UserInfo(userid, name, Uri.parse(portraitUri));
        else {
            curUserInfo.setUserId(userid);
            curUserInfo.setName(name);
            curUserInfo.setPortraitUri(Uri.parse(portraitUri));
        }
        switch (this.mConversationType) {
            //none
            case NONE:
                break;
            //private
            case PRIVATE:
                break;
            //discussion
            case DISCUSSION:
                break;
            //group
            case GROUP:
                break;
        }
    }

    public WritableMap savePicToTemp(Bitmap bitmap) {
        WritableMap result = null;
        try {
            Log.d("RNImagePicker", "结束读取Md5输入流");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] buf = baos.toByteArray();
            String hash = getMD5(buf);
            baos.close();
            Log.d("RNImagePicker", "结束读取Md5输入流");
            File tempFile = createTempFile(hash);
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile())
                    return null;
                tempFile.deleteOnExit();
                OutputStream os = new FileOutputStream(tempFile);
                //baos.writeTo(os);
                Log.d("RNImagePicker", "开始bitmap.compress");
                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.write(buf);
                os.flush();
                os.close();
                Log.d("RNImagePicker", "结束bitmap.compress");
            }

            String fileuri = Uri.fromFile(tempFile).toString();
            result = new WritableNativeMap();
            result.putString("uri", fileuri);
            result.putString("md5", hash);
            result.putString("type", "image/png");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * 将文件转成base64 字符串
     *
     * @param path
     * @return *
     * @throws Exception
     */

    public String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    public void decoderBase64File(String base64Code, String savePath) throws Exception {
        File file = new File(savePath);
        if (file.exists())
            return;
        file.createNewFile();//新建文件
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = new FileOutputStream(file);
        out.write(buffer);
        out.close();
    }

    public static String getMD5(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".png";
    }

    public static File createTempFile(String hash) {
        String tmpDir = System.getProperty("java.io.tmpdir", ".");
        File tmpDirFile = new File(tmpDir);
        String fileName = null;
        if (hash != null) {
            fileName = hash + ".png";
        } else {
            fileName = getPhotoFileName();
        }

        File tempFile = new File(tmpDirFile, fileName);
        return tempFile;
    }

    /***
     * generate unique msgId
     * @return
     */
    public String genMsguid() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return getMD5(uuid.getBytes());
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
                "sendVoiceMsg", SEND_VOICE_MSG,
                "sendPicMsg", SEND_PIC_MSG,
                "sendRichTextMsg", SEND_RICHTEXT_MSG,
                "sendLocationMsg", SEND_LOCATION_MSG,
                "updateMsgStatus", UPDATE_MSG_STATUS,
                "getHistoryMessage", GET_HISTORY_MESSAGE
        );
    }

    @Override
    public void receiveCommand(ChatView chatView, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case SEND_TEXT_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    if (msg == null)
                        return;
                    Message message = receivedMessageFromJS(msg);
                    //使用EventBus--订阅模式 发送消息到UI线程
                    EventBus.getDefault().post(message);
                }
                break;
            case SEND_VOICE_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    if (msg == null)
                        return;
                    Message message = receivedMessageFromJS(msg);
                    //使用EventBus--订阅模式 发送消息到UI线程
                    EventBus.getDefault().post(message);
                }
                break;
            case SEND_PIC_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    if (msg == null)
                        return;
                    Message message = receivedMessageFromJS(msg);
                    //使用EventBus--订阅模式 发送消息到UI线程
                    EventBus.getDefault().post(message);
                }
                break;
            case SEND_RICHTEXT_MSG:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    if (msg == null)
                        return;
                    Message message = receivedMessageFromJS(msg);
                    //使用EventBus--订阅模式 发送消息到UI线程
                    EventBus.getDefault().post(message);
                }
                break;
            case UPDATE_MSG_STATUS:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    String msgStatus = msg.hasKey("status") ? msg.getString("status") : "";
                    String uid = msg.hasKey("uid") ? msg.getString("uid") : "";
                    int msgid = msg.hasKey("msgid") ? msg.getInt("msgid") : 0;
                    ReadReceiptMessage readReceiptMessage = new ReadReceiptMessage(uid);
                    readReceiptMessage.setLastMessageSendTime(Calendar.getInstance().getTimeInMillis());
                    Message message = Message.obtain(this.mTargetId, this.mConversationType, readReceiptMessage);
                    message.setUId(uid);
                    message.setMessageId(msgid);
                    message.setMessageDirection(Message.MessageDirection.RECEIVE);
                    //使用EventBus--订阅模式 发送消息到UI线程
                    EventBus.getDefault().post(new ReadReceiptEvent(message));
                }
                break;
            case GET_HISTORY_MESSAGE:
                if (args != null && args.size() > 0) {
                    ReadableMap msg = args.getMap(0);
                    if (msg == null)
                        return;
                    ReadableArray readableArray = msg.getArray("historyMessage");
                    if (readableArray.size() == 0)
                        return;
                    if (mChatView != null) {
                        List<Message> messageList = new ArrayList<>();
                        for (int i = readableArray.size() - 1; i >= 0; i--) {
                            //处理消息
                            Message message = receivedMessageFromJS(readableArray.getMap(i));
                            messageList.add(message);
                        }
                        mChatView.getHistoryMessage(ConversationType.PRIVATE, mTargetId, 30, 1, messageList);
                    }
                }
                break;
        }
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
}
