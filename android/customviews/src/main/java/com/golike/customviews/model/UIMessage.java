package com.golike.customviews.model;

import android.text.SpannableStringBuilder;

import com.golike.customviews.emoticon.AndroidEmoji;
import com.golike.customviews.model.Conversation.ConversationType;
/**
 * Created by admin on 2017/8/14.
 */

public class UIMessage {
    private SpannableStringBuilder textMessageContent;
    private UserInfo mUserInfo;
    private int mProgress;
    private boolean evaluated = false;
    private boolean isHistoryMessage = true;
    private Message mMessage;
    private boolean mNickName;
    private boolean isListening;
    public boolean continuePlayAudio;


    public UIMessage() {
    }


    public boolean isListening() {
        return this.isListening;
    }

    public void setListening(boolean listening) {
        this.isListening = listening;
    }

    public boolean isNickName() {
        return this.mNickName;
    }

    public void setNickName(boolean nickName) {
        this.mNickName = nickName;
    }

    public Message getMessage() {
        return this.mMessage;
    }

    public void setMessage(Message message) {
        this.mMessage = message;
    }

    public void setReceivedStatus(Message.ReceivedStatus receivedStatus) {
        this.mMessage.setReceivedStatus(receivedStatus);
    }

    public void setSentStatus(Message.SentStatus sentStatus) {
        this.mMessage.setSentStatus(sentStatus);
    }

    public void setReceivedTime(long receivedTime) {
        this.mMessage.setReceivedTime(receivedTime);
    }

    public void setSentTime(long sentTime) {
        this.mMessage.setSentTime(sentTime);
    }


    public void setExtra(String extra) {
        this.mMessage.setExtra(extra);
    }

    public void setSenderUserId(String senderUserId) {
        this.mMessage.setSenderUserId(senderUserId);
    }


    public String getUId() {
        return this.mMessage.getUId();
    }

    public ConversationType getConversationType() {
        return this.mMessage.getConversationType();
    }

    public String getTargetId() {
        return this.mMessage.getTargetId();
    }

    public int getMessageId() {
        return this.mMessage.getMessageId();
    }

    public Message.MessageDirection getMessageDirection() {
        return this.mMessage.getMessageDirection();
    }

    public String getSenderUserId() {
        return this.mMessage.getSenderUserId();
    }

    public Message.ReceivedStatus getReceivedStatus() {
        return this.mMessage.getReceivedStatus();
    }

    public Message.SentStatus getSentStatus() {
        return this.mMessage.getSentStatus();
    }

    public long getReceivedTime() {
        return this.mMessage.getReceivedTime();
    }

    public long getSentTime() {
        return this.mMessage.getSentTime();
    }

    public String getObjectName() {
        return this.mMessage.getObjectName();
    }


    public String getExtra() {
        return this.mMessage.getExtra();
    }


    public static UIMessage obtain(Message message) {
        UIMessage uiMessage = new UIMessage();
        uiMessage.mMessage = message;
        uiMessage.continuePlayAudio = false;
        return uiMessage;
    }

    public MessageContent getContent() {
        return this.mMessage.getContent();
    }

    public SpannableStringBuilder getTextMessageContent() {
        if(this.textMessageContent == null) {
            MessageContent content = this.mMessage.getContent();
            if(content instanceof TextMessage) {
                TextMessage textMessage = (TextMessage)content;
                if(textMessage.getContent() != null) {
                    SpannableStringBuilder spannable = new SpannableStringBuilder(textMessage.getContent());
                    AndroidEmoji.ensure(spannable);
                    this.setTextMessageContent(spannable);
                }
            }
        }

        return this.textMessageContent;
    }


    public void setTextMessageContent(SpannableStringBuilder textMessageContent) {
        this.textMessageContent = textMessageContent;
    }

    public UserInfo getUserInfo() {
        return this.mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    public boolean getEvaluated() {
        return this.evaluated;
    }

    public void setIsHistoryMessage(boolean isHistoryMessage) {
        this.isHistoryMessage = isHistoryMessage;
    }

    public boolean getIsHistoryMessage() {
        return this.isHistoryMessage;
    }
}

